package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with registered objects.
 *
 * @param <T> The type to match against (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.common.Tags
 * @see RegObjKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.RegistryMapField
 * @see RegistrySet RegistrySet - A similar collection that does not allow values
 */
@SuppressWarnings( "unused" )
public class RegistryMap<T, V> extends FuzzyMap<T, V> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public RegistryMap( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public RegistryMap( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public RegistryMap( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public RegistryMap( IRegWrapper<T> reg, IValueCodec<V> codec ) {
        super( reg.getParser(), codec );
        registry = reg;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryMap.Builder} is much easier.
     */
    @SafeVarargs
    public RegistryMap( IRegWrapper<T> reg, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryMap.Builder} is much easier.
     */
    public RegistryMap( IRegWrapper<T> reg, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistryMap<T, V> makeNew() { return new RegistryMap<>( registry, valueCodec ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry maps smoother. */
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, RegistryMap<T, V>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
        
        public Builder( IRegWrapper<T> reg, IValueCodec<V> codec ) {
            super( codec );
            registry = reg;
        }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public RegistryMap<T, V> build() { return new RegistryMap<>( registry, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided object. */
        public B put( String resLoc, V value ) { return put( RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided object. */
        public B put( ResourceLocation resLoc, V value ) { return put( RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided object. */
        public B put( RegistryObject<? extends T> regObj, V value ) { return put( RegObjKey.of( registry, regObj, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided object. */
        public B put( ResourceKey<? extends T> resKey, V value ) { return put( RegObjKey.of( registry, resKey, false ), value ); }
        
        /** Adds a key-value pair based on the registered object. Only suitable for vanilla stuff. Matches only the provided object. */
        public B put( T obj, V value ) { return put( RegObjKey.of( registry, obj, false ), value ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided object. */
        public B putBlacklist( String resLoc ) { return putBlacklist( RegObjKey.of( registry, resLoc, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided object. */
        public B putBlacklist( ResourceLocation resLoc ) { return putBlacklist( RegObjKey.of( registry, resLoc, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided object. */
        public B putBlacklist( RegistryObject<? extends T> regObj ) { return putBlacklist( RegObjKey.of( registry, regObj, true ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided object. */
        public B putBlacklist( ResourceKey<? extends T> resKey ) { return putBlacklist( RegObjKey.of( registry, resKey, true ) ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. Matches only the provided object. */
        public B putBlacklist( T obj ) { return putBlacklist( RegObjKey.of( registry, obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches everything in the namespace that starts with the partial path. */
        public B putWildcard( ResourceLocation partialResLoc, V value ) { return put( RegObjKey.ofWildcard( registry, partialResLoc, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches everything in the namespace. */
        public B putWildcard( String namespace, V value ) { return put( RegObjKey.ofWildcard( registry, namespace, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches everything in the namespace that starts with the partial path. */
        public B putWildcard( String namespace, String partialPath, V value ) { return put( RegObjKey.ofWildcard( registry, namespace, partialPath, false ), value ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches everything in the namespace that starts with the partial path. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc ) { return putBlacklist( RegObjKey.ofWildcard( registry, partialResLoc, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches everything in the namespace. */
        public B putWildcardBlacklist( String namespace ) { return putBlacklist( RegObjKey.ofWildcard( registry, namespace, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches everything in the namespace that starts with the partial path. */
        public B putWildcardBlacklist( String namespace, String partialPath ) { return putBlacklist( RegObjKey.ofWildcard( registry, namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches everything in the tag. */
        public B putTag( String resLoc, V value ) { return put( RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches everything in the tag. */
        public B putTag( ResourceLocation resLoc, V value ) { return put( RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches everything in the tag. */
        public B putTag( TagKey<T> tag, V value ) { return put( RegObjKey.ofTag( registry, tag, false ), value ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches everything in the tag. */
        public B putTagBlacklist( String resLoc ) { return putBlacklist( RegObjKey.ofTag( registry, resLoc, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches everything in the tag. */
        public B putTagBlacklist( ResourceLocation resLoc ) { return putBlacklist( RegObjKey.ofTag( registry, resLoc, true ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches everything in the tag. */
        public B putTagBlacklist( TagKey<T> tag ) { return putBlacklist( RegObjKey.ofTag( registry, tag, true ) ); }
    }
}