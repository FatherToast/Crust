package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyRegKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A fuzzy set used to match registered objects.
 * <p>
 * See also:
 * {@link net.minecraftforge.registries.ForgeRegistries}, {@link FuzzyRegKey}, and
 * {@link fathertoast.crust.api.config.common.field.RegistrySetField}
 *
 */
@ApiStatus.Experimental
public class CrustRegistrySet<T> extends FuzzySet<T> {
    /** The target registry. */
    private final IForgeRegistry<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public CrustRegistrySet( IForgeRegistry<T> reg ) { registry = reg; }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public CrustRegistrySet( IForgeRegistry<T> reg, FuzzyKey<T>... keys ) {
        super( keys );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public CrustRegistrySet( IForgeRegistry<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( keys );
        registry = reg;
    }
    
    /** @return A fresh, empty set of the same type as this one. */
    public FuzzySet<T> makeNew() { return new CrustRegistrySet<>( registry ); }
    
    /** The target registry */
    public IForgeRegistry<T> getRegistry() { return registry; }
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible
     * and provide useful feedback, identifying the config field if present.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                     @Nullable String value, boolean blacklist ) {
        FuzzyKey<T> loadedKey;
        if( key.startsWith( FuzzyRegKey.Tag.CODE ) ) {
            loadedKey = FuzzyRegKey.Tag.of( blacklist, registry, key );
            if( field != null ) {
                if( loadedKey == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Registry entry has invalid tag key! Skipping. Entry: {}", line );
                }
                if( registry.tags() == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Registry entry defines a tag key for a registry that does not support tags! Entry: {}",
                            line );
                }
            }
        }
        else if( key.endsWith( FuzzyRegKey.Wildcard.CODE ) ) {
            loadedKey = FuzzyRegKey.Wildcard.of( blacklist, registry, key );
            if( field != null && loadedKey == null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has invalid wildcard key! Skipping. Entry: {}",
                        line );
            }
        }
        else {
            loadedKey = FuzzyRegKey.ResLoc.of( blacklist, registry, key );
            if( field != null && loadedKey == null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has invalid key! Skipping. Entry: {}", line );
            }
        }
        return loadedKey;
    }
    
    
    /** Builder to make constructing registry sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<T> extends FuzzySet.Builder<T> {
        public final IForgeRegistry<T> registry;
        
        public Builder( IForgeRegistry<T> reg ) { registry = reg; }
        
        /** @return A new registry set reflecting the current state of this builder. */
        @Override
        public CrustRegistrySet<T> build() { return new CrustRegistrySet<>( registry, list ); }
        
        /** @return A new registry set with a default key reflecting the current state of this builder. */
        @Override
        public CrustRegistrySet<T> buildWithDefault() {
            add( DefaultKey.get() );
            return build();
        }
        
        /** Adds a pre-constructed key. */
        @Override
        public Builder<T> add( FuzzyKey<T> key ) {
            super.add( key );
            return this;
        }
        
        
        // ---- Resource Location Keys ---- //
        
        /** Adds a resource location key based on the resource location. */
        public Builder<T> add( ResourceLocation resLoc ) { return add( resLoc, false ); }
        
        /** Adds a resource location key based on the resource location. */
        public Builder<T> add( ResourceLocation resLoc, boolean blacklist ) {
            return add( FuzzyRegKey.ResLoc.of( blacklist, registry, resLoc ) );
        }
        
        /** Adds a resource location key based on the registry object. */
        public Builder<T> add( RegistryObject<? extends T> regObj ) { return add( regObj, false ); }
        
        /** Adds a resource location key based on the registry object. */
        public Builder<T> add( RegistryObject<? extends T> regObj, boolean blacklist ) {
            return add( FuzzyRegKey.ResLoc.of( blacklist, registry, regObj ) );
        }
        
        /** Adds a resource location key based on the registered object. Only suitable for vanilla stuff. */
        public Builder<T> add( T obj ) { return add( obj, false ); }
        
        /** Adds a resource location key based on the registered object. Only suitable for vanilla stuff. */
        public Builder<T> add( T obj, boolean blacklist ) {
            return add( FuzzyRegKey.ResLoc.of( blacklist, registry, obj ) );
        }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. */
        public Builder<T> addWildcard( ResourceLocation partialResLoc ) { return addWildcard( partialResLoc, false ); }
        
        /** Adds a wildcard key based on the partial resource location. */
        public Builder<T> addWildcard( ResourceLocation partialResLoc, boolean blacklist ) {
            return add( FuzzyRegKey.Wildcard.of( blacklist, registry, partialResLoc ) );
        }
        
        /** Adds a wildcard key based on the namespace. */
        public Builder<T> addWildcard( String namespace ) { return addWildcard( namespace, false ); }
        
        /** Adds a wildcard key based on the namespace. */
        public Builder<T> addWildcard( String namespace, boolean blacklist ) {
            return addWildcard( namespace, "", blacklist );
        }
        
        /** Adds a wildcard key based on the namespace and partial path. */
        public Builder<T> addWildcard( String namespace, String partialPath ) { return addWildcard( namespace, partialPath, false ); }
        
        /** Adds a wildcard key based on the namespace and partial path. */
        public Builder<T> addWildcard( String namespace, String partialPath, boolean blacklist ) {
            return add( FuzzyRegKey.Wildcard.of( blacklist, registry, namespace, partialPath ) );
        }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. */
        public Builder<T> addTag( ResourceLocation resLoc ) { return addTag( resLoc, false ); }
        
        /** Adds a tag key based on the resource location. */
        public Builder<T> addTag( ResourceLocation resLoc, boolean blacklist ) {
            return add( FuzzyRegKey.Tag.of( blacklist, registry, resLoc ) );
        }
        
        /** Adds a tag key based on the tag. */
        public Builder<T> addTag( TagKey<T> tag ) { return addTag( tag, false ); }
        
        /** Adds a tag key based on the tag. */
        public Builder<T> addTag( TagKey<T> tag, boolean blacklist ) {
            return add( FuzzyRegKey.Tag.of( blacklist, registry, tag ) );
        }
        
        /** Adds a tag key based on the tag. */
        public Builder<T> addTag( ITag<T> tag ) { return addTag( tag, false ); }
        
        /** Adds a tag key based on the tag. */
        public Builder<T> addTag( ITag<T> tag, boolean blacklist ) {
            return add( FuzzyRegKey.Tag.of( blacklist, registry, tag ) );
        }
    }
}