package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A key for fuzzy sets and maps that tests against registered objects.
 *
 * @see net.minecraftforge.registries.ForgeRegistries
 */
@ApiStatus.Experimental
public abstract class FuzzyRegKey<T> extends FuzzyKey<T> {
    
    /** @return The parser appropriate for a particular registry. */
    public static <T> IFuzzyKeyParser<T> parser( IForgeRegistry<T> registry ) {
        final ResourceLocation regName = registry.getRegistryName();
        if( !PARSERS.containsKey( regName ) ) {
            final Parser<T> parser = new Parser<>( registry );
            PARSERS.put( regName, parser );
            return parser;
        }
        //noinspection unchecked
        return (Parser<T>) PARSERS.get( regName );
    }
    
    
    // ---- Key Implementations ---- //
    
    /** This key's target registry. */
    protected final IForgeRegistry<T> registry;
    
    protected FuzzyRegKey( IForgeRegistry<T> reg, boolean blacklist ) {
        super( blacklist );
        registry = reg;
    }
    
    
    /**
     * A key that matches one specific registered object.
     */
    @ApiStatus.Experimental
    public static class Basic<T> extends FuzzyRegKey<T> {
        public static final String PATTERN = "namespace:path";
        
        /** @return A new key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Basic<T> parse( IForgeRegistry<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key );
            return resLoc == null ? null : of( reg, resLoc, blacklist );
        }
        
        /** @return A new resource location key based on the resource location. */
        public static <T> Basic<T> of( IForgeRegistry<T> reg, ResourceLocation resLoc, boolean blacklist ) {
            return new Basic<>( reg, resLoc, blacklist );
        }
        
        /** @return A new resource location key based on the registry object. */
        public static <T> Basic<T> of( IForgeRegistry<T> reg, RegistryObject<? extends T> regObj, boolean blacklist ) {
            //noinspection DataFlowIssue
            return of( reg, regObj.getId(), blacklist );
        }
        
        /**
         * @return A new resource location key based on the registered object, or throws an exception if the
         * object is not registered.
         * When building default config values, this is only suitable for vanilla objects.
         */
        public static <T> Basic<T> of( IForgeRegistry<T> reg, T obj, boolean blacklist ) {
            return of( reg, Objects.requireNonNull( reg.getKey( obj ) ), blacklist );
        }
        
        
        protected final ResourceLocation resLoc;
        
        protected Basic( IForgeRegistry<T> reg, ResourceLocation rl, boolean blacklist ) {
            super( reg, blacklist );
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
     * A key that matches all registered objects in a namespace that have a path starting with a specific string.
     */
    @ApiStatus.Experimental
    public static class Wildcard<T> extends FuzzyRegKey<T> {
        public static final String CODE = "*";
        public static final String PATTERN = "namespace:partial_path" + CODE;
        
        /** @return A new wildcard key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Wildcard<T> parse( IForgeRegistry<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key.substring( 0, key.length() - CODE.length() ) );
            return resLoc == null ? null : of( reg, resLoc.getNamespace(), resLoc.getPath(), blacklist );
        }
        
        /** @return A new wildcard key, based on the partial resource location. */
        public static <T> Wildcard<T> of( IForgeRegistry<T> reg, ResourceLocation partialResLoc, boolean blacklist ) {
            return of( reg, partialResLoc.getNamespace(), partialResLoc.getPath(), blacklist );
        }
        
        /** @return A new wildcard key, based on the namespace and partial path. */
        public static <T> Wildcard<T> of( IForgeRegistry<T> reg, String namespace, String partialPath, boolean blacklist ) {
            return new Wildcard<>( reg, namespace, partialPath, blacklist );
        }
        
        
        protected final String namespace;
        protected final String path;
        
        protected Wildcard( IForgeRegistry<T> reg, String ns, String p, boolean blacklist ) {
            super( reg, blacklist );
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
     * A key that matches all registered objects belonging to a specific tag.
     */
    @ApiStatus.Experimental
    public static class Tag<T> extends FuzzyRegKey<T> {
        public static final String CODE = "#";
        public static final String PATTERN = CODE + "namespace:tag_path";
        
        /** @return A new tag key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Tag<T> parse( IForgeRegistry<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key.substring( CODE.length() ) );
            return resLoc == null ? null : of( reg, resLoc, blacklist );
        }
        
        /** @return A new tag key based on the tag resource location. */
        public static <T> Tag<T> of( IForgeRegistry<T> reg, ResourceLocation resLoc, boolean blacklist ) {
            return of( reg, TagKey.create( reg.getRegistryKey(), resLoc ), blacklist );
        }
        
        /** @return A new tag key based on the tag key (well, different kind of tag key). */
        public static <T> Tag<T> of( IForgeRegistry<T> reg, TagKey<? extends T> tag, boolean blacklist ) {
            return new Tag<>( reg, tag, blacklist );
        }
        
        
        protected final TagKey<T> tagKey;
        
        protected Tag( IForgeRegistry<T> reg, TagKey<? extends T> tag, boolean blacklist ) {
            super( reg, blacklist );
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
    
    
    // ---- Parser Implementation ---- //
    
    private static final Map<ResourceLocation, Parser<?>> PARSERS = new HashMap<>();
    
    private record Parser<T>(IForgeRegistry<T> registry) implements IFuzzyKeyParser<T> {
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() {
            return "\"" + ConfigUtil.toString( registry.getRegistryName() ) + "\" Registry";
        }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns() {
            return String.format( "\"%s\", \"%s\", \"%s\"", Basic.PATTERN, Wildcard.PATTERN, Tag.PATTERN );
        }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param key   The key string to parse from.
         * @return A new fuzzy key based on the key string.
         */
        @Override
        @Nullable
        public FuzzyKey<T> parseTomlString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            FuzzyKey<T> loadedKey;
            if( key.startsWith( Tag.CODE ) ) {
                loadedKey = Tag.parse( registry, key, blacklist );
                if( field != null ) {
                    if( loadedKey == null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Registry entry has invalid tag key! Must follow pattern \"{}\". Skipping. Entry: {}",
                                Tag.PATTERN, line );
                    }
                    if( registry.tags() == null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Registry entry defines a tag key for a registry that does not support tags! Entry: {}",
                                line );
                    }
                }
            }
            else if( key.endsWith( Wildcard.CODE ) ) {
                loadedKey = Wildcard.parse( registry, key, blacklist );
                if( field != null && loadedKey == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Registry entry has invalid wildcard key! Must follow pattern \"{}\". Skipping. Entry: {}",
                            Wildcard.PATTERN, line );
                }
            }
            else {
                loadedKey = Basic.parse( registry, key, blacklist );
                if( field != null && loadedKey == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Registry entry has invalid key! Must follow pattern \"{}\". Skipping. Entry: {}",
                            Basic.PATTERN, line );
                }
            }
            return loadedKey;
        }
    }
}