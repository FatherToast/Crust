package fathertoast.crust.api.config.common.field;

import com.mojang.datafixers.util.Pair;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An extension of {@link StringListField} that loads
 * and parses a list of Strings as a list of ResourceLocations
 * paired with one or multiple numeric values.<br><br>
 * <p>
 * Actual pairs of ResourceLocations and values are stored in the field itself,
 * so calling getters from super only returns the raw list of Strings.
 */
public class RLValueListField extends StringListField {
    
    /** The resource location entries in this list. */
    private final List<ResourceLocation> ENTRIES = new ArrayList<>();
    /** The list of entry-value pairs for this list. */
    private final List<Pair<ResourceLocation, Double[]>> VALUE_PAIRS = new ArrayList<>();
    
    private final int valueCount;
    
    
    public RLValueListField( String key, int valueCount, List<String> defaultValue, @Nullable String... description ) {
        super( key, "Resource Location value List", defaultValue, description );
        this.valueCount = Math.max( 0, valueCount );
        parseEntries( defaultValue );
    }
    
    /** Attempts to parse each String in the given list into valid entries. */
    private void parseEntries( List<String> list ) {
        // Get all values from the list
        for( String entry : list ) {
            if( entry != null ) {
                try {
                    String s = entry.trim();
                    String[] parts = s.split( " " );
                    
                    // Make sure the total amount of components is as expected after trimming spaces
                    if( parts.length != (valueCount + 1) ) {
                        ConfigUtil.LOG.error( "RLValueListField '{}' contains a line with an invalid number of values. Expected {}, got {}! ", getKey(), valueCount, parts.length - 1 );
                        continue;
                    }
                    final ResourceLocation rl = ResourceLocation.tryParse( parts[0] );
                    
                    // First string is not a valid resource location, skip line
                    if( rl == null ) {
                        ConfigUtil.LOG.error( "RLValueListField '{}' contains a line with invalid ResourceLocation. Problematic String: '{}'", getKey(), parts[0] );
                        continue;
                    }
                    Double[] values = new Double[valueCount];
                    
                    for( int i = 0; i < valueCount; i++ ) {
                        try {
                            values[i] = Double.valueOf( parts[i + 1] );
                        }
                        // Value cannot be parsed as a double
                        catch( NumberFormatException e ) {
                            ConfigUtil.LOG.error( "RLValueListField {} contains invalid non-numeric value! Problematic String: '{}'", getKey(), parts[i + 1] );
                            break;
                        }
                    }
                    ENTRIES.add( rl );
                    VALUE_PAIRS.add( Pair.of( rl, values ) );
                }
                catch( Exception e ) {
                    ConfigUtil.LOG.error( "Failed to load RLValueListField '{}'!", getKey() );
                }
            }
        }
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Resource Location List", valueDefault,
                "[ \"namespace:path\", ... ]" ) );
    }
    
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        final List<String> list = TomlHelper.parseStringList( raw );
        parseEntries( list );
        value = list;
    }
    
    /** @return An iterable view of this list's entries. */
    public Iterable<ResourceLocation> iterator() {
        return ENTRIES;
    }
    
    /**
     * @return The values associated with the given resource location.
     * returns null if the resource location is not contained in this list.
     */
    @Nullable
    public Double[] getValuesFor( ResourceLocation resourceLocation ) {
        Objects.requireNonNull( resourceLocation );
        
        for( Pair<ResourceLocation, Double[]> pair : VALUE_PAIRS ) {
            if( resourceLocation.equals( pair.getFirst() ) ) {
                return pair.getSecond();
            }
        }
        return null;
    }
}