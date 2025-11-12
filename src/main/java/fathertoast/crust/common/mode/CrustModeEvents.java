package fathertoast.crust.common.mode;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.config.CrustConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public final class CrustModeEvents {
    
    public static final UUID SUPER_SPEED_UUID = UUID.fromString( "B9766B69-9569-4202-BC1F-2EE2A276D836" );
    
    /** Called when an entity dies. */
    @SubscribeEvent
    static void onLivingDeath( LivingDeathEvent event ) {
        if( event.getEntity() instanceof Player player ) {
            if( CrustModes.UNDYING.enabled( player ) ) {
                player.setHealth( player.getMaxHealth() );
                // Also stop non-temporary damaging effects
                player.setAirSupply( Math.max( player.getAirSupply(), player.getMaxAirSupply() ) );
                FoodData playerFood = player.getFoodData();
                playerFood.setFoodLevel( Math.max( playerFood.getFoodLevel(), 6 ) );
                event.setCanceled( true );
            }
        }
    }
    
    /** Called when a player touches an item entity. */
    @SubscribeEvent
    static void onItemPickup( EntityItemPickupEvent event ) {
        Player player = event.getEntity();
        if( CrustModes.DESTROY_ON_PICKUP.enabled( player ) ) {
            event.getItem().setDefaultPickUpDelay();
            event.getItem().discard();
            // noinspection resource
            player.level().playSound( null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4F + 2.0F );
            
            event.setResult( Event.Result.DENY );
            event.setCanceled( true );
        }
    }
    
    /** Called each integrated/dedicated server tick. */
    @SubscribeEvent
    static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.START ) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if( server != null ) {
                for( ServerLevel level : server.getAllLevels() ) tickMagnetMode( level, level::getAllEntities );
            }
        }
    }
    
    /**
     * Called each player tick. Client event is handled in
     * {@link fathertoast.crust.client.mode.CrustModeClientEvents#onPlayerTick(TickEvent.PlayerTickEvent)}.
     */
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase != TickEvent.Phase.END || event.side.isClient() ) return;
        
        Player player = event.player;
        CrustModesData playerModes = CrustModesData.of( player );
        
        // Various timers
        int clock32 = player.tickCount & 0b1_1111;
        int clock16 = clock32 & 0b1111;
        int clock4 = clock32 & 0b11;
        
        // Super speed
        if( clock4 == 3 ) {
            AttributeInstance moveSpeed = player.getAttribute( Attributes.MOVEMENT_SPEED );
            AttributeInstance swimSpeed = player.getAttribute( ForgeMod.SWIM_SPEED.get() );
            if( moveSpeed != null ) {
                moveSpeed.removeModifier( SUPER_SPEED_UUID );
                if( swimSpeed != null ) swimSpeed.removeModifier( SUPER_SPEED_UUID );
                if( player.isSprinting() && playerModes.enabled( CrustModes.SUPER_SPEED ) ) {
                    moveSpeed.addTransientModifier( new AttributeModifier( SUPER_SPEED_UUID,
                            "Super speed mode", playerModes.get( CrustModes.SUPER_SPEED ) - 1.0F,
                            AttributeModifier.Operation.MULTIPLY_TOTAL ) );
                    if( swimSpeed != null ) swimSpeed.addTransientModifier( new AttributeModifier( SUPER_SPEED_UUID,
                            "Super speed mode", playerModes.get( CrustModes.SUPER_SPEED ) - 1.0F,
                            AttributeModifier.Operation.MULTIPLY_TOTAL ) );
                }
            }
        }
        
        // Uneating
        if( clock16 == 5 && playerModes.enabled( CrustModes.UNEATING ) ) {
            int minimum = playerModes.get( CrustModes.UNEATING );
            FoodData foodData = player.getFoodData();
            if( foodData.getFoodLevel() < minimum ) foodData.eat( 20, 0.125F );
        }
        
        // Super vision
        else if( clock16 == 13 && playerModes.enabled( CrustModes.SUPER_VISION ) ) {
            if( !player.hasEffect( MobEffects.NIGHT_VISION ) ) {
                player.addEffect( new MobEffectInstance( MobEffects.NIGHT_VISION, -1,
                        0, true, false, false ) );
            }
            // Not needed, but looks nicer this way
            if( player.hasEffect( MobEffects.BLINDNESS ) ) player.removeEffect( MobEffects.BLINDNESS );
        }
        
        // Unbreaking
        if( clock32 == 7 && playerModes.enabled( CrustModes.UNBREAKING ) ) {
            // Would be nice if we can generally enable infinite items instead; like 'player.abilities.instabuild'
            for( int s = 0; s < player.getInventory().getContainerSize(); s++ ) {
                ItemStack item = player.getInventory().getItem( s );
                if( !item.isEmpty() && item.isDamaged() ) item.setDamageValue( 0 );
            }
        }
    }
    
    
    /** Called each tick start for each world, on both the client and server side. */
    public static void tickMagnetMode( Level level, Supplier<Iterable<Entity>> entityGetter ) {
        // Skip every other tick if needed
        if( !CrustConfig.MODES.MAGNET.smooth.get() && (level.getGameTime() & 1) == 0 ) return;
        
        // Build a list of all players with magnet mode
        final List<CrustModesData> allMagnetModeData = new ArrayList<>();
        for( Player player : level.players() ) {
            if( !player.isSpectator() && player.isAlive() ) {
                CrustModesData playerModes = CrustModesData.of( player );
                if( playerModes.enabled( CrustModes.MAGNET ) ) allMagnetModeData.add( playerModes );
            }
        }
        if( allMagnetModeData.isEmpty() ) return;
        
        // Iterate over all entities in the level
        for( Entity entity : entityGetter.get() ) {
            // Check if the entity is a pullable dropped item
            //  Actual pickup delay is not available on the client, so we use tick count instead
            if( entity.isAlive() && entity.tickCount > CrustConfig.MODES.MAGNET.delay.get() &&
                    entity instanceof ItemEntity item && !item.getItem().isEmpty() ) {
                // Find closest player that can pull this item
                CrustModesData closest = null;
                float closestRangeSq = Float.MAX_VALUE;
                double closestDistSq = Float.MAX_VALUE;
                for( CrustModesData modes : allMagnetModeData ) {
                    float maxRange = modes.get( CrustModes.MAGNET );
                    float rangeSqr = maxRange * maxRange;
                    double distSq = modes.getOwner().distanceToSqr( item );
                    if( distSq < closestDistSq && distSq <= rangeSqr && hasSpaceFor( modes.getOwner(), item.getItem() ) ) {
                        closest = modes;
                        closestRangeSq = rangeSqr;
                        closestDistSq = distSq;
                    }
                }
                // If a player is found, pull the item
                if( closest != null ) {
                    magnetPullItem( item, closest.getOwner(),
                            (closestRangeSq - closestDistSq) / closestRangeSq );
                }
            }
        }
    }
    
    /** @return True if the player has inventory space to pick up at least some of the item stack. */
    private static boolean hasSpaceFor( Player player, ItemStack item ) {
        return player.getInventory().getFreeSlot() >= 0 || player.getInventory().getSlotWithRemainingSpace( item ) >= 0;
    }
    
    /** Applies magnet pull velocity to the item. */
    private static void magnetPullItem( ItemEntity item, Player player, double power ) {
        item.setDeltaMovement( player.getEyePosition( 1.0F ).subtract( item.position() ).normalize()
                .scale( power * CrustConfig.MODES.MAGNET.maxSpeed.get() ).add( 0.0, 0.04, 0.0 ) );
    }
    
    
    // Static listener, no instantiation
    private CrustModeEvents() { }
}