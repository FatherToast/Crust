package fathertoast.crust.common.mode;

import fathertoast.crust.common.core.Crust;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
    
    /** Called each player tick. */
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.side.isServer() && event.phase == TickEvent.Phase.END ) {
            PlayerEntity player = event.player;
            CrustModesData playerModes = CrustModesData.of( player );
            
            // Would be nice if we can generally enable infinite items like 'player.abilities.instabuild'
            if( playerModes.enabled( CrustModes.UNBREAKING ) ) {
                int index = player.tickCount % player.inventory.getContainerSize();
                ItemStack item = player.inventory.getItem( index );
                if( !item.isEmpty() && item.isDamaged() ) {
                    item.setDamageValue( 0 );
                }
            }
            
            //            if( playerModes.enabled( CrustModes.UNEATING ) && (player.tickCount & 0b11111) == 1 ) { // ~1.5 sec
            //                float minimum = playerModes.get( CrustModes.UNEATING );
            //                FoodStats hunger = player.getFoodData();
            //                if( hunger.getFoodLevel() < Math.min( (int) minimum, 20 ) || hunger.getSaturationLevel() < minimum - 20.0F ) {
            //                    hunger.eat( 20, 1.0F );
            //                }
            //            }
        }
    }
}