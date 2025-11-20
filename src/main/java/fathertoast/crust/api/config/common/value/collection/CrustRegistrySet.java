package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyRegKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 */
@ApiStatus.Experimental
public class CrustRegistrySet<T> extends FuzzySet<T> {
    
    /** @return The array of miscellaneous objects parsed into keys valid for this set type. */
    public static <T> List<FuzzyKey<T>> parseObjects( IForgeRegistry<T> reg, Object... objects ) {
        List<FuzzyKey<T>> keys = new ArrayList<>( objects.length );
        for( Object obj : objects ) {
            if( obj instanceof FuzzyKey<?> ) {
                //noinspection unchecked
                keys.add( (FuzzyKey<T>) obj );
            }
            else if( obj instanceof ResourceLocation ) {
                keys.add( FuzzyRegKey.ResLoc.of( false, reg, (ResourceLocation) obj ) );
            }
            else if( obj instanceof TagKey<?> ) {
                //noinspection unchecked
                keys.add( FuzzyRegKey.Tag.of( false, reg, (TagKey<T>) obj ) );
            }
            else if( obj instanceof ITag<?> ) {
                //noinspection unchecked
                keys.add( FuzzyRegKey.Tag.of( false, reg, (ITag<T>) obj ) );
            }
            else if( obj instanceof String ) {
                tryParse( reg, null, (String) obj, (String) obj, false );
            }
        }
        return keys;
    }
    
    @Nullable
    public static <T> FuzzyKey<T> tryParse( IForgeRegistry<T> reg, @Nullable AbstractConfigField field,
                                            String line, String keyString, boolean blacklist ) {
        FuzzyRegKey<T> key;
        if( keyString.startsWith( FuzzyRegKey.Tag.CODE ) ) {
            key = FuzzyRegKey.Tag.of( blacklist, reg, keyString );
            if( key == null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has invalid tag key! Skipping. Entry: {}", line );
            }
        }
        else if( keyString.endsWith( FuzzyRegKey.Wildcard.CODE ) ) {
            key = FuzzyRegKey.Wildcard.of( blacklist, reg, keyString );
            if( key == null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has invalid wildcard key! Must follow pattern \"namespace:path*\". Skipping. Entry: {}",
                        line );
            }
        }
        else {
            key = FuzzyRegKey.ResLoc.of( blacklist, reg, keyString );
            if( key == null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has invalid key! Skipping. Entry: {}", line );
            }
        }
        return key;
    }
    
    
    /** The registry this list acts as a subset of. */
    private final IForgeRegistry<T> registry;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    protected CrustRegistrySet( IForgeRegistry<T> reg ) { registry = reg; }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    protected CrustRegistrySet( IForgeRegistry<T> reg, Object... objects ) {
        this( reg, parseObjects( reg, objects ) );
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    protected CrustRegistrySet( IForgeRegistry<T> reg, FuzzyKey<T>... keys ) {
        super( keys );
        registry = reg;
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    protected CrustRegistrySet( IForgeRegistry<T> reg, Collection<FuzzyKey<T>> keys ) {
        super( keys );
        registry = reg;
    }
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible
     * and provide useful feedback, identifying the config field if present.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                     @Nullable String value, boolean blacklist ) {
        return tryParse( registry, field, line, key, blacklist );
    }
}