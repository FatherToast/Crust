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
 * A fuzzy list used to iterate over registered objects.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.common.Tags
 * @see RegObjKey
 * @see fathertoast.crust.api.config.common.field.collection.RegistryListField
 * @see RegistryValueList RegistryValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class RegistryList<T> extends FuzzyList<T> {
    /** The target registry. */
    private final IRegWrapper<T> registry;
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public RegistryList( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public RegistryList( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public RegistryList( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public RegistryList( IRegWrapper<T> reg ) {
        super( reg.getParser() );
        registry = reg;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistryList.Builder} is much easier.
     */
    @SafeVarargs
    public RegistryList( IRegWrapper<T> reg, FuzzyKey<T>... keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link RegistryList.Builder} is much easier.
     */
    public RegistryList( IRegWrapper<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( reg.getParser(), keys );
        registry = reg;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public RegistryList<T> makeNew() { return new RegistryList<>( registry ); }
    
    /** The target registry */
    public IRegWrapper<T> getRegistry() { return registry; }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, RegistryList<T>, B> {
        
        public final IRegWrapper<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( Registry<T> reg ) { this( IRegWrapper.of( reg ) ); }
        
        public Builder( ResourceKey<Registry<T>> key ) { this( IRegWrapper.forKey( key ) ); }
        
        public Builder( IRegWrapper<T> reg ) { registry = reg; }
        
        /** @return A new fuzzy list reflecting the current state of this builder. */
        @Override
        public RegistryList<T> build() { return new RegistryList<>( registry, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. */
        public B add( String resLoc ) { return add( RegObjKey.of( registry, resLoc, false ) ); }
        
        /** Adds a key based on the resource location. */
        public B add( ResourceLocation resLoc ) { return add( RegObjKey.of( registry, resLoc, false ) ); }
        
        /** Adds a key based on the registry object. */
        public B add( RegistryObject<? extends T> regObj ) { return add( RegObjKey.of( registry, regObj, false ) ); }
        
        /** Adds a key based on the resource key. */
        public B add( ResourceKey<? extends T> resKey ) { return add( RegObjKey.of( registry, resKey, false ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. */
        public B add( T obj ) { return add( RegObjKey.of( registry, obj, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Tag keys add the tag's entire contents to the iterator. */
        public B addTag( String resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, false ) ); }
        
        /** Adds a tag key based on the resource location. Tag keys add the tag's entire contents to the iterator. */
        public B addTag( ResourceLocation resLoc ) { return add( RegObjKey.ofTag( registry, resLoc, false ) ); }
        
        /** Adds a tag key based on the tag. Tag keys add the tag's entire contents to the iterator. */
        public B addTag( TagKey<T> tag ) { return add( RegObjKey.ofTag( registry, tag, false ) ); }
    }
}