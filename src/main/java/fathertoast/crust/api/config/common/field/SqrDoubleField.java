package fathertoast.crust.api.config.common.field;

import javax.annotation.Nullable;

/**
 * Represents a config field with a double value. The entered config value is squared when loaded.
 */
@SuppressWarnings( "unused" )
public class SqrDoubleField extends DoubleField {
    
    /** Creates a new field that accepts a common range of values. */
    public SqrDoubleField( String key, double defaultValue, Range range, @Nullable String... description ) {
        super( key, defaultValue, range, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public SqrDoubleField( String key, double defaultValue, double min, double max, @Nullable String... description ) {
        super( key, defaultValue, min, max, description );
    }
    
    /** @return Returns the config field's value. */
    @Override
    public Double get() { return super.get() * super.get(); }
    
    /** @return Returns the square root of the config field's value. */
    public double getSqrRoot() { return super.get(); }
}