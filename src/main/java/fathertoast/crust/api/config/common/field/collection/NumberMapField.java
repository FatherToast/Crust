package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.NumberMap;
import fathertoast.crust.api.lib.number.NumberType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a number map value.
 * If the value type is a number, you may use {@link #rollChance(T, RandomSource)} to retrieve the
 * value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 * <p>
 * Allows all primitive number types as keys: bytes, shorts, ints, longs, floats and doubles.
 * <p>
 *
 * @param <T> The number type to match against (integer, float, long etc.).
 * @param <V> The value type.
 */
@ApiStatus.Experimental
public class NumberMapField<T extends Number, V> extends FuzzyMapField<T, V, NumberMap<T, V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Number Map fields: General format = [ \"number value1 value2 ...\", ... ]" );
        comment.add( "  Number Maps are collections of number keys linked to one or more values." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        
        comment.add( "" );
        comment.add( "  Entries will only match one exact value, unless a specific comparison identifier is used." );
        comment.add( "  Below is a list of supported comparison identifiers that can be added to the start of " +
                "the entry to match a range of values:" );
        comment.add( "    '<' - Matches all values that are lower than the key's value." );
        comment.add( "    '>' - Matches all values that are greater than the key's value." );
        comment.add( "    '<=' - Matches all values that are lower or equal tto the key's value." );
        comment.add( "    '>=' - Matches all values that are greater or equal to key's value." );
        comment.add( "    '!=' - Matches all values that not equal to the key's value." );
        comment.add( "    '%' - Matches all values that are perfectly divisibly by the key's value." );
        
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
        comment.add( "  IMPORTANT: The order of entries in this map matters! Entries are always checked from top to bottom, " +
                "and the first matching entry decides which value is assigned." );
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
    public NumberMapField( String key, NumberMap<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return This field's number value type. */
    public NumberType getNumberType() { return getDefaultValue().getNumberType(); }
}
