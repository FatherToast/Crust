package fathertoast.crust.api.config.common.field;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Represents a config field with a double value. The entered config value is squared when loaded.
 */
@SuppressWarnings( "unused" )
public class SqrDoubleField extends DoubleField {
    
    /** The underlying field value, squared. */
    private double valueSqr;
    
    /** Creates a new field that accepts a common range of values. */
    public SqrDoubleField( String key, double defaultValue, Range range, @Nullable String... description ) {
        super( key, defaultValue, range, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public SqrDoubleField( String key, double defaultValue, double min, double max, @Nullable String... description ) {
        super( key, defaultValue, min, max, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public SqrDoubleField( String key, double defaultValue, Supplier<Double> min, Supplier<Double> max, @Nullable String... description ) {
        super( key, defaultValue, min, max, description );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        super.load( raw );
        valueSqr = super.get() * super.get();
    }
    
    /** @return Returns the config field's value. */
    @Override
    public double get() { return valueSqr; }
    
    /** @return Returns the square root of the config field's value. */
    public double getSqrRoot() { return super.get(); }
}