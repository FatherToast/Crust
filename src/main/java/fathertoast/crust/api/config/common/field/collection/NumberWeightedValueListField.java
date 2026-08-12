package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberWeightedValueList;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.lib.number.NumberType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a number weighted value list value.
 * Use {@link #next(RandomSource)} to draw a random key-value pair, or null if empty or nothing is drawn.
 * <p>
 * Allows any value type that has a codec.
 * <p>
 * Allows all primitive number types as keys: bytes, shorts, ints, longs, floats and doubles.
 *
 * @param <T> The type of list (i.e., the number type).
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 * @see NumberKey
 * @see Number
 */
@ApiStatus.Experimental
public class NumberWeightedValueListField<T extends Number, V> extends FuzzyWeightedValueListField<T, V, NumberWeightedValueList<T, V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number Weighted Value List fields: General format = [ \"weight number value1 value2 ...\", ... ]" );
        comment.add( "  Number Weighted Value Lists are collections of number keys linked to one " +
                "or more values, in addition to weights for random selection." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        comment.add( "  Entry-value pairs with higher weight are more likely to be chosen, while pairs with a weight of 0 will " +
                "never be chosen. Weights cannot be negative." );
        
        comment.add( "" );
        comment.add( "  Unlike other number collections such as Number Map or Number Set, this field type does not support "
                + "the use of special comparison identifiers ('>', '<', '!=' etc.)." );
        
        comment.add( "" );
        comment.add( "  Specific range entries (defined by the '~' symbol) are not supported by this number field type." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, tag and default entries are not supported by this field type." );
        return comment;
    }
    
    /**
     * Inserts a detailed description into the given spec of how to use this field type.
     * Recommended to include either in a README or at the start of each config that contains any field of this type.
     * <br><br>
     * This is NOT shown in the GUI.
     */
    public static void describe( CrustConfigSpec spec ) {
        spec.paddedFileOnlyComment( verboseDescription() );
    }
    
    /** Creates a new field. */
    public NumberWeightedValueListField( String key, NumberWeightedValueList<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return This field's number value type. */
    public NumberType getNumberType() { return getDefaultValue().getNumberType(); }
}
