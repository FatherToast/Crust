package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.RegistryEnvironment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

/**
 * Structures: {@link net.minecraft.world.level.levelgen.structure.BuiltinStructures}.
 */
public class StructureEnvironment extends RegistryEnvironment<Structure> {
    
    public static final IRegWrapper<Structure> REGISTRY = IRegWrapper.forKey( Registries.STRUCTURE );
    
    /** @return A new environment based on the resource location. */
    public static StructureEnvironment of( String resLoc, boolean invert ) {
        return new StructureEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the resource location. */
    public static StructureEnvironment of( ResourceLocation resLoc, boolean invert ) {
        return new StructureEnvironment( RegObjKey.of( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new environment based on the registry object. */
    public static StructureEnvironment of( RegistryObject<? extends Structure> regObj, boolean invert ) {
        return new StructureEnvironment( RegObjKey.of( REGISTRY, regObj, false ), invert );
    }
    
    /** @return A new environment based on the resource key. */
    public static StructureEnvironment of( ResourceKey<? extends Structure> resKey, boolean invert ) {
        return new StructureEnvironment( RegObjKey.of( REGISTRY, resKey, false ), invert );
    }
    
    /**
     * @return A new environment based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static StructureEnvironment of( Structure obj, boolean invert ) {
        return new StructureEnvironment( RegObjKey.of( REGISTRY, obj, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the partial resource location. */
    public static StructureEnvironment ofWildcard( ResourceLocation partialResLoc, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofWildcard( REGISTRY, partialResLoc, false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace. */
    public static StructureEnvironment ofWildcard( String namespace, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, "", false ), invert );
    }
    
    /** @return A new wildcard environment, based on the namespace and partial path. */
    public static StructureEnvironment ofWildcard( String namespace, String partialPath, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static StructureEnvironment ofTag( String resLoc, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag resource location. */
    public static StructureEnvironment ofTag( ResourceLocation resLoc, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofTag( REGISTRY, resLoc, false ), invert );
    }
    
    /** @return A new tag environment based on the tag key. */
    public static StructureEnvironment ofTag( TagKey<? extends Structure> tag, boolean invert ) {
        return new StructureEnvironment( RegObjKey.ofTag( REGISTRY, tag, false ), invert );
    }
    
    
    public StructureEnvironment( RegObjKey<Structure> key, boolean invert ) { super( key, invert ); }
    
    public StructureEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The registry used. */
    @Override
    public IRegWrapper<Structure> getRegistry() { return REGISTRY; }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    @Override
    protected boolean cleanTest( EnvironmentContext context ) {
        if( context.getBlockPos() != null && context.getLevel() instanceof ServerLevel level ) {
            for( Structure structure : level.structureManager().getAllStructuresAt( context.getBlockPos() ).keySet() ) {
                if( KEY.matches( structure ) ) return true;
            }
        }
        return false;
    }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Structure getActual( EnvironmentContext context ) { return null; }
}