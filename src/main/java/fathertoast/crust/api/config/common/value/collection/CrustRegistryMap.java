package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.FuzzyRegKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with registered objects.
 *
 * @param <T> The type to match against (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see FuzzyRegKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.RegistrySetField
 */
@ApiStatus.Experimental
public class CrustRegistryMap<T, V> extends FuzzyMap<T, V> {
    /** The target registry. */
    private final IForgeRegistry<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistryMap( IForgeRegistry<T> reg, IValueCodec<V> codec ) {
        super( FuzzyRegKey.parser( reg ), codec );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public CrustRegistryMap( IForgeRegistry<T> reg, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( FuzzyRegKey.parser( reg ), codec, keys );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public CrustRegistryMap( IForgeRegistry<T> reg, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( FuzzyRegKey.parser( reg ), codec, keys );
        registry = reg;
    }
    
    /** @return A fresh, empty map of the same type as this one. */
    public CrustRegistryMap<T, V> makeNew() { return new CrustRegistryMap<>( registry, valueCodec ); }
    
    /** The target registry */
    public IForgeRegistry<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry maps smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends FuzzyMap.Builder<T, V, CrustRegistryMap<T, V>, B> {
        public final IForgeRegistry<T> registry;
        
        public Builder( IForgeRegistry<T> reg, IValueCodec<V> codec ) {
            super( codec );
            registry = reg;
        }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public CrustRegistryMap<T, V> build() { return new CrustRegistryMap<>( registry, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. */
        public B put( ResourceLocation resLoc, V value ) { return put( FuzzyRegKey.Basic.of( registry, resLoc, false ), value ); }
        
        /** Adds a blacklist key based on the resource location. */
        public B putBlacklist( ResourceLocation resLoc ) { return putBlacklist( FuzzyRegKey.Basic.of( registry, resLoc, true ) ); }
        
        /** Adds a key-value pair based on the registry object. */
        public B put( RegistryObject<? extends T> regObj, V value ) { return put( FuzzyRegKey.Basic.of( registry, regObj, false ), value ); }
        
        /** Adds a blacklist key based on the registry object. */
        public B putBlacklist( RegistryObject<? extends T> regObj ) { return putBlacklist( FuzzyRegKey.Basic.of( registry, regObj, true ) ); }
        
        /** Adds a key-value pair based on the registered object. Only suitable for vanilla stuff. */
        public B put( T obj, V value ) { return put( FuzzyRegKey.Basic.of( registry, obj, false ), value ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. */
        public B putBlacklist( T obj ) { return putBlacklist( FuzzyRegKey.Basic.of( registry, obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key-value pair based on the partial resource location. */
        public B putWildcard( ResourceLocation partialResLoc, V value ) { return put( FuzzyRegKey.Wildcard.of( registry, partialResLoc, false ), value ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc ) { return putBlacklist( FuzzyRegKey.Wildcard.of( registry, partialResLoc, true ) ); }
        
        /** Adds a wildcard key-value pair based on the namespace. */
        public B putWildcard( String namespace, V value ) { return putWildcard( namespace, "", value ); }
        
        /** Adds a blacklist wildcard key based on the namespace. */
        public B putWildcardBlacklist( String namespace ) { return putWildcardBlacklist( namespace, "" ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. */
        public B putWildcard( String namespace, String partialPath, V value ) { return put( FuzzyRegKey.Wildcard.of( registry, namespace, partialPath, false ), value ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. */
        public B putWildcardBlacklist( String namespace, String partialPath ) { return putBlacklist( FuzzyRegKey.Wildcard.of( registry, namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. */
        public B putTag( ResourceLocation resLoc, V value ) { return put( FuzzyRegKey.Tag.of( registry, resLoc, false ), value ); }
        
        /** Adds a blacklist tag key based on the resource location. */
        public B putTagBlacklist( ResourceLocation resLoc ) { return putBlacklist( FuzzyRegKey.Tag.of( registry, resLoc, true ) ); }
        
        /** Adds a tag key-value pair based on the tag. */
        public B putTag( TagKey<T> tag, V value ) { return put( FuzzyRegKey.Tag.of( registry, tag, false ), value ); }
        
        /** Adds a blacklist tag key based on the tag. */
        public B putTagBlacklist( TagKey<T> tag ) { return putBlacklist( FuzzyRegKey.Tag.of( registry, tag, true ) ); }
    }
}