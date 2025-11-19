package fathertoast.crust.api.config.common.value.collection.key;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a key for fuzzy sets and maps that tests against resource locations.
 * <p>
 * Its pattern is simply a resource location (namespace:path).
 */
@ApiStatus.Experimental
public class ResLocKey extends FuzzyKey<ResourceLocation> { //TODO Res loc is definitely the wrong type here
    
    //public static ResLocKey get() { return new ResLocKey(); }
    
    
    private final ResourceLocation resLoc;
    
    private ResLocKey( ResourceLocation rl, boolean blacklist ) {
        super( blacklist );
        resLoc = rl;
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    public String keyString() { return resLoc.toString(); }
    
    /** @return True if this key matches the target. */
    public boolean matches( ResourceLocation target ) { return resLoc.equals( target ); }
    
    
    /**
     * Represents a key for fuzzy sets and maps that tests against partial resource locations.
     * <p>
     * Its pattern is the first part of a resource location with an asterisk that will match
     * anything beyond (namespace:path*).
     */
    @ApiStatus.Experimental
    public static class Wildcard extends FuzzyKey<ResourceLocation> {
        
        public static final char CODE = '*';
        
        
        private final String namespace;
        private final String path;
        
        private Wildcard( String ns, String p, boolean blacklist ) {
            super( blacklist );
            namespace = ns;
            path = p;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        public String keyString() { return namespace + ResourceLocation.NAMESPACE_SEPARATOR + path + CODE; }
        
        /** @return True if this key matches the target. */
        public boolean matches( ResourceLocation target ) {
            return target.getNamespace().equals( namespace ) && target.getPath().startsWith( path );
        }
    }
    
    
    /**
     * Represents a key for fuzzy sets and maps that tests against tags.
     * <p>
     * Its pattern is a resource location prefixed with a pound sign (#namespace:path).
     */
    @ApiStatus.Experimental
    public static class Tag extends ResLocKey {
        
        public static final char CODE = '#';
        
        
        private Tag( ResourceLocation rl, boolean blacklist ) { super( rl, blacklist ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        public String keyString() { return CODE + super.keyString(); }
        
        /** @return True if this key matches the target. */
        public boolean matches( ResourceLocation target ) {
            return false;//TODO
        }
    }
}