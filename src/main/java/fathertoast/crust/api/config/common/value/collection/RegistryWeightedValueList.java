package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy weighted list used to randomly pick registered object-value pairs.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.common.Tags
 * @see RegObjKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.RegistryWeightedValueListField
 * @see RegistryWeightedList RegistryWeightedList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class RegistryWeightedValueList<T, V> extends FuzzyWeightedValueList<T, V> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public RegistryWeightedValueList( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public RegistryWeightedValueList( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public RegistryWeightedValueList( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public RegistryWeightedValueList( IRegWrapper<T> reg, IValueCodec<V> codec ) {
        super( reg.getParser(), codec );
        registry = reg;
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryWeightedValueList.Builder} is much easier.
     */
    @SafeVarargs
    public RegistryWeightedValueList( IRegWrapper<T> reg, IValueCodec<V> codec, WeightedEntry<T, V>... keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryWeightedValueList.Builder} is much easier.
     */
    public RegistryWeightedValueList( IRegWrapper<T> reg, IValueCodec<V> codec, Collection<WeightedEntry<T, V>> keys ) {
        super( reg.getParser(), codec, keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistryWeightedValueList<T, V> makeNew() { return new RegistryWeightedValueList<>( registry, valueCodec ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, RegistryWeightedValueList<T, V>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( Registry<T> reg, IValueCodec<V> codec ) { this( IRegWrapper.of( reg ), codec ); }
        
        public Builder( ResourceKey<Registry<T>> key, IValueCodec<V> codec ) { this( IRegWrapper.forKey( key ), codec ); }
        
        public Builder( IRegWrapper<T> reg, IValueCodec<V> codec ) {
            super( codec );
            registry = reg;
        }
        
        /** @return A new fuzzy weighted value list reflecting the current state of this builder. */
        @Override
        public RegistryWeightedValueList<T, V> build() { return new RegistryWeightedValueList<>( registry, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. */
        public B put( int weight, String resLoc, V value ) { return put( weight, RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. */
        public B put( int weight, ResourceLocation resLoc, V value ) { return put( weight, RegObjKey.of( registry, resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. */
        public B put( int weight, RegistryObject<? extends T> regObj, V value ) { return put( weight, RegObjKey.of( registry, regObj, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. */
        public B put( int weight, ResourceKey<? extends T> resKey, V value ) { return put( weight, RegObjKey.of( registry, resKey, false ), value ); }
        
        /** Adds a key-value pair based on the registered object. Only suitable for vanilla stuff. */
        public B put( int weight, T obj, V value ) { return put( weight, RegObjKey.of( registry, obj, false ), value ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Tag keys return a uniform random element from the tag's contents when picked. */
        public B putTag( int weight, String resLoc, V value ) { return put( weight, RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Tag keys return a uniform random element from the tag's contents when picked. */
        public B putTag( int weight, ResourceLocation resLoc, V value ) { return put( weight, RegObjKey.ofTag( registry, resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Tag keys return a uniform random element from the tag's contents when picked. */
        public B putTag( int weight, TagKey<T> tag, V value ) { return put( weight, RegObjKey.ofTag( registry, tag, false ), value ); }
    }
}