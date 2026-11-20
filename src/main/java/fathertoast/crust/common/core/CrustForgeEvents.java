package fathertoast.crust.common.core;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.command.CrustCleanCommand;
import fathertoast.crust.common.command.CrustModeCommand;
import fathertoast.crust.common.command.CrustPortalCommand;
import fathertoast.crust.common.command.CrustRecoverCommand;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.network.CrustPacketHandler;
import fathertoast.crust.common.network.message.clientbound.S2CSimpleRequest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public final class CrustForgeEvents {
    
    /** Set of all players that have had their Crust modes changed recently. */
    private static final Set<ServerPlayer> NEED_CRUST_MODE_UPDATE = new HashSet<>();
    /** Set of all players that wants mob pathfinding data sent to them for debug purposes. */
    private static final Set<ServerPlayer> PATHFINDER_DATA_RECEIVERS = new HashSet<>();
    
    /** An update counter for short cycles. */
    private static int updateCounter;
    /** The update counter for sending pathfinder debug data to clients. */
    private static int pathfinderUpdateCounter;
    
    /** Queues the player for a Crust mode update to notify the client of changes. */
    public static void markModesDirty( Player player ) {
        NEED_CRUST_MODE_UPDATE.add( (ServerPlayer) player );
    }
    
    /** Adds the given player to the set of pathfinder data receivers. */
    public static void addPathfinderDataReceiver( ServerPlayer player ) {
        PATHFINDER_DATA_RECEIVERS.add( player );
    }
    
    /** Removes the given player from the set of pathfinder data receivers. */
    public static void removePathfinderDataReceiver( ServerPlayer player ) {
        PATHFINDER_DATA_RECEIVERS.remove( player );
    }
    
    
    /** Called when an entity is spawned/added into the world. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onEntityJoinWorld( EntityJoinLevelEvent event ) {
        if( !event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer serverPlayer ) {
            markModesDirty( serverPlayer );
        }
    }
    
    /** Called each server tick. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            // Check if any Crust modes needs update every 4 ticks
            if( updateCounter++ >= 4 ) {
                updateCounter = 0;
                
                if( !NEED_CRUST_MODE_UPDATE.isEmpty() ) {
                    for( ServerPlayer player : NEED_CRUST_MODE_UPDATE ) {
                        CrustPacketHandler.sendCrustModesUpdate( player );
                    }
                    NEED_CRUST_MODE_UPDATE.clear();
                }
            }
            if( pathfinderUpdateCounter++ >= CrustConfig.UTILITIES.OPTIONAL_DATA_SHARING.pathfinderDataInterval.get() ) {
                pathfinderUpdateCounter = 0;
                
                // Send navigation path debug packets once a second, if enabled.
                // Don't bother calculating if nobody is asking for the data anyway!
                if( !CrustConfig.UTILITIES.OPTIONAL_DATA_SHARING.pathfinderData.get()
                        || event.getServer().getPlayerList().getPlayers().isEmpty()
                        || PATHFINDER_DATA_RECEIVERS.isEmpty() ) {
                    return;
                }
                for( ServerLevel level : event.getServer().getAllLevels() ) {
                    for( Entity entity : level.getEntities().getAll() ) {
                        // Check if the entity is a pathfinder mob
                        if( !(entity instanceof PathfinderMob mob) )
                            continue;
                        
                        for( ServerPlayer player : level.players() ) {
                            if( PATHFINDER_DATA_RECEIVERS.contains( player ) ) {
                                if( mob.distanceTo( player ) <= CrustConfig.UTILITIES.OPTIONAL_DATA_SHARING.pathfinderDataDistance.get() ) {
                                    CrustPacketHandler.sendDebugPath( player, mob );
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /** Called when an entity is taking damage. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingHurt( LivingHurtEvent event ) {
        // noinspection resource
        Level level = event.getEntity().level();
        
        if( event.getEntity() != null && event.getSource().type() != level.damageSources().fellOutOfWorld().type() && !event.getSource().is( DamageTypeTags.BYPASSES_ENCHANTMENTS ) &&
                event.getEntity().hasEffect( CrustObjects.Effects.VULNERABILITY.get() ) ) {
            
            final MobEffectInstance vulnerability = event.getEntity().getEffect( CrustObjects.Effects.VULNERABILITY.get() );
            if( vulnerability == null ) return;
            
            // Take 25% more damage per effect level (vs. Damage Resistance's 20% less per level)
            event.setAmount( Math.max( event.getAmount() * (1.0F + 0.25F * (vulnerability.getAmplifier() + 1)), 0.0F ) );
        }
    }
    
    /** Called when an entity lands on the ground. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingFall( LivingFallEvent event ) {
        if( event.getEntity() != null && event.getEntity().hasEffect( CrustObjects.Effects.WEIGHT.get() ) ) {
            
            final MobEffectInstance weight = event.getEntity().getEffect( CrustObjects.Effects.WEIGHT.get() );
            if( weight == null ) return;
            
            // Increase effective fall distance by ~33% per effect level
            event.setDamageMultiplier( event.getDamageMultiplier() * (1.0F + 0.3334F * (weight.getAmplifier() + 1)) );
        }
    }
    
    /** Called on the server when a player logs in. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onPlayerLoggedIn( PlayerEvent.PlayerLoggedInEvent event ) {
        if( event.getEntity() instanceof ServerPlayer player ) {
            CrustPacketHandler.sendConfigSync( player );
            CrustPacketHandler.sendSimpleRequest( player, S2CSimpleRequest.Type.TOGGLE_PATHFINDER_DATA_REQUEST );
        }
    }
    
    /** Called on the server when a player logs out. */
    @SubscribeEvent
    public static void onPlayerLoggedOut( PlayerEvent.PlayerLoggedOutEvent event ) {
        if( event.getEntity() instanceof ServerPlayer player ) {
            PATHFINDER_DATA_RECEIVERS.remove( player );
        }
    }
    
    /** Called immediately before shutting down on the dedicated server, and before returning to the main menu on the client. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onServerStopped( ServerStoppedEvent event ) {
        ConfigManager.getAll().forEach( manager -> manager.getConfigs()
                .forEach( file -> file.SPEC.clearSyncData() ) );
    }
    
    /** Called each time commands are loaded. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void registerCommands( RegisterCommandsEvent event ) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CrustCleanCommand.register( dispatcher );
        CrustModeCommand.register( dispatcher );
        CrustPortalCommand.register( dispatcher );
        CrustRecoverCommand.register( dispatcher );
    }
    
    
    // Static listener, no instantiation
    private CrustForgeEvents() { }
}