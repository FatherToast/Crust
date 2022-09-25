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