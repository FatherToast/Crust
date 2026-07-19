package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberList;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a number list value.
 * Use {@link #entries()} to iterate through the defined list.
 */
@ApiStatus.Experimental
public class NumberListField<T extends Number> extends FuzzyListField<T, NumberList<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended putting at the top of any file using block lists.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number List fields: General format = [ \"value\", ... ]" );
        comment.add( "  Number Lists are collections of numbers." );
        comment.add( "  The type of number used depends on the field, so make sure to read " +
                "the field description for details." );
        
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
}
