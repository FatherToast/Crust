package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.RegistryEnvironment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

/**
 * Biomes: {@link net.minecraft.world.level.biome.Biomes}.
 * <p>
 * Biome tags: {@link net.minecraft.tags.BiomeTags} & {@link net.minecraftforge.common.Tags.Biomes}.
 */
public class BiomeEnvironment extends RegistryEnvironment<Biome> {
    
    public static final IRegWrapper<Biome> REGISTRY = IRegWrapper.forKey( Registries.BIOME );
    
    /** @return A new environment based on the resource location. */
    public static BiomeEnvironment of( String resLoc, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the resource location. */
    public static BiomeEnvironment of( ResourceLocation resLoc, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the registry object. */
    public static BiomeEnvironment of( RegistryObject<? extends Biome> regObj, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.of( REGISTRY, regObj, false ), invert );
    }
    
    /** @return A new environment based on the resource key. */
    public static BiomeEnvironment of( ResourceKey<? extends Biome> resKey, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.of( REGISTRY, resKey, false ), invert );
    }
    
    /**
     * @return A new environment based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static BiomeEnvironment of( Biome obj, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.of( REGISTRY, obj, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the partial resource location. */
    public static BiomeEnvironment ofWildcard( ResourceLocation partialResLoc, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofWildcard( REGISTRY, partialResLoc, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace. */
    public static BiomeEnvironment ofWildcard( String namespace, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, "", false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace and partial path. */
    public static BiomeEnvironment ofWildcard( String namespace, String partialPath, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static BiomeEnvironment ofTag( String resLoc, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static BiomeEnvironment ofTag( ResourceLocation resLoc, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag key. */
    public static BiomeEnvironment ofTag( TagKey<? extends Biome> tag, boolean invert ) {
        return new BiomeEnvironment( RegObjKey.ofTag( REGISTRY, tag, false ), invert );
    }
    
    
    public BiomeEnvironment( RegObjKey<Biome> key, boolean invert ) { super( key, invert ); }
    
    public BiomeEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The registry used. */
    @Override
    public IRegWrapper<Biome> getRegistry() { return REGISTRY; }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Biome getActual( EnvironmentContext context ) {
        return context.getBlockPos() == null ? null : context.getLevel().getBiome( context.getBlockPos() ).value();
    }
}