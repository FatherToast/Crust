package fathertoast.crust.common.mode;

import fathertoast.crust.common.core.Crust;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID )
public class CrustModeEvents {
    
    /** Called when an entity dies. */
    @SubscribeEvent
    static void onLivingDeath( LivingDeathEvent event ) {
        if( event.getEntity() instanceof PlayerEntity ) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            
            if( CrustModes.UNDYING.enabled( player ) ) {
                player.setHealth( player.getMaxHealth() );
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
    
    /** Called each player tick. */
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.side.isServer() && event.phase == TickEvent.Phase.END ) {
            PlayerEntity player = event.player;
            CrustModesData playerModes = CrustModesData.of( player );
            
            // Various timers
            int clock32 = player.tickCount & 0b1_1111;
            int clock16 = clock32 & 0b1111;
            
            // Would be nice if we can generally enable infinite items instead; like 'player.abilities.instabuild'
            if( playerModes.enabled( CrustModes.UNBREAKING ) && clock32 == 7 ) {
                for( int s = 0; s < player.inventory.getContainerSize(); s++ ) {
                    ItemStack item = player.inventory.getItem( s );
                    if( !item.isEmpty() && item.isDamaged() ) item.setDamageValue( 0 );
                }
            }
            
            if( playerModes.enabled( CrustModes.UNEATING ) && clock16 == 4 ) {
                int minimum = playerModes.get( CrustModes.UNEATING );
                FoodStats foodData = player.getFoodData();
                if( foodData.getFoodLevel() < minimum ) foodData.eat( 20, 0.125F );
            }
        }
    }
}