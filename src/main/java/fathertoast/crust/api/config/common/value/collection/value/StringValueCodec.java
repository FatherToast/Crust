package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * An string value codec. Allows spaces when used as the top-level value.
 * The keyword "null" is reserved for use in place of the empty string for missing values.
 * <p>
 * Optionally, you may provide an {@link IValueCorrector} to validate and repair
 * input values and customize the format hint for config field comments.
 */
@SuppressWarnings( "ClassCanBeRecord" )
@ApiStatus.Experimental
public class StringValueCodec implements IValueCodec<String> {
    
    /** A string codec that allows any value (except "null"). */
    public static final StringValueCodec ANY = of( "" );
    
    /** A string codec that allows only valid resource locations. */
    public static final StringValueCodec RES_LOC = resLoc( "" );
    
    /** @return A string codec with a default value that allows only valid resource locations. */
    public static StringValueCodec resLoc( String defaultValue ) { return of( new ResLocCorrector( defaultValue ) ); }
    
    /** @return A string codec with a default. Missing or empty strings will be loaded as the default value. */
    public static StringValueCodec of( String defaultValue ) { return of( new BaseCorrector( defaultValue ) ); }
    
    /** @return A string codec with a line validator. Missing and invalid strings will be loaded as empty strings. */
    public static StringValueCodec of( Predicate<String> validator ) { return of( "", validator ); }
    
    /** @return A string codec with a default and line validator. Missing and invalid strings will be loaded as the default value. */
    public static StringValueCodec of( String defaultValue, Predicate<String> validator ) { return of( new PredicateCorrector( defaultValue, validator ) ); }
    
    /** @return A string codec with a maximum length. Characters beyond the limit are truncated. */
    public static StringValueCodec of( int maxLength ) { return of( "", maxLength ); }
    
    /** @return A string codec with a default and maximum length. Characters beyond the limit are truncated. */
    public static StringValueCodec of( String defaultValue, int maxLength ) { return of( new LengthCorrector( defaultValue, maxLength ) ); }
    
    /** @return A string codec with a custom value corrector. */
    public static StringValueCodec of( IValueCorrector<String> corrector ) { return new StringValueCodec( corrector ); }
    
    
    // ---- Instance Methods ---- //
    
    public final IValueCorrector<String> corrector;
    
    private StringValueCodec( IValueCorrector<String> c ) { corrector = c; }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return corrector.getFormat(); }
    
    /** @return The value, converted to a single-line string. */
    @Override // The empty string doesn't work very nicely as a value
    public String toTomlString( String value ) { return value.isEmpty() ? FuzzyKey.NULL_KEY : value; }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public String parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        return corrector.correctValue( field, line, value == null || value.isEmpty() ||
                FuzzyKey.NULL_KEY.equalsIgnoreCase( value ) ? null : value );
    }
    
    
    // ---- Value Correctors ---- //
    
    /** Simply provides a default value. */
    public static class BaseCorrector implements IValueCorrector<String> {
        
        public final String defaultValue;
        
        public BaseCorrector( String def ) { defaultValue = def; }
        
        /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
        @Override
        public String getFormat() { return "<String>"; }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value to correct, or null if the value is missing.
         * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
         * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
         */
        @Override
        public String correctValue( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
            return value == null ? defaultValue : value;
        }
    }
    
    /** Provides a default value and a string predicate validator; if the validator returns false, it sets to the default. */
    public static class PredicateCorrector extends BaseCorrector {
        
        public final Predicate<String> validator;
        
        public PredicateCorrector( String def, Predicate<String> v ) {
            super( def );
            validator = v;
        }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value to correct, or null if the value is missing.
         * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
         * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
         */
        @Override
        public String correctValue( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
            return value == null || !validator.test( value ) ? defaultValue : value;
        }
    }
    
    /** Provides a default value and a maximum length. Characters beyond the max length are truncated. */
    public static class LengthCorrector extends BaseCorrector {
        
        public final int length;
        
        public LengthCorrector( String def, int l ) {
            super( def );
            if( l < 1 ) throw new IllegalArgumentException( "Max length must be positive!" );
            if( def.length() > l )
                throw new IllegalArgumentException( "Default value cannot be longer than the max length!" );
            length = l;
        }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value to correct, or null if the value is missing.
         * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
         * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
         */
        @Override
        public String correctValue( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
            return value == null ? defaultValue : value.length() > length ? value.substring( 0, length ) : value;
        }
    }
    
    /** Ensures that the string represents a valid resource location. */
    public static class ResLocCorrector extends BaseCorrector {
        
        public ResLocCorrector( String def ) {
            super( def );
            if( !def.isEmpty() && !ResourceLocation.isValidResourceLocation( def ) )
                throw new IllegalArgumentException( "Default value must be a valid resource location!" );
        }
        
        /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
        @Override
        public String getFormat() { return "<namespace:path>"; }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value to correct, or null if the value is missing.
         * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
         * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
         */
        @Override
        public String correctValue( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
            return value == null ? defaultValue : ResourceLocation.isValidResourceLocation( value ) ? value :
                    stripInvalidChars( value );
        }
        
        /** @return The string with all characters that make it invalid as a resource location removed. */
        private static String stripInvalidChars( String resLoc ) {
            final StringBuilder newResLoc = new StringBuilder();
            boolean inPath = resLoc.indexOf( ResourceLocation.NAMESPACE_SEPARATOR ) < 0;
            for( char c : resLoc.toCharArray() ) {
                if( inPath ) {
                    if( ResourceLocation.validPathChar( c ) ) newResLoc.append( c );
                }
                else {
                    if( c == ResourceLocation.NAMESPACE_SEPARATOR ) {
                        newResLoc.append( c );
                        inPath = true;
                    }
                    else if( ResourceLocation.validNamespaceChar( c ) ) newResLoc.append( c );
                }
            }
            return newResLoc.toString();
        }
    }
}