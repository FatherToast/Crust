package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberList;
import fathertoast.crust.api.lib.number.NumberType;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a number list value.
 * Use {@link #entries()} to iterate through the defined list.
 *
 * @param <T> The number type of this list (integer, float, long etc.).
 */
@ApiStatus.Experimental
public class NumberListField<T extends Number> extends FuzzyListField<T, NumberList<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended putting at the top of any file using block lists.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number List fields: General format = [ \"number\", ... ]" );
        comment.add( "  Number Lists are collections of numbers." );
        comment.add( "  The type of number used depends on the field, so make sure to read " +
                "the field description for details." );
        
        comment.add( "" );
        comment.add( "  Unlike other number collections such as Number Map or Number Set, this field type does not support "
                + "the use of special comparison identifiers ('>', '<', '!=' etc.)." );
        
        comment.add( "" );
        comment.add( "  To match a specific range between two values you can use the '~' symbol." );
        comment.add( "  For example, the entry '5~44' will match all values between 5 and 44 (both inclusive)." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, tag and default entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters!" );
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
    public NumberListField( String key, NumberList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return This field's number value type. */
    public NumberType getNumberType() { return getDefaultValue().getNumberType(); }
}
