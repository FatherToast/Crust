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
     * @param strictParse If true, the returned codec will not accept resource location strings without specified namespace when parsing.
     * @return A resource location codec with the specified default. Missing or empty strings will be loaded as the default value.
     */
    public static ResourceLocValueCodec of( ResourceLocation defaultValue, boolean strictParse ) {
        return new ResourceLocValueCodec( defaultValue, strictParse );
    }
    
    /**
     * @param strictParse If true, the returned codec will not accept resource location strings without specified namespace when parsing.
     * @return A resource location codec with the specified default. Missing or empty strings will be loaded as the default value.
     */
    public static ResourceLocValueCodec of( String defaultValue, boolean strictParse ) {
        return new ResourceLocValueCodec( defaultValue, strictParse );
    }
    
    
    // ---- Instance Methods ---- //
    
    /** The default value of this codec. */
    public final ResourceLocation defaultValue;
    /**
     * If true, entries will be parsed in "strict" mode,
     * where namespace MUST be specified.
     */
    public final boolean strict;
    
    
    private ResourceLocValueCodec( ResourceLocation def, boolean strictParse ) {
        defaultValue = def;
        strict = strictParse;
    }
    
    private ResourceLocValueCodec( String def, boolean strictParse ) {
        defaultValue = ResourceLocationUtils.strictTryParse( def );
        strict = strictParse;
        
        if( defaultValue == null )
            throw new IllegalArgumentException( "Default value cannot be null!" );
    }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return "<namespace:path>"; }
    
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
    @SuppressWarnings( "ConstantConditions" )
    public ResourceLocation parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value != null )
            value = stripInvalidChars( value );
        
        if( strict ) {
            return ResourceLocationUtils.strictParseOrDefault( value, defaultValue );
        }
        return ResourceLocationUtils.parseOrDefault( value, defaultValue );
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
