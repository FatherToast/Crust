package fathertoast.crust.api.lib;

import com.mojang.brigadier.arguments.ArgumentType;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.entity.CrustFishingHook;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This helper class contains references/getters for all registry objects provided by Crust.
 */
public final class CrustObjects {
    /**
     * The Forge registry for Crust portal builders.<br>
     * Populated during {@link net.minecraftforge.registries.NewRegistryEvent}.
     */
    public static Supplier<IForgeRegistry<PortalBuilder>> PORTAL_REGISTRY;
    
    /** The portal builders provided by Crust. */
    public interface Portals {
        /** The portal builder registry's key. Can be used to register new portals and create object holders. */
        ResourceKey<Registry<PortalBuilder>> REGISTRY_KEY = ResourceKey.createRegistryKey( rl( "portal_builder" ) );
        
        RegistryObject<PortalBuilder> NETHER = portal( "nether_portal" );
        RegistryObject<PortalBuilder> END = portal( "end_portal" );
    }
    
    public interface Blocks {
        RegistryObject<Block> FEATURE_GENERATOR = block( "feature_generator" );
    }
    
    public interface BlockEntities {
        RegistryObject<BlockEntityType<?>> FEATURE_GENERATOR = blockEntity( Blocks.FEATURE_GENERATOR );
    }
    
    /** The mob effects provided by Crust. */
    public interface Effects {
        RegistryObject<MobEffect> VULNERABILITY = effect( "vulnerability" );
        RegistryObject<MobEffect> WEIGHT = effect( "weight" );
    }
    
    /** The entity types provided by Crust. */
    public interface Entities {
        RegistryObject<EntityType<CrustFishingHook>> FISH_HOOK = entity( "fishing_bobber" );
    }
    
    /** The structure processor types provided by Crust. */
    public interface StructureProcessors {
        RegistryObject<StructureProcessorType<?>> FEATURE_GEN_ACTIVATOR = structureProcessor( "feature_gen_activator" );
    }
    
    /** The integer provider types provided by Crust. */
    public interface IntProviders {
        RegistryObject<IntProviderType<?>> CFG_CONSTANT = intProvider( "config_constant" );
        RegistryObject<IntProviderType<?>> CFG_UNIFORM = intProvider( "config_uniform" );
        RegistryObject<IntProviderType<?>> CFG_COUNT = intProvider( "config_count" );
    }
    
    /** The float provider types provided by Crust. */
    public interface FloatProviders {
        RegistryObject<FloatProviderType<?>> CFG_CONSTANT = floatProvider( "config_constant" );
        RegistryObject<FloatProviderType<?>> CFG_UNIFORM = floatProvider( "config_uniform" );
    }
    
    /** The height provider types provided by Crust. */
    public interface HeightProviders {
        RegistryObject<HeightProviderType<?>> CFG_UNIFORM = heightProvider( "config_uniform" );
    }
    
    /** The command argument types provided by Crust. */
    public interface CommandArguments {
        RegistryObject<ArgumentTypeInfo<ArgumentType<PortalBuilder>, ?>> PORTAL_TYPE = cmdArg( "portal_type" );
    }
    
    
    // ---- Internal Methods ---- //
    
    /** @return An object holder for a portal builder. */
    private static RegistryObject<PortalBuilder> portal( String name ) { return ro( name, Portals.REGISTRY_KEY ); }
    
    /** @return An object holder for a mob effect. */
    private static RegistryObject<MobEffect> effect( String name ) { return ro( name, ForgeRegistries.MOB_EFFECTS ); }
    
    /** @return An object holder for a block. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<Block> block( String name ) { return ro( name, ForgeRegistries.BLOCKS ); }
    
    /** @return An object holder for a block entity type. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<BlockEntityType<?>> blockEntity( String name ) { return ro( name, ForgeRegistries.BLOCK_ENTITY_TYPES ); }
    
    /**
     * @return An object holder for a block entity type,
     * using the same ID as the given block registry object.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<BlockEntityType<?>> blockEntity( RegistryObject<Block> regObj ) {
        return blockEntity( Objects.requireNonNull( regObj.getId() ).getPath() );
    }
    
    /** @return An object holder for an entity type. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends Entity> RegistryObject<EntityType<T>> entity( String name ) { return ro( name, ForgeRegistries.ENTITY_TYPES ); }
    
    /** @return An object holder for a structure processor type. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<StructureProcessorType<?>> structureProcessor( String name ) { return ro( name, Registries.STRUCTURE_PROCESSOR ); }
    
    /** @return An object holder for an integer provider type. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<IntProviderType<?>> intProvider( String name ) { return ro( name, Registries.INT_PROVIDER_TYPE ); }
    
    /** @return An object holder for a float provider type. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<FloatProviderType<?>> floatProvider( String name ) { return ro( name, Registries.FLOAT_PROVIDER_TYPE ); }
    
    /** @return An object holder for a height provider type. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<HeightProviderType<?>> heightProvider( String name ) { return ro( name, Registries.HEIGHT_PROVIDER_TYPE ); }
    
    /** @return An object holder for a command argument type. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends ArgumentType<?>> RegistryObject<ArgumentTypeInfo<T, ?>> cmdArg( String name ) { return ro( name, ForgeRegistries.COMMAND_ARGUMENT_TYPES ); }
    
    /** @return An object holder for a Forge registry object. */
    private static <R, T extends R> RegistryObject<T> ro( String name, IForgeRegistry<R> reg ) {
        return RegistryObject.create( rl( name ), reg );
    }
    
    /** @return An object holder for a custom registry object. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T> RegistryObject<T> ro( String name, ResourceKey<? extends Registry<T>> registryKey ) {
        return RegistryObject.createOptional( rl( name ), registryKey, ICrustApi.MOD_ID );
    }
    
    /** @return A resource location. */
    private static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, path );
    }
    
    
    // Utility class
    private CrustObjects() { }
}