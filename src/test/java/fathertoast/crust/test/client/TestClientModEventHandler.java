package fathertoast.crust.test.client;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.test.common.TestCrustObjects;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestClientModEventHandler {
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        TestClientForgeEventHandler.register();
    }
    
    /** Registers this mod's additional key bindings. */
    @SubscribeEvent
    static void onRegisterKeyMappings( RegisterKeyMappingsEvent event ) {
        TestClientForgeEventHandler.registerKeyBindings( event );
    }
    
    @SubscribeEvent
    public static void buildCreativeContents( BuildCreativeModeTabContentsEvent event ) {
        if( event.getTabKey() == CreativeModeTabs.SEARCH ) {
            for( RegistryObject<Item> item : TestCrustObjects.Reg.ITEMS.getEntries() ) {
                event.accept( item.get() );
            }
        }
        if( event.getTabKey() == TestCrustObjects.Obj.TEST_TAB.key() ) {
            for( RegistryObject<Item> item : TestCrustObjects.Reg.ITEMS.getEntries() ) {
                event.accept( item.get() );
            }
        }
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        event.registerEntityRenderer( TestCrustObjects.Obj.TEST_SKELETON.get(), SkeletonRenderer::new );
    }
}