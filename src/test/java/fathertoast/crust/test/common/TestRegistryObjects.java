package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.test.common.game_objects.TestBlock;
import fathertoast.crust.test.common.game_objects.TestBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, modid = ICrustApi.MOD_ID )
public class TestRegistryObjects {


    // Misc reg objects
    public static final RegistryObject<Block> TEST_BLOCK = Crust.BLOCK_REGISTRY.register("test", TestBlock::new);
    public static final RegistryObject<Item> TEST_BLOCK_ITEM = Crust.ITEM_REGISTRY.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<TestBlockEntity>> TEST_BE = Crust.BE_REGISTRY.register("test", () -> BlockEntityType.Builder.of(TestBlockEntity::new, TEST_BLOCK.get()).build(null));


    @SubscribeEvent
    public static void onNewRegistries(NewRegistryEvent event) {
        // Nothing to do here at the moment, just class loading
    }
}
