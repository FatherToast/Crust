package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyRegKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy set used to match registered objects.
 *
 * @param <T> The type to match against (i.e., the registry type).
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see FuzzyRegKey
 * @see fathertoast.crust.api.config.common.field.RegistrySetField
 */
@ApiStatus.Experimental
public class CrustRegistrySet<T> extends FuzzySet<T> {
    /** The target registry. */
    private final IForgeRegistry<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( IForgeRegistry<T> reg ) {
        super( FuzzyRegKey.parser( reg ) );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public CrustRegistrySet( IForgeRegistry<T> reg, FuzzyKey<T>... keys ) {
        super( FuzzyRegKey.parser( reg ), keys );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public CrustRegistrySet( IForgeRegistry<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( FuzzyRegKey.parser( reg ), keys );
        registry = reg;
    }
    
    /** @return A fresh, empty set of the same type as this one. */
    public CrustRegistrySet<T> makeNew() { return new CrustRegistrySet<>( registry ); }
    
    /** The target registry */
    public IForgeRegistry<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends FuzzySet.Builder<T, CrustRegistrySet<T>, B> {
        public final IForgeRegistry<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { registry = reg; }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public CrustRegistrySet<T> build() { return new CrustRegistrySet<>( registry, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. */
        public B add( ResourceLocation resLoc ) { return add( resLoc, false ); }
        
        /** Adds a key based on the resource location. */
        public B add( ResourceLocation resLoc, boolean blacklist ) { return add( FuzzyRegKey.Basic.of( registry, resLoc, blacklist ) ); }
        
        /** Adds a key based on the registry object. */
        public B add( RegistryObject<? extends T> regObj ) { return add( regObj, false ); }
        
        /** Adds a key based on the registry object. */
        public B add( RegistryObject<? extends T> regObj, boolean blacklist ) { return add( FuzzyRegKey.Basic.of( registry, regObj, blacklist ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. */
        public B add( T obj ) { return add( obj, false ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. */
        public B add( T obj, boolean blacklist ) { return add( FuzzyRegKey.Basic.of( registry, obj, blacklist ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. */
        public B addWildcard( ResourceLocation partialResLoc ) { return addWildcard( partialResLoc, false ); }
        
        /** Adds a wildcard key based on the partial resource location. */
        public B addWildcard( ResourceLocation partialResLoc, boolean blacklist ) { return add( FuzzyRegKey.Wildcard.of( registry, partialResLoc, blacklist ) ); }
        
        /** Adds a wildcard key based on the namespace. */
        public B addWildcard( String namespace ) { return addWildcard( namespace, false ); }
        
        /** Adds a wildcard key based on the namespace. */
        public B addWildcard( String namespace, boolean blacklist ) { return addWildcard( namespace, "", blacklist ); }
        
        /** Adds a wildcard key based on the namespace and partial path. */
        public B addWildcard( String namespace, String partialPath ) { return addWildcard( namespace, partialPath, false ); }
        
        /** Adds a wildcard key based on the namespace and partial path. */
        public B addWildcard( String namespace, String partialPath, boolean blacklist ) { return add( FuzzyRegKey.Wildcard.of( registry, namespace, partialPath, blacklist ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. */
        public B addTag( ResourceLocation resLoc ) { return addTag( resLoc, false ); }
        
        /** Adds a tag key based on the resource location. */
        public B addTag( ResourceLocation resLoc, boolean blacklist ) { return add( FuzzyRegKey.Tag.of( registry, resLoc, blacklist ) ); }
        
        /** Adds a tag key based on the tag. */
        public B addTag( TagKey<T> tag ) { return addTag( tag, false ); }
        
        /** Adds a tag key based on the tag. */
        public B addTag( TagKey<T> tag, boolean blacklist ) { return add( FuzzyRegKey.Tag.of( registry, tag, blacklist ) ); }
    }
}