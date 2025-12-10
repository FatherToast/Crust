package fathertoast.crust.api.config.common.file;

import com.electronwill.nightconfig.core.utils.StringUtils;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.ITomlDoubleValue;
import fathertoast.crust.api.config.common.value.ITomlIntValue;
import fathertoast.crust.api.config.common.value.ITomlValue;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings( "unused" )
public final class TomlHelper {
    
    // ---- Key Helpers ---- //
    
    /** @return The resource location as a string, stripped of any characters disallowed for TOML bare dotted keys. */
    public static String toBareKey( ResourceLocation resLoc ) {
        // Only char in resource locations that is invalid is '/', which is only allowed in the path
        return resLoc.getNamespace() + "." + resLoc.getPath().replace( '/', '.' );
    }
    
    /**
     * @return The string with all characters invalid for use as a TOML bare dotted key removed or changed.
     * Non-trailing/leading whitespace is replaced with '_', while ':', '/', and '\' are changed to '.' -
     * all other characters are deleted.
     */
    public static String toBareKey( String key ) {
        key = key.trim();
        final StringBuilder newKey = new StringBuilder();
        for( char c : key.toCharArray() ) {
            if( isValidBareKeyChar( c ) ) {
                newKey.append( c );
            }
            else if( Character.isWhitespace( c ) ) {
                newKey.append( '_' );
            }
            else if( c == ':' || c == '/' || c == '\\' ) {
                newKey.append( '.' );
            }
        }
        return newKey.toString();
    }
    
    /** @return True if the string is allowed as a TOML bare dotted key (A-Za-z0-9_-.). */
    public static boolean isValidBareKey( String key ) {
        for( int i = 0; i < key.length(); i++ ) {
            if( !isValidBareKeyChar( key.charAt( i ) ) ) return false;
        }
        return true;
    }
    
