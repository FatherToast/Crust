package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public class TestGameEventHandler {
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingHurt( LivingHurtEvent event ) {
        // noinspection resource
        if( event.getEntity().level().isClientSide() || !(event.getSource().getEntity() instanceof Player) ) return;
        
        // Test stuff here
        EntityType<?> entityType = event.getEntity().getType();
        Integer value = TestCrust.CONFIG.GENERAL.registryMapField.get( entityType );
        if( value == null ) {
            TestCrust.LOG.debug( "Entity NOT matched: {}", entityType );
        }
        else {
            TestCrust.LOG.debug( "Value = {} for entity: {}", value, entityType );
        }
    }
}