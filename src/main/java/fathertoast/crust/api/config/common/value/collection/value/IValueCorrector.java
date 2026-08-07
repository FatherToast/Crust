package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.IConfigField;

import javax.annotation.Nullable;

/**
 * Provides instructions on how to error-correct values on behalf of an {@link IValueCodec}.
 * <p>
 * Value codecs that support custom error correction generally accept the corrector as a parameter.
 *
 * @param <V> The type of value this performs error correction for.
 */
public interface IValueCorrector<V> {
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    String getFormat();
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value to correct, or null if the value is missing.
     * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
     * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
     */
    V correctValue( @Nullable IConfigField<?> field, String line, @Nullable V value );
}