    /** @return True if the character is allowed in a TOML bare dotted key (A-Za-z0-9_-.). */
    public static boolean isValidBareKeyChar( char c ) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.' || c == '_' || c == '-' || c >= '0' && c <= '9';
    }
    
    
    /** Splits a TOML dotted key into a path. */
    public static List<String> splitKey( String key ) { return StringUtils.split( key, '.' ); }
    
    /** Combines a path into a TOML dotted key. */
    public static String mergePath( @Nullable List<String> path ) {
        if( path == null || path.isEmpty() ) return "";
        StringBuilder key = new StringBuilder();
        Iterator<String> itr = path.listIterator();
        if( itr.hasNext() ) while( true ) {
            key.append( itr.next() );
            if( itr.hasNext() ) key.append( '.' );
            else break;
        }
        return key.toString();
    }
    
    
    // ---- Parsing Helpers ---- //
    
    /** @return The object as a string, or null if it cannot be. */
    @Nullable
    public static String readAsString( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        return raw == null ? null : raw.toString();
    }
    
    /** @return The object as a non-string TOML primitive type, or null if it cannot be. */
    @Nullable
    public static Object readAsPrimitive( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        if( raw instanceof String stringValue ) {
            if( field != null ) {
                ConfigUtil.infoFor( field );
                ConfigUtil.LOG.info( "Unboxing string value \"{}\" to a different primitive.", raw );
            }
            return parseStringPrimitive( stringValue );
        }
        return raw;
    }
    
    /** @return The object as a number, or null if it cannot be. */
    @Nullable
    public static Number readAsNumber( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        return asNumber( readAsPrimitive( field, raw ) );
    }
    
    /** @return The object as a boolean, or null if it cannot be. */
    @Nullable
    public static Boolean readAsBoolean( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        Object value = readAsPrimitive( field, raw );
        if( value instanceof Boolean booleanValue ) return booleanValue;
        if( value instanceof Number numberValue ) {
            boolean newValue = !(numberValue.doubleValue() == 0.0); // 0 is false, anything else is true
            if( field != null ) {
                ConfigUtil.infoFor( field );
                ConfigUtil.LOG.info( "Numerical value given for boolean! Converting value {} to {}.",
                        value, newValue );
            }
            return newValue;
        }
        return null;
    }
    
    /** @return The object as a list of strings, or null if it cannot be. */
    @Nullable
    public static List<String> readAsStringList( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        return raw == null ? null : parseStringList( raw );
    }
    
    /** @return The object as a list of numbers, or null if it cannot be. */
    @Nullable
    public static List<Number> readAsNumberList( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        return raw == null ? null : parseNumberList( field, raw );
    }
    
    /** @return The object as a list of booleans, or null if it cannot be. */
    @Nullable
    public static List<Boolean> readAsBooleanList( @Nullable AbstractConfigField field, @Nullable Object raw ) {
        return raw == null ? null : parseBooleanList( field, raw );
    }
    
    
    /** @return The object cast to number, or null if it cannot be. */
    @Nullable
    public static Number asNumber( @Nullable Object raw ) { return raw instanceof Number ? (Number) raw : null; }
    
    /** @return The string parsed to a number, or null if the parse fails. */
    @Nullable
    public static Number parseNumber( @Nullable String value ) { return asNumber( parseStringPrimitive( value ) ); }
    
    /** @return The object cast to boolean, or null if it cannot be. */
    @Nullable
    public static Boolean asBoolean( @Nullable Object raw ) { return raw instanceof Boolean ? (Boolean) raw : null; }
    
    /** @return The string parsed to a boolean, or null if the parse fails. */
    @Nullable
    public static Boolean parseBoolean( @Nullable String value ) { return asBoolean( parseStringPrimitive( value ) ); }
    
    /** @return The string parsed to a non-string TOML primitive type, or null if the parse fails. */
    @Nullable
    public static Object parseStringPrimitive( @Nullable String value ) {
        if( value != null && !value.isEmpty() ) {
            // Try to parse as a numerical value (long or double)
            try {
                return Long.parseLong( value );
            }
            catch( NumberFormatException ex ) {
                try {
                    return Double.parseDouble( value );
                }
                catch( NumberFormatException ex2 ) {
                    // This is okay; string was not a number
                }
            }
            // Try to parse as a boolean
            if( Boolean.TRUE.toString().equalsIgnoreCase( value ) ) {
                return Boolean.TRUE;
            }
            else if( Boolean.FALSE.toString().equalsIgnoreCase( value ) ) {
                return Boolean.FALSE;
            }
        }
        // Null or failed to parse string
        return null;
    }
    
    /** @return The hex int string (without "0x" prefix) parsed to an integer, or null if the parse fails. */
    @Nullable
    public static Integer parseHexInt( @Nullable String value ) {
        if( value != null ) {
            try {
                return Integer.parseUnsignedInt( value, 16 );
            }
            catch( NumberFormatException ex ) {
                // This is okay; string was not a hex int
            }
        }
        return null;
    }
    
    /** Attempts to convert a TOML literal to a list of strings. */
    public static List<String> parseStringList( Object value ) {
        final List<String> list = new ArrayList<>();
        if( value instanceof List ) {
            // Get all values from the list
            for( Object entry : (List<?>) value ) {
                if( entry != null ) list.add( entry.toString() );
            }
        }
        else {
            // Read non-list as a single item list
            list.add( value.toString() );
        }
        return list;
    }
    
    /** Attempts to convert a TOML literal to a list of numbers. */
    public static List<Number> parseNumberList( @Nullable AbstractConfigField field, Object value ) {
        final List<Number> list = new ArrayList<>();
        if( value instanceof List ) {
            // Get all values from the list
            for( Object entry : (List<?>) value ) {
                Number number = readAsNumber( field, entry );
                if( number != null ) list.add( number );
            }
        }
        else {
            // Read non-list as a single item list
            Number number = readAsNumber( field, value );
            if( number != null ) list.add( number );
        }
        return list;
    }
    
    /** Attempts to convert a TOML literal to a list of booleans. */
    public static List<Boolean> parseBooleanList( @Nullable AbstractConfigField field, Object value ) {
        final List<Boolean> list = new ArrayList<>();
        if( value instanceof List ) {
            // Get all values from the list
            for( Object entry : (List<?>) value ) {
                Boolean bool = readAsBoolean( field, entry );
                if( bool != null ) list.add( bool );
            }
        }
        else {
            // Read non-list as a single item list
            Boolean bool = readAsBoolean( field, value );
            if( bool != null ) list.add( bool );
        }
        return list;
    }
    
    
    // ---- Writing Helpers ---- //
    
    /** @return True if the two objects result in equivalent TOML literals. */
    public static boolean equals( @Nullable Object valueA, @Nullable Object valueB ) {
        return Objects.equals( toLiteral( valueA ), toLiteral( valueB ) );
    }
    
    
    /**
     * Attempts to convert an object to a TOML literal for the use of a comment.
     * Enables truncation of excessively long values.
     */
    public static String toLiteralForComment( @Nullable Object value ) { return toLiteral( value, true ); }
    
    /**
     * Attempts to convert an object to a single-line TOML literal. Only compatible with the following objects:<p>
     * * null (becomes the empty string)<p>
     * * {@link String}s<p>
     * * {@link Number}s<p>
     * * {@link Boolean}s<p>
     * * {@link Enum}s (as by {@link TomlHelper#enumToString(Enum)})<p>
     * * {@link List}s<p>
     * * Objects that implement {@link ITomlValue}<p>
     * * Objects that override {@link Object#toString()} to provide a valid TOML literal<p>
     *
     * @param value The value to convert.
     * @return The value, converted to a single-line TOML literal.
     */
    public static String toLiteral( @Nullable Object value ) { return toLiteral( value, false ); }
    
    /**
     * Attempts to convert an object to a single-line TOML literal. Only compatible with the following objects:<p>
     * * null (becomes the empty string)<p>
     * * {@link String}s<p>
     * * {@link Number}s<p>
     * * {@link Boolean}s<p>
     * * {@link Enum}s (as by {@link TomlHelper#enumToString(Enum)})<p>
     * * {@link List}s<p>
     * * Objects that implement {@link ITomlValue}<p>
     * * Objects that override {@link Object#toString()} to provide a valid TOML literal<p>
     *
     * @param value      The value to convert.
     * @param forComment If true, particularly long values (e.g., many-element lists) should be truncated.
     * @return The value, converted to a single-line TOML literal.
     */
    public static String toLiteral( @Nullable Object value, boolean forComment ) {
        if( value == null ) {
            return toBasicStringLiteral( null );
        }
        else if( value instanceof ITomlValue tomlValue ) {
            return tomlValue.toTomlLiteral( forComment );
        }
        if( value instanceof String stringValue ) {
            return toBasicStringLiteral( stringValue, forComment );
        }
        else if( value instanceof List<?> listValue ) {
            return toArrayLiteral( listValue, forComment );
        }
        else if( value instanceof Enum<?> enumValue ) {
            return toBasicStringLiteral( enumToString( enumValue ), forComment );
        }
        // TOML special float values do not match java's Double#toString() - We do not currently support these anyway
        //else if( value instanceof Double || value instanceof Float ) {
        //    double doubleValue = ((Number) value).doubleValue();
        //    if( Double.isInfinite( doubleValue ) ) return doubleValue > 0.0 ? "inf" : "-inf";
        //    else if( Double.isNaN( doubleValue ) ) return "nan";
        //}
        // By default, assume the object's #toString() method returns its TOML literal; this
        //  generally works for numerical and boolean values, but nothing else unless
        //  #toString() is specifically overridden to return a valid TOML literal
        return value.toString();
        
    }
    
    /** @return The enum value's string representation, as used by configs. */
    public static String enumToString( Enum<?> value ) { return value.name().toLowerCase( Locale.ROOT ); }
    
    /** @return The value as a basic TOML string (surrounded by double quotes). */
    public static String toBasicStringLiteral( @Nullable String value, boolean forComment ) {
        if( forComment ) {
            // Limit to 99 chars or less
            String literal = toBasicStringLiteral( value );
            if( literal.length() < 100 ) return literal;
            return literal.substring( 0, 93 ) + " ... \"";
        }
        return toBasicStringLiteral( value );
    }
    
    /** @return The value as a basic TOML string (surrounded by double quotes). */
    public static String toBasicStringLiteral( @Nullable String value ) { return "\"" + escapeString( value ) + "\""; }
    
    /** @return The value escaped, for inclusion in a basic TOML string. */
    public static String escapeString( @Nullable String value ) {
        if( value == null || value.isEmpty() ) return "";
        StringBuilder literal = new StringBuilder();
        for( char c : value.toCharArray() ) {
            // Escape allowable TOML escape chars (ignoring unicode), otherwise just append as normal
            switch( c ) {
                case '\b':
                    literal.append( "\\b" );
                    break;
                case '\t':
                    literal.append( "\\t" );
                    break;
                case '\n':
                    literal.append( "\\n" );
                    break;
                case '\f':
                    literal.append( "\\f" );
                    break;
                case '\r':
                    literal.append( "\\r" );
                    break;
                case '\"':
                    literal.append( "\\\"" );
                    break;
                case '\\':
                    literal.append( "\\\\" );
                    break;
                default:
                    literal.append( c );
            }
        }
        return literal.toString();
    }
    
    /** @return The value as a literal TOML string (surrounded by single quotes). */
    public static String toLiteralStringLiteral( @Nullable String value, boolean forComment ) {
        if( forComment ) {
            // Limit to 99 chars or less
            String literal = toLiteralStringLiteral( value );
            if( literal.length() < 100 ) return literal;
            return literal.substring( 0, 93 ) + " ... '";
        }
        return toLiteralStringLiteral( value );
    }
    
    /** @return The value as a literal TOML string (surrounded by single quotes). */
    public static String toLiteralStringLiteral( @Nullable String value ) {
        if( value == null ) return "''";
        if( value.contains( "'" ) )
            throw new IllegalArgumentException( "Literal TOML strings may not contain single quotes (')!" );
        return String.format( "'%s'", value );
    }
    
    /** @return The list value as a TOML array literal. */
    public static String toArrayLiteral( @Nullable List<?> value, boolean forComment ) {
        if( forComment && value != null && value.size() > 10 ) {
            // Limit to 9 elements or less
            String literal = toArrayLiteral( value.subList( 0, 9 ) );
            return literal.substring( 0, literal.length() - 2 ) + ", ... ]";
        }
        return toArrayLiteral( value );
    }
    
    /** @return The array value as a TOML array literal. */
    public static String toArrayLiteral( @Nullable Object... value ) {
        return toArrayLiteral( value == null ? null : Arrays.asList( value ) );
    }
    
    /** @return The list value as a TOML array literal. */
    public static String toArrayLiteral( @Nullable List<?> value ) {
        return value == null || value.isEmpty() ? "[]" : "[ " + literalList( value ) + " ]";
    }
    
    /** Use {@link #toArrayLiteral(Object...)} instead; removing this one because was named too ambiguously. */
    @Deprecated( forRemoval = true ) // TODO Remove when updating beyond MC 1.20.1
    public static String toLiteral( @Nullable Object... values ) { return toArrayLiteral( values ); }
    
    
    /**
     * Attempts to convert an object array to a readable list of TOML literals.
     * Not to be confused with a TOML array literal - this does NOT include brackets so it cannot be used as a TOML value.
     */
    public static String toLiteralList( Object... list ) { return literalList( Arrays.asList( list ) ); }
    
    /**
     * Attempts to convert an object list to a readable list of TOML literals.
     * Not to be confused with a TOML array literal - this does NOT include brackets so it cannot be used as a TOML value.
     */
    public static String literalList( @Nullable List<?> valuesList ) {
        if( valuesList == null || valuesList.isEmpty() ) return "";
        StringBuilder literal = new StringBuilder();
        Iterator<?> itr = valuesList.listIterator();
        if( itr.hasNext() ) while( true ) {
            literal.append( toLiteral( itr.next() ) );
            if( itr.hasNext() ) literal.append( ", " );
            else break;
        }
        return literal.toString();
    }
    
    
    /** @return The default field info for a fuzzy set/map field (they print the default value on a separate line). */
    public static String fieldInfoNoDefault( String typeName, String format ) {
        return String.format( "<%s> Format: %s", typeName, format );
    }
    
    /** @return Only the default value for a fuzzy set/map field. */
    public static String fieldInfoOnlyDefault( Object defaultValue ) {
        return String.format( "Default: %s", toLiteralForComment( defaultValue ) );
    }
    
    /** @return The default field info for a field that must provide its help in the field comment. */
    public static String fieldInfoNoHelp( String typeName, Object defaultValue ) {
        return String.format( "<%s> Default: %s", typeName, toLiteralForComment( defaultValue ) );
    }
    
    /** @return The default field info for a field with a value format/structure. */
    public static String fieldInfoFormat( String typeName, Object defaultValue, String format ) {
        return String.format( "<%s> Format: %s, Default: %s", typeName, format, toLiteralForComment( defaultValue ) );
    }
    
    /** @return The default field info for a field with a limited set of valid values. */
    public static String fieldInfoValidValues( String typeName, Object defaultValue, Object... validValues ) {
        return String.format( "<%s> Valid Values: { %s }, Default: %s",
                typeName, TomlHelper.toLiteralList( validValues ), toLiteralForComment( defaultValue ) );
    }
    
    /** @return The default field info for a series of int fields (no defaults listed). */
    public static String multiFieldInfo( IntField.Range range ) { return multiFieldInfo( range.MIN, range.MAX ); }
    
    /** @return The default field info for a series of int fields (no defaults listed). */
    public static String multiFieldInfo( int min, int max ) {
        return String.format( "<%s> Range: %s", "Integer", fieldRange( min, max ) );
    }
    
    /** @return The default field info for a series of int fields (no defaults listed). */
    public static String multiFieldInfo( ITomlIntValue min, ITomlIntValue max ) {
        return String.format( "<%s> Range: %s", "Integer", fieldRange( min, max ) );
    }
    
    /** @return The default field info for a series of double fields (no defaults listed). */
    public static String multiFieldInfo( DoubleField.Range range ) { return multiFieldInfo( range.MIN, range.MAX ); }
    
    /** @return The default field info for a series of double fields (no defaults listed). */
    public static String multiFieldInfo( double min, double max ) {
        return String.format( "<%s> Range: %s", "Number", fieldRange( min, max ) );
    }
    
    /** @return The default field info for a series of double fields (no defaults listed). */
    public static String multiFieldInfo( ITomlDoubleValue min, ITomlDoubleValue max ) {
        return String.format( "<%s> Range: %s", "Number", fieldRange( min, max ) );
    }
    
    /** @return The default field info for a number with a range. */
    public static String fieldInfoRange( int defaultValue, int min, int max ) {
        return fieldInfoRange( "Integer", fieldRange( min, max ), defaultValue );
    }
    
    /** @return The default field info for a number with a range. */
    public static String fieldInfoRange( ITomlIntValue defaultValue, ITomlIntValue min, ITomlIntValue max ) {
        return fieldInfoRange( "Integer", fieldRange( min, max ), defaultValue );
    }
    
    /** @return The default field info for a number with a range. */
    public static String fieldInfoRange( double defaultValue, double min, double max ) {
        return fieldInfoRange( "Number", fieldRange( min, max ), defaultValue );
    }
    
    /** @return The default field info for a number with a range. */
    public static String fieldInfoRange( ITomlDoubleValue defaultValue, ITomlDoubleValue min, ITomlDoubleValue max ) {
        return fieldInfoRange( "Number", fieldRange( min, max ), defaultValue );
    }
    
    /** @return The default field info for a number with a range. */
    private static String fieldInfoRange( String typeName, String range, Object defaultValue ) {
        return String.format( "<%s> Range: %s, Default: %s", typeName, range, toLiteral( defaultValue ) );
    }
    
    /** @return A range representation of TOML literals. */
    public static String fieldRange( int min, int max ) {
        if( min == Integer.MIN_VALUE ) {
            if( max == Integer.MAX_VALUE ) {
                return fieldRangeNoLimit();
            }
            else {
                return fieldRangeUpperLimit( max );
            }
        }
        else if( max == Integer.MAX_VALUE ) {
            return fieldRangeLowerLimit( min );
        }
        else {
            return fieldRangeInterval( min, max );
        }
    }
    
    /** @return A range representation of TOML literals. */
    public static String fieldRange( ITomlIntValue min, ITomlIntValue max ) {
        if( min.get() == Integer.MIN_VALUE ) {
            if( max.get() == Integer.MAX_VALUE ) {
                return fieldRangeNoLimit();
            }
            else {
                return fieldRangeUpperLimit( max );
            }
        }
        else if( max.get() == Integer.MAX_VALUE ) {
            return fieldRangeLowerLimit( min );
        }
        else {
            return fieldRangeInterval( min, max );
        }
    }
    
    /** @return A range representation of TOML literals. */
    public static String fieldRange( double min, double max ) {
        if( min <= -Double.MAX_VALUE ) {
            if( max >= Double.MAX_VALUE ) {
                return fieldRangeNoLimit();
            }
            else {
                return fieldRangeUpperLimit( max );
            }
        }
        else if( max >= Double.MAX_VALUE ) {
            return fieldRangeLowerLimit( min );
        }
        else {
            return fieldRangeInterval( min, max );
        }
    }
    
    /** @return A range representation of TOML literals. */
    public static String fieldRange( ITomlDoubleValue min, ITomlDoubleValue max ) {
        if( min.get() <= -Double.MAX_VALUE ) {
            if( max.get() >= Double.MAX_VALUE ) {
                return fieldRangeNoLimit();
            }
            else {
                return fieldRangeUpperLimit( max );
            }
        }
        else if( max.get() >= Double.MAX_VALUE ) {
            return fieldRangeLowerLimit( min );
        }
        else {
            return fieldRangeInterval( min, max );
        }
    }
    
    /** @return A range representation of TOML literals with no lower or upper limit. */
    private static String fieldRangeNoLimit() { return "Any Value"; }
    
    /** @return A range representation of TOML literals with only an upper limit. */
    private static String fieldRangeUpperLimit( Object max ) { return ConfigUtil.LESS_OR_EQUAL + " " + toLiteral( max ); }
    
    /** @return A range representation of TOML literals with only a lower limit. */
    private static String fieldRangeLowerLimit( Object min ) { return ConfigUtil.GREATER_OR_EQUAL + " " + toLiteral( min ); }
    
    /** @return A range representation of TOML literals with both a lower and upper limit. */
    private static String fieldRangeInterval( Object min, Object max ) { return toLiteral( min ) + " ~ " + toLiteral( max ); }
    
    
    /** Convenience method for creating a list of single-line comments (no \n or \r). */
    public static ArrayList<String> newComment( String... lines ) { return new ArrayList<>( Arrays.asList( lines ) ); }
    
    /** Combines an array of objects as a comma-separated string. */
    public static String combineList( Object... list ) { return combineList( Arrays.asList( list ) ); }
    
    /** Combines a list of objects as a comma-separated string. */
    public static String combineList( @Nullable List<Object> list ) {
        if( list == null || list.isEmpty() ) return "";
        StringBuilder str = new StringBuilder();
        Iterator<Object> itr = list.listIterator();
        if( itr.hasNext() ) while( true ) {
            str.append( itr.next().toString() );
            if( itr.hasNext() ) str.append( ", " );
            else break;
        }
        return str.toString();
    }
}