package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * TODO
 */
@ApiStatus.Experimental
public class CrustRegistrySet<T> extends FuzzySet<T> {
    
    /** The registry this list acts as a subset of. */
    private final IForgeRegistry<T> REGISTRY;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    protected CrustRegistrySet( IForgeRegistry<T> registry ) { REGISTRY = registry; }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    protected CrustRegistrySet( IForgeRegistry<T> registry, FuzzyKey<T>... keys ) {
        super( keys );
        REGISTRY = registry;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    protected CrustRegistrySet( IForgeRegistry<T> registry, Collection<FuzzyKey<T>> keys ) {
        super( keys );
        REGISTRY = registry;
    }
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                     @Nullable String value, boolean blacklist ) {
        //TODO
        return null;
    }
    
    
    /** Boilerplate builder class for fuzzy sets/maps. */
    @ApiStatus.Experimental
    public static class Builder<T> extends FuzzySet.Builder<T> {
        private final IForgeRegistry<T> REGISTRY;
        
        public Builder( IForgeRegistry<T> registry ) { REGISTRY = registry; }
        
        @Override
        public CrustRegistrySet<T> build() {
            list.trimToSize();
            return new CrustRegistrySet<>( REGISTRY, list );
        }
        
        @Override
        public CrustRegistrySet<T> buildWithDefault() {
            list.add( DefaultKey.get() );
            return build();
        }
        
        public Builder<T> add( Object... entries ) {
            // TODO
            return this;
        }
        
        @Override
        public Builder<T> add( FuzzyKey<T> key ) {
            list.add( key );
            return this;
        }
    }
}