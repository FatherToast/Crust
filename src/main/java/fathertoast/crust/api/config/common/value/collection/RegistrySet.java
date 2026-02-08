package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
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
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.common.Tags
 * @see RegObjKey
 * @see fathertoast.crust.api.config.common.field.collection.RegistrySetField
 * @see RegistryMap RegistryMap - A similar collection that allows values
 */
@ApiStatus.Experimental
public class RegistrySet<T> extends FuzzySet<T> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public RegistrySet( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public RegistrySet( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public RegistrySet( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public RegistrySet( IRegWrapper<T> reg ) {
        super( reg.getParser() );
        registry = reg;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistrySet.Builder} is much easier.
     */
    @SafeVarargs
    public RegistrySet( IRegWrapper<T> reg, FuzzyKey<T>... keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistrySet.Builder} is much easier.
     */
    public RegistrySet( IRegWrapper<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistrySet<T> makeNew() { return new RegistrySet<>( registry ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, RegistrySet<T>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
        
        public Builder( IRegWrapper<T> reg ) { registry = reg; }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public RegistrySet<T> build() { return new RegistrySet<>( registry, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided object. */
        public B add( String resLoc ) { return add( RegObjKey.of( registry, resLoc, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided object. */
        public B addBlacklist( String resLoc ) { return add( RegObjKey.of( registry, resLoc, true ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided object. */
        public B add( ResourceLocation resLoc ) { return add( RegObjKey.of( registry, resLoc, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided object. */
        public B addBlacklist( ResourceLocation resLoc ) { return add( RegObjKey.of( registry, resLoc, true ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided object. */
        public B add( RegistryObject<? extends T> regObj ) { return add( RegObjKey.of( registry, regObj, false ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided object. */
        public B addBlacklist( RegistryObject<? extends T> regObj ) { return add( RegObjKey.of( registry, regObj, true ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided object. */
        public B add( ResourceKey<? extends T> resKey ) { return add( RegObjKey.of( registry, resKey, false ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided object. */
        public B addBlacklist( ResourceKey<? extends T> resKey ) { return add( RegObjKey.of( registry, resKey, true ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. Matches only the provided object. */
        public B add( T obj ) { return add( RegObjKey.of( registry, obj, false ) ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. Matches only the provided object. */
        public B addBlacklist( T obj ) { return add( RegObjKey.of( registry, obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. Matches everything in the namespace that starts with the partial path. */
        public B addWildcard( ResourceLocation partialResLoc ) { return add( RegObjKey.ofWildcard( registry, partialResLoc, false ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches everything in the namespace that starts with the partial path. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc ) { return add( RegObjKey.ofWildcard( registry, partialResLoc, true ) ); }
        
        /** Adds a wildcard key based on the namespace. Matches everything in the namespace. */
        public B addWildcard( String namespace ) { return add( RegObjKey.ofWildcard( registry, namespace, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches everything in the namespace. */
        public B addWildcardBlacklist( String namespace ) { return add( RegObjKey.ofWildcard( registry, namespace, true ) ); }
        
        /** Adds a wildcard key based on the namespace and partial path. Matches everything in the namespace that starts with the partial path. */
        public B addWildcard( String namespace, String partialPath ) { return add( RegObjKey.ofWildcard( registry, namespace, partialPath, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches everything in the namespace that starts with the partial path. */
        public B addWildcardBlacklist( String namespace, String partialPath ) { return add( RegObjKey.ofWildcard( registry, namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches everything in the tag. */
        public B addTag( String resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches everything in the tag. */
        public B addTagBlacklist( String resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, true ) ); }
        
        /** Adds a tag key based on the resource location. Matches everything in the tag. */
        public B addTag( ResourceLocation resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches everything in the tag. */
        public B addTagBlacklist( ResourceLocation resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, true ) ); }
        
        /** Adds a tag key based on the tag. Matches everything in the tag. */
        public B addTag( TagKey<T> tag ) { return add( RegObjKey.ofTag( registry, tag, false ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches everything in the tag. */
        public B addTagBlacklist( TagKey<T> tag ) { return add( RegObjKey.ofTag( registry, tag, true ) ); }
    }
}