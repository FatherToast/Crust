package fathertoast.crust.api.util;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/** Contains various convenience methods related to {@link ResourceLocation}. */
public final class ResourceLocationUtils {
    
    /**
     * A ResourceLocation whose purpose is to point to "nothing" in the context it is being used.
     */
    public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "empty" );
    
    
    /** @return True if the given ResourceLocation is null or {@link ResourceLocationUtils#EMPTY}. */
    public static boolean isEmpty( @Nullable ResourceLocation rl ) {
        return rl == null || rl.equals( EMPTY );
    }
    
    /** @return A ResourceLocation parsed from the specified String, or the supplied default value if something went wrong. */
    @Nullable
    public static ResourceLocation parseOrDefault( String value, @Nullable ResourceLocation defaultValue ) {
        ResourceLocation rl = strictTryParse( value );
        if( rl == null ) return defaultValue;
        return rl;
    }
    
    /**
     * Operates similarly to {@link ResourceLocation#tryParse(String)}, except
     * the specified String must consist of both namespace, separator and path, following the
     * expected format of "namespace:path".
     *
     * @param value The String to try and parse. Can be null.
     * @return A ResourceLocation parsed from the specified String, or null if something went wrong.
     */
    @Nullable
    public static ResourceLocation strictTryParse( @Nullable String value ) {
        if( value == null ) return null;
        
        String[] components = value.split( ":" );
        if( components.length != 2 ) return null;
        
        try {
            return ResourceLocation.fromNamespaceAndPath( components[0], components[1] );
        }
        catch( Exception ignored ) {
            return null;
        }
    }
    
    /**
     * @return True if the specified String is a full, valid ResourceLocation.
     * Unlike {@link ResourceLocation#isValidResourceLocation(String)}, this
     * returns false if namespace is not present.
     */
    public static boolean strictIsValid( @Nullable String value ) {
        if( value == null ) return false;
        
        String[] components = value.split( ":" );
        if( components.length != 2 ) return false;
        return ResourceLocation.isValidNamespace( components[0] ) && ResourceLocation.isValidPath( components[1] );
    }
    
    
    // Utility class
    private ResourceLocationUtils() { }
}
