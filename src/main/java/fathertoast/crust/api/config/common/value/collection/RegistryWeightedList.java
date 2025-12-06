package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy weighted list used to randomly pick registered objects.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see RegObjKey
 * @see fathertoast.crust.api.config.common.field.collection.RegistryWeightedListField
 * @see RegistryWeightedValueList RegistryWeightedValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class RegistryWeightedList<T> extends FuzzyWeightedList<T> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public RegistryWeightedList( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public RegistryWeightedList( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public RegistryWeightedList( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public RegistryWeightedList( IRegWrapper<T> reg ) {
        super( reg.getParser() );
        registry = reg;
    }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistryWeightedList.Builder} is much easier.
     */
    @SafeVarargs
    public RegistryWeightedList( IRegWrapper<T> reg, WeightedKey<T>... keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistryWeightedList.Builder} is much easier.
     */
    public RegistryWeightedList( IRegWrapper<T> reg, Collection<WeightedKey<T>> keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistryWeightedList<T> makeNew() { return new RegistryWeightedList<>( registry ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, RegistryWeightedList<T>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
        
        public Builder( IRegWrapper<T> reg ) { registry = reg; }
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public RegistryWeightedList<T> build() { return new RegistryWeightedList<>( registry, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. */
        public B add( int weight, ResourceLocation resLoc ) { return add( weight, RegObjKey.Basic.of( registry, resLoc, false ) ); }
        
        /** Adds a key based on the registry object. */
        public B add( int weight, RegistryObject<? extends T> regObj ) { return add( weight, RegObjKey.Basic.of( registry, regObj, false ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. */
        public B add( int weight, T obj ) { return add( weight, RegObjKey.Basic.of( registry, obj, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Tag keys return a uniform random element from the tag's contents when picked. */
        public B addTag( int weight, ResourceLocation resLoc ) { return add( weight, RegObjKey.Tag.of( registry, resLoc, false ) ); }
        
        /** Adds a tag key based on the tag. Tag keys return a uniform random element from the tag's contents when picked. */
        public B addTag( int weight, TagKey<T> tag ) { return add( weight, RegObjKey.Tag.of( registry, tag, false ) ); }
    }
}