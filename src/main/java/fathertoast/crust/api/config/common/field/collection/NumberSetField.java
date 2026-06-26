package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberSet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NumberSetField<T extends Number> extends FuzzySetField<T, NumberSet<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number Set fields: General format = [ \"value\", ... ]" );
        comment.add( "  Number Sets are collections of numerical values used exclusively for matching." );
        comment.add( "  The type of numerical value used depends on the field, so make sure to read " +
                "the field description for details." );
        
        comment.add( "" );
        comment.add( "  Entries will only match one exact value, unless a specific comparison identifier is used." );
        comment.add( "  Below is a list of supported comparison identifiers that can be added to the start of " +
                "the entry to match a range of values:" );
        comment.add( "    '<' - Matches all values that are lower than the key's value." );
        comment.add( "    '>' - Matches all values that are greater than the key's value." );
        comment.add( "    '%' - Matches all values that are perfectly divisibly by the key's value." );
        comment.add( "    '<=' - Matches all values that are lower or equal tto the key's value." );
        comment.add( "    '>=' - Matches all values that are greater or equal to key's value." );
        comment.add( "    '!=' - Matches all values that not equal to the key's value." );
        
        comment.add( "" );
        comment.add( "  To match a specific range between two values you can use the '~' symbol." );
        comment.add( "  For example, the entry '5~44' will match all values between 5 and 44 (both inclusive)." );
        
        comment.add( "" );
        comment.add( "  Blacklist entries are supported by this field type. An entry can " +
                "be turned into a blacklist entry by appending 'exclude' to the end of it. For example, '1000 exclude' " +
                "prevents that number from being matched by any entries below it." );
        
        comment.add( "" );
        comment.add( "  A 'default' entry can be added to effectively turn this field type into a blacklist" );
        comment.add( "  This makes it so ALL values get matches, and exceptions can be added by specifying blacklist entries." );
        
        comment.add( "" );
        comment.add( "  Wildcard and tag entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters! Entries are always checked from top to bottom." );
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
    public NumberSetField( String key, NumberSet<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}
