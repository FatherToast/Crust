package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.RegistryEnvironment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

/**
 * Dimensions: {@link LevelStem#OVERWORLD}, {@link LevelStem#NETHER}, and {@link LevelStem#END}.
 */
public class DimensionEnvironment extends RegistryEnvironment<LevelStem> {
    
    public static final IRegWrapper<LevelStem> REGISTRY = IRegWrapper.forKey( Registries.LEVEL_STEM );
    
    /** @return A new environment based on the resource location. */
    public static DimensionEnvironment of( String resLoc, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the resource location. */
    public static DimensionEnvironment of( ResourceLocation resLoc, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the registry object. */
    public static DimensionEnvironment of( RegistryObject<? extends LevelStem> regObj, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.of( REGISTRY, regObj, false ), invert );
    }
    
    /** @return A new environment based on the resource key. */
    public static DimensionEnvironment of( ResourceKey<? extends LevelStem> resKey, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.of( REGISTRY, resKey, false ), invert );
    }
    
    /**
     * @return A new environment based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static DimensionEnvironment of( LevelStem obj, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.of( REGISTRY, obj, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the partial resource location. */
    public static DimensionEnvironment ofWildcard( ResourceLocation partialResLoc, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofWildcard( REGISTRY, partialResLoc, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace. */
    public static DimensionEnvironment ofWildcard( String namespace, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, "", false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace and partial path. */
    public static DimensionEnvironment ofWildcard( String namespace, String partialPath, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static DimensionEnvironment ofTag( String resLoc, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static DimensionEnvironment ofTag( ResourceLocation resLoc, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag key. */
    public static DimensionEnvironment ofTag( TagKey<? extends LevelStem> tag, boolean invert ) {
        return new DimensionEnvironment( RegObjKey.ofTag( REGISTRY, tag, false ), invert );
    }
    
    
    public DimensionEnvironment( RegObjKey<LevelStem> key, boolean invert ) { super( key, invert ); }
    
    public DimensionEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The registry used. */
    @Override
    public IRegWrapper<LevelStem> getRegistry() { return REGISTRY; }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected LevelStem getActual( EnvironmentContext context ) {
        Registry<LevelStem> registry = getRegistry().asVanillaRegistry();
        return registry == null ? null : registry.get( Registries.levelToLevelStem( context.getFullLevel().dimension() ) );
    }
}