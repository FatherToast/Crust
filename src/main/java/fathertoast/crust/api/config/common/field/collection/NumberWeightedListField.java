package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberWeightedList;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a number weighted list value.
 * Use {@link #next(RandomSource)} to draw a random object, or null if empty or nothing is drawn.
 *
 * @param <T> The type of list (i.e., the number type).
 */
@ApiStatus.Experimental
public class NumberWeightedListField<T extends Number> extends FuzzyWeightedListField<T, NumberWeightedList<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number Weighted List fields: General format = [ \"weight number\", ... ]" );
        comment.add( "  Number Weighted Lists are collections of numbers linked to weights for random selection." );
        comment.add( "  Entries with higher weight are more likely to be chosen, while entries with a weight of 0 will " +
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
    public NumberWeightedListField( String key, NumberWeightedList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return This field's number value type. */
    public NumberKey.NumberType getNumberType() { return getDefaultValue().getNumberType(); }
}
