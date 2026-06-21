package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestModEventHandler {
    
    @SubscribeEvent
    static void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( () -> {
            TestCrust.CONFIG.SPEC.initialize();
            TestCrust.README.SPEC.initialize();
        } );
    }
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    @SubscribeEvent
    static void createAttributes( EntityAttributeCreationEvent event ) {
        event.put( TestCrustObjects.Obj.TEST_SKELETON.get(),
                TestCrust.CONFIG.GENERAL.attributeOpListField.build( AbstractSkeleton.createAttributes() ) );
    }
}