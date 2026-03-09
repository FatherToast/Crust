package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.crust.api.config.common.value.weighted.WeightedPotionList;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Deprecated( forRemoval = true )
public class WeightedPotionListField extends RegistryEntryValueListField<MobEffect> {
    
    public static WeightedPotionList fromNBT( ListTag tag, int reqValues, double minVal, double maxVal ) {
        // noinspection unchecked
        RegistryValueEntry<MobEffect>[] entries = new RegistryValueEntry[tag.size()];
        for( int i = 0; i < entries.length; i++ ) {
            RegistryValueEntry<MobEffect> entry = parseEntry( tag.getString( i ), null, reqValues, minVal, maxVal );
            
            if( entry != null )
                entries[i] = entry;
        }
        return new WeightedPotionList( entries );
    }
    
    /** Creates a new field. */
    public WeightedPotionListField( String key, WeightedPotionList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** @return Returns the config field's value. */
    public WeightedPotionList get() { return (WeightedPotionList) value; }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public WeightedPotionList getValue() { return (WeightedPotionList) value; }
    
    /** @return The default value of this field. */
    @Override
    public WeightedPotionList getDefaultValue() { return (WeightedPotionList) valueDefault; }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        
        if( raw instanceof WeightedPotionList ) {
            value = (WeightedPotionList) raw;
        }
        else {
            List<String> list = TomlHelper.parseStringList( raw );
            List<RegistryValueEntry<MobEffect>> entryList = new ArrayList<>();
            for( String line : list ) {
                RegistryValueEntry<MobEffect> entry = parseEntry( line, this, valueDefault.getRequiredValues(),
                        valueDefault.getMinValue(), valueDefault.getMaxValue() );
                
                if( entry != null ) entryList.add( entry );
            }
            value = new WeightedPotionList( entryList );
        }
    }
    
    /** Parses a single entry line and returns the result. */
    @Nullable
    // TODO Use this as the starting point for making value lists generic
    //      (Remember that default entries, namespace entries and tag entries are not handled properly here, maybe see RegistryEntryValueList)
    private static RegistryValueEntry<MobEffect> parseEntry( final String line, @Nullable final WeightedPotionListField field,
                                                             final int reqValues, final double minVal, final double maxVal ) {
        // Parse the value array
        final String[] args = line.split( " " );
        if( "default".equalsIgnoreCase( args[0].trim() ) ) {
            // Default entry not allowed
            return null;
        }
        // Normal entry
        final ResourceLocation regKey = ResourceLocation.tryParse( args[0].trim() );
        if( regKey == null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Registry entry has invalid resource location! Deleting. Entry: {}", line );
            return null;
        }
        final List<Double> valuesList = new ArrayList<>();
        final int actualValues = args.length - 1;
        
        // Variable-value; just needs at least one value
        if( reqValues < 0 ) {
            if( actualValues < 1 ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has too few values! Must have at least one value. Replacing missing value with 0. Entry: {}",
                        line );
                valuesList.add( 0.0 );
            }
            else {
                // Parse all values
                for( int i = 1; i < args.length; i++ ) {
                    valuesList.add( parseValue( args[i], line, field, minVal, maxVal ) );
                }
            }
        }
        // Specified value; must have the exact number of values
        else {
            if( reqValues > actualValues ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has too few values! Expected {} values, but found {}. Replacing missing values with 0. Entry: {}",
                        reqValues, actualValues, line );
            }
            else if( reqValues < actualValues ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Registry entry has too many values! Expected {} values, but found {}. Deleting excess values. Entry: {}",
                        reqValues, actualValues, line );
            }
            
            // Parse all values
            for( int i = 1; i < reqValues + 1; i++ ) {
                if( i < args.length ) {
                    valuesList.add( parseValue( args[i], line, field, minVal, maxVal ) );
                }
                else {
                    valuesList.add( 0.0 );
                }
            }
        }
        
        // Convert to array
        final double[] values = new double[valuesList.size()];
        for( int i = 0; i < values.length; i++ ) {
            values[i] = valuesList.get( i );
        }
        return new RegistryValueEntry<>( field, regKey, values );
    }
    
    /** Parses a single value argument and returns a valid result. */
    private static double parseValue( final String arg, final String line, @Nullable final WeightedPotionListField field,
                                      final double minVal, final double maxVal ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Registry entry has invalid value ({})! Falling back to 0. Entry: {}",
                    arg, line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < minVal ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Registry entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
                    value, minVal, line );
            value = minVal;
        }
        else if( value > maxVal ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Entity entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
                    value, maxVal, line );
            value = maxVal;
        }
        return value;
    }
}