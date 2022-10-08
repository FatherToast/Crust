package fathertoast.crust.api.config.common.field;

import javax.annotation.Nullable;

/**
 * Represents a config field with a number value.
 */
public abstract class NumberField extends AbstractConfigField {
    
    /** Creates a new field. */
    public NumberField( String key, @Nullable String... description ) { super( key, description ); }
    
    /** @return True if the number is within the range limits of this field. */
    public abstract boolean isInRange( Number number );
}