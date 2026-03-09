package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustEntityHelper;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.test.common.block.TestBlock;
import fathertoast.crust.test.common.block.TestBlockEntity;
import fathertoast.crust.test.common.entity.TestSkeleton;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, modid = ICrustApi.MOD_ID )
public class TestCrustObjects {
    // Registries
    public interface Reg {
        DeferredRegister<CreativeModeTab> CREATIVE_TABS = reg( Registries.CREATIVE_MODE_TAB );
        DeferredRegister<Block> BLOCKS = reg( ForgeRegistries.BLOCKS );
        DeferredRegister<Item> ITEMS = reg( ForgeRegistries.ITEMS );
        DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = reg( ForgeRegistries.BLOCK_ENTITY_TYPES );
        DeferredRegister<EntityType<?>> ENTITIES = reg( ForgeRegistries.ENTITY_TYPES );
    }
    
    // Registry objects
    public interface Obj {
        // Creative tab
        CreativeTabRegObj TEST_TAB = registerTab( "test_tab", () -> CreativeModeTab.builder()
                .icon( () -> new ItemStack( Blocks.ANVIL ) )
                .title( Component.translatable( "itemGroup." + ICrustApi.MOD_ID + ".test" ) )
                .build() );
        
        // Test block
        RegistryObject<Block> TEST_BLOCK = Reg.BLOCKS.register( "test_block", TestBlock::new );
        @SuppressWarnings( "unused" )
        RegistryObject<Item> TEST_BLOCK_ITEM = Reg.ITEMS.register( "test_block_item", () ->
                new BlockItem( TEST_BLOCK.get(), new Item.Properties() ) );
        @SuppressWarnings( "DataFlowIssue" )
        RegistryObject<BlockEntityType<TestBlockEntity>> TEST_BE = Reg.BLOCK_ENTITIES.register( "test_block_entity", () ->
                BlockEntityType.Builder.of( TestBlockEntity::new, TEST_BLOCK.get() )
                        .build( null ) );
        
        // Test skeleton
        RegistryObject<EntityType<TestSkeleton>> TEST_SKELETON = registerEntity( "test_skeleton",
                CrustEntityHelper.monsterType( TestSkeleton::new, 0.6F, 1.99F ) );
        @SuppressWarnings( "unused" )
        RegistryObject<ForgeSpawnEggItem> TEST_SKELETON_SPAWN_EGG = registerSpawnEgg(
                TEST_SKELETON, 0xC1C1C1, 0x494949 );
    }
    
    /** Called when the mod is constructed. */
    @SubscribeEvent
    static void onConstructMod( FMLConstructModEvent event ) {
        IEventBus bus = Crust.INSTANCE.container.getEventBus();
        Reg.CREATIVE_TABS.register( bus );
        Reg.BLOCKS.register( bus );
        Reg.ITEMS.register( bus );
        Reg.BLOCK_ENTITIES.register( bus );
        Reg.ENTITIES.register( bus );
        
        //noinspection ResultOfMethodCallIgnored
        Obj.TEST_BLOCK.getId(); // Load class
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private static <T> DeferredRegister<T> reg( ResourceKey<? extends Registry<T>> reg ) {
        return DeferredRegister.create( reg, ICrustApi.MOD_ID );
    }
    
    private static <T> DeferredRegister<T> reg( IForgeRegistry<T> reg ) {
        return DeferredRegister.create( reg, ICrustApi.MOD_ID );
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private static CreativeTabRegObj registerTab( String name, Supplier<CreativeModeTab> supplier ) {
        return new CreativeTabRegObj( Reg.CREATIVE_TABS.register( name, supplier ),
                ResourceKey.create( Registries.CREATIVE_MODE_TAB, Crust.rl( name ) ) );
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity( String name, EntityType.Builder<T> builder ) {
        return Reg.ENTITIES.register( name, () -> builder.build( name ) );
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends Mob> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(
            RegistryObject<EntityType<T>> entityType, int eggBaseColor, int eggSpotsColor ) {
        final String name = Objects.requireNonNull( entityType.getId() ).getPath() + "_spawn_egg";
        return Reg.ITEMS.register( name, () -> new ForgeSpawnEggItem( entityType,
                eggBaseColor, eggSpotsColor, new Item.Properties() )
        );
    }
    
    public record CreativeTabRegObj(RegistryObject<CreativeModeTab> regObj, ResourceKey<CreativeModeTab> key) { }
}