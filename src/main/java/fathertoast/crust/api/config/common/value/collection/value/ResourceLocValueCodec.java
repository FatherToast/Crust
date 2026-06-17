package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/** A simple {@code ResourceLocation} value codec. */
@ApiStatus.Experimental
public class ResourceLocValueCodec implements IValueCodec<ResourceLocation> {
    
    /** A strict resource location codec that uses {@link fathertoast.crust.api.util.ResourceLocationUtils#EMPTY} as the default value. */
    public static final ResourceLocValueCodec DEFAULT = of( ResourceLocationUtils.EMPTY, true );
    
    
    /**
     * @param strictParse If true, the returned codec's value corrector will not accept resource location strings without specified namespace.
     * @return A resource location codec with the specified default. Missing or empty strings will be loaded as the default value.
     */
    public static ResourceLocValueCodec of( ResourceLocation defaultValue, boolean strictParse ) {
        return new ResourceLocValueCodec( defaultValue, strictParse );
    }
    
    /**
     * @param strictParse If true, the returned codec's value corrector will not accept resource location strings without specified namespace.
     * @return A resource location codec with the specified default. Missing or empty strings will be loaded as the default value.
     */
    public static ResourceLocValueCodec of( String defaultValue, boolean strictParse ) {
        return new ResourceLocValueCodec( defaultValue, strictParse );
    }
    
    
    // ---- Instance Methods ---- //
    
    public final IValueCorrector<ResourceLocation> corrector;
    
    /**
     * If true, entries will be parsed in "strict" mode,
     * where namespace MUST be specified.
     */
    public final boolean strict;
    
    
    private ResourceLocValueCodec( ResourceLocation defaultValue, boolean strictParse ) {
        corrector = new ResLocCorrector( defaultValue.toString() );
        strict = strictParse;
    }
    
    private ResourceLocValueCodec( String defaultValue, boolean strictParse ) {
        corrector = new ResLocCorrector( defaultValue );
        strict = strictParse;
    }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return corrector.getFormat(); }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( ResourceLocation value ) { return value.toString(); }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public ResourceLocation parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value != null )
            value = stripInvalidChars( value );
        
        if( strict ) {
            return corrector.correctValue( field, line, ResourceLocationUtils.strictTryParse( value ) );
        }
        return corrector.correctValue( field, line, value == null ? null : ResourceLocation.tryParse( value ) );
    }
    
    
    // ---- Value Correctors ---- //
    
    /**
     * Ensures that a default resource location is returned if the value is null.
     */
    public static class ResLocCorrector implements IValueCorrector<ResourceLocation> {
        
        /** The default value of this corrector. */
        private final ResourceLocation defaultValue;
        
        
        /** Creates a new corrector with the given default value. */
        public ResLocCorrector( ResourceLocation def ) {
            // noinspection ConstantConditions
            if( def == null )
                throw new IllegalArgumentException( "Default value cannot be null!" );
            
            defaultValue = def;
        }
        
        /**
         * Alternative constructor that parses a resource location
         * from the specified string. String must contain a valid namespace!
         */
        public ResLocCorrector( String resLocString ) {
            // noinspection ConstantConditions
            this( ResourceLocationUtils.strictTryParse( resLocString ) );
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
        public ResourceLocation correctValue( @Nullable AbstractConfigField field, String line, @Nullable ResourceLocation value ) {
            if( value == null ) return defaultValue;
            return value;
        }
    }
    
    /** @return The string with all characters that make it invalid as a resource location removed. */
    protected static String stripInvalidChars( String resLoc ) {
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
