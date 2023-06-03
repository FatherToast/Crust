package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.environment.AbstractEnvironment;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an environment list value.
 */
@SuppressWarnings( "unused" )
public class EnvironmentListField extends GenericField<EnvironmentList> {
    
    /**
     * Provides a description of how to use environment lists. Recommended to put at the top of any file using environment lists.
     * Always use put the environment condition descriptions at the bottom of the file if this is used!
     */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Environment List fields: General format =" );
        comment.add( "    [ \"value environment1 condition1 & environment2 condition2 & ...\", ... ]" );
        comment.add( "  Environment lists are arrays of environment entries. Each entry is a value followed by the " +
                "environment conditions that must be satisfied for the value to be chosen. The environments are tested " +
                "in the order listed, and the first matching entry is chosen." );
        comment.add( "  See the bottom of this file for an explanation on each environment condition available." );
        return comment;
    }
    
    /** Creates a new field. */
    public EnvironmentListField( String key, EnvironmentList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Environment List", valueDefault,
                "[ \"value condition1 state1 & condition2 state2 & ...\", ... ]" ) );
        comment.add( "   Range for Values: " + TomlHelper.fieldRange( valueDefault.getMinValue(), valueDefault.getMaxValue() ) );
    }
    
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
        
        if( raw instanceof EnvironmentList ) {
            value = (EnvironmentList) raw;
        }
        else {
            List<String> list = TomlHelper.parseStringList( raw );
            List<EnvironmentEntry> entryList = new ArrayList<>();
            for( String line : list ) {
                entryList.add( parseEntry( line ) );
            }
            value = new EnvironmentList( entryList );
        }
    }
    
    /** Parses a single entry line and returns the result. */
    private EnvironmentEntry parseEntry( final String line ) {
        // Parse the value out of the conditions
        final String[] args = line.split( " ", 2 );
        final double value = parseValue( args[0], line );
        
        final List<AbstractEnvironment> conditions = new ArrayList<>();
        if( args.length > 1 ) {
            final String[] condArgs = args[1].split( "&" );
            for( String condArg : condArgs ) {
                conditions.add( parseCondition( condArg.trim(), line ) );
            }
        }
        if( conditions.isEmpty() ) {
            ConfigUtil.LOG.warn( "No environments defined in entry for {} \"{}\"! Invalid entry: {}",
                    getClass(), getKey(), line );
        }
        
        return new EnvironmentEntry( value, conditions );
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
            ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to 0. Invalid entry: {}",
                    getClass(), getKey(), line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < valueDefault.getMinValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMinValue(), value );
            value = valueDefault.getMinValue();
        }
        else if( value > valueDefault.getMaxValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMaxValue(), value );
            value = valueDefault.getMaxValue();
        }
        return value;
    }
    
    /** Parses a single environment condition argument and returns a valid result. */
    private AbstractEnvironment parseCondition( final String arg, final String line ) {
        // First parse the environment name, since it defines the format for the rest
        final String[] args = arg.split( " ", 2 );
        
        final String value;
        if( args.length < 2 ) value = "";
        else value = args[1].trim();
        
        return CrustEnvironmentRegistry.parse( this, args[0], value );
    }
    
    
    // Convenience methods
    
    /** @return The value matching the given environment, or the default value if no matching environment is defined. */
    public double getOrElse( World world, @Nullable BlockPos pos, DoubleField defaultValue ) { return get().getOrElse( world, pos, defaultValue ); }
    
    /** @return The value matching the given environment, or the default value if no matching environment is defined. */
    public double getOrElse( World world, @Nullable BlockPos pos, double defaultValue ) { return get().getOrElse( world, pos, defaultValue ); }
    
    /** @return The value matching the given environment, or null if no matching environment is defined. */
    @Nullable
    public Double get( World world, @Nullable BlockPos pos ) { return get().get( world, pos ); }
}