package fathertoast.crust.api.config.common.value.collection.key;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a key for fuzzy sets and maps that tests against registered objects.
 * <p>
 * Its pattern is simply a resource location (namespace:path).
 */
@ApiStatus.Experimental
public abstract class FuzzyRegKey<T> extends FuzzyKey<T> {
    
    /** The registry this list acts as a subset of. */
    protected final IForgeRegistry<T> registry;
    
    protected FuzzyRegKey( boolean blacklist, IForgeRegistry<T> reg ) {
        super( blacklist );
        registry = reg;
    }
    
    /**
     * Represents a key for fuzzy sets and maps that matches a specific registry object.
     * <p>
     * Its pattern is simply a resource location (namespace:path).
     */
    @ApiStatus.Experimental
    public static class ResLoc<T> extends FuzzyRegKey<T> {
        
        /**
         * @return A new wildcard key, parsed from a partial resource location string ending with a wildcard (*).
         * Null if the partial resource location was invalid.
         */
        @Nullable
        public static <T> ResLoc<T> of( boolean blacklist, IForgeRegistry<T> reg, String resLocString ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( resLocString );
            return resLoc == null ? null : of( blacklist, reg, resLoc );
        }
        
        /** @return A new resource location key based on the resource location. */
        public static <T> ResLoc<T> of( boolean blacklist, IForgeRegistry<T> reg, ResourceLocation resLoc ) {
            return new ResLoc<>( blacklist, reg, resLoc );
        }
        
        /** @return A new resource location key based on the registry object. */
        public static <T> ResLoc<T> of( boolean blacklist, IForgeRegistry<T> reg, RegistryObject<? extends T> regObj ) {
            //noinspection DataFlowIssue
            return of( blacklist, reg, regObj.getId() );
        }
        
        /**
         * @return A new resource location key based on the registered object, or throws an exception if the
         * object is not registered.
         * When building default config values, this is only suitable for vanilla objects.
         */
        public static <T> ResLoc<T> of( boolean blacklist, IForgeRegistry<T> reg, T obj ) {
            return of( blacklist, reg, Objects.requireNonNull( reg.getKey( obj ) ) );
        }
        
        
        protected final ResourceLocation resLoc;
        
        protected ResLoc( boolean blacklist, IForgeRegistry<T> reg, ResourceLocation rl ) {
            super( blacklist, reg );
            resLoc = rl;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return resLoc.toString(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) { return resLoc.equals( registry.getKey( target ) ); }
    }
    
    
    /**
     * Represents a key for fuzzy sets and maps that tests against partial resource locations.
     * <p>
     * Its pattern is the first part of a resource location with an asterisk that will match
     * anything beyond (namespace:path*).
     */
    @ApiStatus.Experimental
    public static class Wildcard<T> extends FuzzyRegKey<T> {
        
        public static final String CODE = "*";
        
        /**
         * @return A new wildcard key, parsed from a partial resource location string ending with a wildcard (*).
         * Null if the partial resource location was invalid.
         */
        @Nullable
        public static <T> Wildcard<T> of( boolean blacklist, IForgeRegistry<T> reg, String wildcardString ) {
            ResourceLocation resLoc = ResourceLocation.tryParse(
                    wildcardString.substring( 0, wildcardString.length() - CODE.length() ) );
            return resLoc == null ? null : of( blacklist, reg, resLoc.getNamespace(), resLoc.getPath() );
        }
        
        /** @return A new wildcard key, based on the partial resource location (NOT ending with a wildcard). */
        public static <T> Wildcard<T> of( boolean blacklist, IForgeRegistry<T> reg, ResourceLocation partialResLoc ) {
            return of( blacklist, reg, partialResLoc.getNamespace(), partialResLoc.getPath() );
        }
        
        /** @return A new wildcard key, based on the namespace and partial path (NOT ending with a wildcard). */
        public static <T> Wildcard<T> of( boolean blacklist, IForgeRegistry<T> reg, String namespace, String partialPath ) {
            return new Wildcard<>( blacklist, reg, namespace, partialPath );
        }
        
        
        protected final String namespace;
        protected final String path;
        
        protected Wildcard( boolean blacklist, IForgeRegistry<T> reg, String ns, String p ) {
            super( blacklist, reg );
            namespace = ns;
            path = p;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return namespace + ResourceLocation.NAMESPACE_SEPARATOR + path + CODE; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            ResourceLocation key = registry.getKey( target );
            return key != null && key.getNamespace().equals( namespace ) && key.getPath().startsWith( path );
        }
    }
    
    
    /**
     * Represents a key for fuzzy sets and maps that tests against tags.
     * <p>
     * Its pattern is a resource location prefixed with a pound sign (#namespace:path).
     */
    @ApiStatus.Experimental
    public static class Tag<T> extends FuzzyRegKey<T> {
        
        public static final String CODE = "#";
        
        /**
         * @return A new tag key, parsed from a resource location string starting with a pound sign (#).
         * Null if the resource location was invalid.
         */
        @Nullable
        public static <T> Tag<T> of( boolean blacklist, IForgeRegistry<T> reg, String tagString ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( tagString.substring( CODE.length() ) );
            return resLoc == null ? null : of( blacklist, reg, resLoc );
        }
        
        /** @return A new tag key based on the resource location. */
        public static <T> Tag<T> of( boolean blacklist, IForgeRegistry<T> reg, ResourceLocation resLoc ) {
            return of( blacklist, reg, TagKey.create( reg.getRegistryKey(), resLoc ) );
        }
        
        /** @return A new tag key based on the tag key (well, different kind of tag key). */
        public static <T> Tag<T> of( boolean blacklist, IForgeRegistry<T> reg, TagKey<? extends T> tag ) {
            return new Tag<>( blacklist, reg, tag );
        }
        
        /** @return A new tag key based on the tag. */
        public static <T> Tag<T> of( boolean blacklist, IForgeRegistry<T> reg, ITag<? extends T> tag ) {
            return of( blacklist, reg, tag.getKey() );
        }
        
        protected final TagKey<T> tagKey;
        
        protected Tag( boolean blacklist, IForgeRegistry<T> reg, TagKey<? extends T> tag ) {
            super( blacklist, reg );
            //noinspection unchecked
            tagKey = (TagKey<T>) tag;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return CODE + tagKey.location(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            ITagManager<T> tags = registry.tags();
            if( tags != null ) {
                Optional<IReverseTag<T>> reverseTag = tags.getReverseTag( target );
                if( reverseTag.isPresent() ) {
                    return reverseTag.get().containsTag( tagKey );
                }
            }
            return false;
        }
    }
}