package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over registered objects with associated values.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.common.Tags
 * @see RegObjKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.RegistryValueListField
 * @see RegistryList RegistryList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class RegistryValueList<T, V> extends FuzzyValueList<T, V> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public RegistryValueList( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public RegistryValueList( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public RegistryValueList( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public RegistryValueList( IRegWrapper<T> reg, IValueCodec<V> codec ) {
        super( reg.getParser(), codec );
        registry = reg;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryValueList.Builder} is much easier.
     */
    @SafeVarargs
    public RegistryValueList( IRegWrapper<T> reg, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryValueList.Builder} is much easier.
     */
    public RegistryValueList( IRegWrapper<T> reg, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistryValueList<T, V> makeNew() { return new RegistryValueList<>( registry, valueCodec ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, RegistryValueList<T, V>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
        
        public Builder( IRegWrapper<T> reg, IValueCodec<V> codec ) {
            super( codec );
            registry = reg;
        }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public RegistryValueList<T, V> build() { return new RegistryValueList<>( registry, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. */
        public B put( String resLoc, V value ) { return put( RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. */
        public B put( ResourceLocation resLoc, V value ) { return put( RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. */
        public B put( RegistryObject<? extends T> regObj, V value ) { return put( RegObjKey.of( registry, regObj, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. */
        public B put( ResourceKey<? extends T> resKey, V value ) { return put( RegObjKey.of( registry, resKey, false ), value ); }
        
        /** Adds a key-value pair based on the registered object. Only suitable for vanilla stuff. */
        public B put( T obj, V value ) { return put( RegObjKey.of( registry, obj, false ), value ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Tag keys add the tag's entire contents to the iterator, with the same value for each. */
        public B putTag( String resLoc, V value ) { return put( RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Tag keys add the tag's entire contents to the iterator, with the same value for each. */
        public B putTag( ResourceLocation resLoc, V value ) { return put( RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Tag keys add the tag's entire contents to the iterator, with the same value for each. */
        public B putTag( TagKey<T> tag, V value ) { return put( RegObjKey.ofTag( registry, tag, false ), value ); }
    }
}