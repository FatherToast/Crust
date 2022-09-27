package fathertoast.crust.common.mode;

import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.core.Crust;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID )
public class CrustModeEvents {
    
    public static final UUID SUPER_SPEED_UUID = UUID.fromString( "B9766B69-9569-4202-BC1F-2EE2A276D836" );
    
    /** Called when an entity dies. */
    @SubscribeEvent
    static void onLivingDeath( LivingDeathEvent event ) {
        if( event.getEntity() instanceof PlayerEntity ) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            
            if( CrustModes.UNDYING.enabled( player ) ) {
                player.setHealth( player.getMaxHealth() );
                // Also stop non-temporary damaging effects
                player.setAirSupply( Math.max( player.getAirSupply(), player.getMaxAirSupply() ) );
                FoodStats playerFood = player.getFoodData();
                playerFood.setFoodLevel( Math.max( playerFood.getFoodLevel(), 6 ) );
                event.setCanceled( true );
            }
        }
    }
    
    /** Called when a player touches an item entity. */
    @SubscribeEvent
    static void onItemPickup( EntityItemPickupEvent event ) {
        PlayerEntity player = event.getPlayer();
        if( CrustModes.DESTROY_ON_PICKUP.enabled( player ) ) {
            event.getItem().setDefaultPickUpDelay();
            event.getItem().remove();
            player.level.playSound( null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                    (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4F + 2.0F );
            
            event.setResult( Event.Result.DENY );
            event.setCanceled( true );
        }
    }
    
    /**
     * Called each player tick. Client event is handled in
     * {@link fathertoast.crust.client.mode.CrustModeClientEvents#onPlayerTick(TickEvent.PlayerTickEvent)}.
     */
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase != TickEvent.Phase.END || event.side.isClient() ) return;
        
        PlayerEntity player = event.player;
        CrustModesData playerModes = CrustModesData.of( player );
        
        // Various timers
        int clock32 = player.tickCount & 0b1_1111;
        int clock16 = clock32 & 0b1111;
        int clock4 = clock32 & 0b11;
        
        if( (player.tickCount & 1) == 1 && playerModes.enabled( CrustModes.MAGNET ) ) {
            onMagnetTick( player, playerModes.get( CrustModes.MAGNET ) );
        }
        
        if( clock4 == 3 ) {
            ModifiableAttributeInstance moveSpeed = player.getAttribute( Attributes.MOVEMENT_SPEED );
            ModifiableAttributeInstance swimSpeed = player.getAttribute( ForgeMod.SWIM_SPEED.get() );
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
        
        if( clock16 == 5 && playerModes.enabled( CrustModes.UNEATING ) ) {
            int minimum = playerModes.get( CrustModes.UNEATING );
            FoodStats foodData = player.getFoodData();
            if( foodData.getFoodLevel() < minimum ) foodData.eat( 20, 0.125F );
        }
        
        if( clock32 == 7 && playerModes.enabled( CrustModes.UNBREAKING ) ) {
            // Would be nice if we can generally enable infinite items instead; like 'player.abilities.instabuild'
            for( int s = 0; s < player.inventory.getContainerSize(); s++ ) {
                ItemStack item = player.inventory.getItem( s );
                if( !item.isEmpty() && item.isDamaged() ) item.setDamageValue( 0 );
            }
        }
        
    }
    
    /** Called every other tick on each player that has magnet mode enabled. */
    public static void onMagnetTick( PlayerEntity player, float maxRange ) {
        float rangeSqr = maxRange * maxRange;
        for( ItemEntity item : player.level.getEntitiesOfClass( ItemEntity.class,
                player.getBoundingBox().inflate( maxRange ) ) ) {
            if( item.isAlive() && !item.getItem().isEmpty() && item.tickCount > 10 ) {
                double distSq = player.distanceToSqr( item );
                if( distSq < rangeSqr && hasSpaceFor( player, item.getItem() ) ) {
                    magnetPullItem( player, item, (rangeSqr - distSq) / rangeSqr );
                }
            }
        }
    }
    
    /** @return True if the player has inventory space to pick up at least some of the item stack. */
    private static boolean hasSpaceFor( PlayerEntity player, ItemStack item ) {
        return player.inventory.getFreeSlot() >= 0 || player.inventory.getSlotWithRemainingSpace( item ) >= 0;
    }
    
    /** Applies magnet pull velocity to the item. */
    private static void magnetPullItem( PlayerEntity player, ItemEntity item, double power ) {
        item.setDeltaMovement( player.position().subtract( item.position() ).normalize()
                .scale( power * CrustConfig.MODES.MAGNET.maxSpeed.get() ).add( 0.0, 0.04, 0.0 ) );
    }
}