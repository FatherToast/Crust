package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class CrustItems {
    
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, ICrustApi.MOD_ID );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an item to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<Item> regObj, Supplier<Item> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    /** Registers a simple block item for the given block. */
    @SuppressWarnings( "SameParameterValue" )
    protected static void registerBlockItem( RegistryObject<Block> regObj ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> new BlockItem( regObj.get(), new Item.Properties() ) );
    }
    
    // Utility class
    private CrustItems() { }
}
