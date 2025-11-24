package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyRegKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
 * @see CrustRegistryMap CrustRegistryMap - A similar collection that allows values
 */
@ApiStatus.Experimental
public class CrustRegistrySet<T> extends FuzzySet<T> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( IRegWrapper<T> reg ) {
        super( reg.getParser() );
        registry = reg;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link CrustRegistrySet.Builder} is much easier.
     */
    @SafeVarargs
    public CrustRegistrySet( IRegWrapper<T> reg, FuzzyKey<T>... keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link CrustRegistrySet.Builder} is much easier.
     */
    public CrustRegistrySet( IRegWrapper<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /** @return A fresh, empty set of the same type as this one. */
    public CrustRegistrySet<T> makeNew() { return new CrustRegistrySet<>( registry ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends FuzzySet.Builder<T, CrustRegistrySet<T>, B> {
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
        
        public Builder( IRegWrapper<T> reg ) { registry = reg; }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public CrustRegistrySet<T> build() { return new CrustRegistrySet<>( registry, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. */
        public B add( ResourceLocation resLoc ) { return add( FuzzyRegKey.Basic.of( registry, resLoc, false ) ); }
        
        /** Adds a blacklist key based on the resource location. */
        public B addBlacklist( ResourceLocation resLoc ) { return add( FuzzyRegKey.Basic.of( registry, resLoc, true ) ); }
        
        /** Adds a key based on the registry object. */
        public B add( RegistryObject<? extends T> regObj ) { return add( FuzzyRegKey.Basic.of( registry, regObj, false ) ); }
        
        /** Adds a blacklist key based on the registry object. */
        public B addBlacklist( RegistryObject<? extends T> regObj ) { return add( FuzzyRegKey.Basic.of( registry, regObj, true ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. */
        public B add( T obj ) { return add( FuzzyRegKey.Basic.of( registry, obj, false ) ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. */
        public B addBlacklist( T obj ) { return add( FuzzyRegKey.Basic.of( registry, obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. */
        public B addWildcard( ResourceLocation partialResLoc ) { return add( FuzzyRegKey.Wildcard.of( registry, partialResLoc, false ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc ) { return add( FuzzyRegKey.Wildcard.of( registry, partialResLoc, true ) ); }
        
        /** Adds a wildcard key based on the namespace. */
        public B addWildcard( String namespace ) { return addWildcard( namespace, "" ); }
        
        /** Adds a blacklist wildcard key based on the namespace. */
        public B addWildcardBlacklist( String namespace ) { return addWildcardBlacklist( namespace, "" ); }
        
        /** Adds a wildcard key based on the namespace and partial path. */
        public B addWildcard( String namespace, String partialPath ) { return add( FuzzyRegKey.Wildcard.of( registry, namespace, partialPath, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. */
        public B addWildcardBlacklist( String namespace, String partialPath ) { return add( FuzzyRegKey.Wildcard.of( registry, namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. */
        public B addTag( ResourceLocation resLoc ) { return add( FuzzyRegKey.Tag.of( registry, resLoc, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. */
        public B addTagBlacklist( ResourceLocation resLoc ) { return add( FuzzyRegKey.Tag.of( registry, resLoc, true ) ); }
        
        /** Adds a tag key based on the tag. */
        public B addTag( TagKey<T> tag ) { return add( FuzzyRegKey.Tag.of( registry, tag, false ) ); }
        
        /** Adds a blacklist tag key based on the tag. */
        public B addTagBlacklist( TagKey<T> tag ) { return add( FuzzyRegKey.Tag.of( registry, tag, true ) ); }
    }
}