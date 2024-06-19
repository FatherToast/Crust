package fathertoast.crust.common.core;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public class CrustForgeEvents {
    
    /** Set of all players that have had their Crust modes changed recently. */
    private static final Set<ServerPlayer> NEED_CRUST_MODE_UPDATE = new HashSet<>();
    
    private static int updateCounter;
    
    /** Queues the player for a Crust mode update to notify the client of changes. */
    public static void markModesDirty( Player player ) {
        NEED_CRUST_MODE_UPDATE.add( (ServerPlayer) player );
    }
    
    /** Called when an entity is spawned/added into the world. */
    @SubscribeEvent
    static void onEntityJoinWorld( EntityJoinLevelEvent event ) {
        if( !event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer serverPlayer ) {
            markModesDirty( serverPlayer );
        }
    }
    
    /** Called each server tick. */
    @SubscribeEvent
    static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            updateCounter++;
            if( updateCounter >= 3 ) {
                updateCounter = 0;
                if( !NEED_CRUST_MODE_UPDATE.isEmpty() ) {
                    for( ServerPlayer player : NEED_CRUST_MODE_UPDATE ) {
                        CrustPacketHandler.sendCrustModesUpdate( player );
                    }
                    NEED_CRUST_MODE_UPDATE.clear();
                }
            }
        }
    }
    
    /** Called when an entity is taking damage. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingHurt( LivingHurtEvent event ) {
        Level level = event.getEntity().level();

        if( event.getEntity() != null && event.getSource().type() != level.damageSources().fellOutOfWorld().type() && !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS) &&
                event.getEntity().hasEffect( CrustObjects.vulnerability() ) ) {
            
            final MobEffectInstance vulnerability = event.getEntity().getEffect( CrustObjects.vulnerability() );
            if( vulnerability == null ) return;
            
            // Take 25% more damage per effect level (vs. Damage Resistance's 20% less per level)
            event.setAmount( Math.max( event.getAmount() * (1.0F + 0.25F * (vulnerability.getAmplifier() + 1)), 0.0F ) );
        }
    }
    
    /** Called when an entity lands on the ground. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingFall( LivingFallEvent event ) {
        if( event.getEntity() != null && event.getEntity().hasEffect( CrustObjects.weight() ) ) {
            
            final MobEffectInstance weight = event.getEntity().getEffect( CrustObjects.weight() );
            if( weight == null ) return;
            
            // Increase effective fall distance by ~33% per effect level
            event.setDamageMultiplier( event.getDamageMultiplier() * (1.0F + 0.3334F * (weight.getAmplifier() + 1)) );
        }
    }
}