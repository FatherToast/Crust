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
    
    private final int reqValues;
    
    /**
     * @param valueCount The number of values attached to each resource location. A negative value means "at least one value".
     */
    public RLValueListField( String key, int valueCount, List<String> defaultValue, @Nullable String... description ) {
        super( key, "Resource Location value List", defaultValue, description );
        reqValues = valueCount;
        parseEntries( defaultValue );
    }
    
    /** Attempts to parse each String in the given list into valid entries. */
    private void parseEntries( List<String> list ) {
        // Get all values from the list
        for( String line : list ) {
            if( line == null ) continue;
            
            final List<Double> valuesList = new ArrayList<>();
            final String[] args = line.trim().split( " " );
            final int actualValues = args.length - 1;
            
            // Parse the resource location
            final ResourceLocation resLoc = ResourceLocation.tryParse( args[0] );
            if( resLoc == null ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Registry entry has invalid resource location! Deleting. Entry: {}", line );
                continue;
            }
            
            // Variable-value; just needs at least one value
            if( reqValues < 0 ) {
                if( actualValues < 1 ) {
                    ConfigUtil.warnFor( this );
                    ConfigUtil.LOG.warn( "Resource location entry has too few values! Must have at least one value. Replacing missing value with 0. Entry: {}",
                            line );
                    valuesList.add( 0.0 );
                }
                else {
                    // Parse all values
                    for( int i = 1; i < args.length; i++ ) {
                        valuesList.add( parseValue( args[i], line ) );
                    }
                }
            }
            // Specified value; must have the exact number of values
            else {
                if( reqValues > actualValues ) {
                    ConfigUtil.warnFor( this );
                    ConfigUtil.LOG.warn( "Resource location entry has too few values! Expected {} values, but found {}. Replacing missing values with 0. Entry: {}",
                            reqValues, actualValues, line );
                }
                else if( reqValues < actualValues ) {
                    ConfigUtil.warnFor( this );
                    ConfigUtil.LOG.warn( "Resource location entry has too many values! Expected {} values, but found {}. Deleting excess values. Entry: {}",
                            reqValues, actualValues, line );
                }
                
                // Parse all values
                for( int i = 1; i < reqValues + 1; i++ ) {
                    if( i < args.length ) {
                        valuesList.add( parseValue( args[i], line ) );
                    }
                    else {
                        valuesList.add( 0.0 );
                    }
                }
            }
            
            // Convert to array
            final Double[] values = new Double[valuesList.size()];
            for( int i = 0; i < values.length; i++ ) {
                values[i] = valuesList.get( i );
            }
            ENTRIES.add( resLoc );
            VALUE_PAIRS.add( Pair.of( resLoc, values ) );
        }
    }
    
    /** Parses a single value argument and returns a valid result. */
    private double parseValue( final String arg, final String line ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Resource location entry has invalid value ({})! Falling back to 0. Entry: {}",
                    arg, line );
            value = 0.0;
        }
        // TODO Make this list match the typical value lists so it can support min/max;
        //  also make a generic values list + field to make these field types less copypasta
        //  (see WeightedPotionListField)
        // Verify value is within range
        //        if( value < valueDefault.getMinValue() ) {
        //            ConfigUtil.LOGn.warn( "Warning for {}:", describeLocation() );
        //            ConfigUtil.LOGn.warn( "Registry entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
        //                    value, valueDefault.getMinValue(), line );
        //            value = valueDefault.getMinValue();
        //        }
        //        else if( value > valueDefault.getMaxValue() ) {
        //            ConfigUtil.LOGn.warn( "Warning for {}:", describeLocation() );
        //            ConfigUtil.LOGn.warn( "Entity entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
        //                    value, valueDefault.getMaxValue(), line );
        //            value = valueDefault.getMaxValue();
        //        }
        return value;
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