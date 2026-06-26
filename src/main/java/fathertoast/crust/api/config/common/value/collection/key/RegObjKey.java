package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * A key for fuzzy collections that test against or contain registered objects.
 *
 * @see Registries
 * @see ForgeRegistries
 * @see net.minecraftforge.common.Tags
 * @see fathertoast.crust.api.config.common.value.collection.RegistrySet
 * @see fathertoast.crust.api.config.common.value.collection.RegistryMap
 * @see fathertoast.crust.api.config.common.value.collection.RegistryList
 * @see fathertoast.crust.api.config.common.value.collection.RegistryValueList
 * @see fathertoast.crust.api.config.common.value.collection.RegistryWeightedList
 * @see fathertoast.crust.api.config.common.value.collection.RegistryWeightedValueList
 */
@ApiStatus.Experimental
public abstract class RegObjKey<T> extends FuzzyKey<T> {
    
    /** @return The parser appropriate for a particular registry. */
    public static <T> IFuzzyKeyParser<T> parser( IForgeRegistry<T> registry ) {
        return parser( registry.getRegistryKey() );
    }
    
    /** @return The parser appropriate for a particular registry. */
    public static <T> IFuzzyKeyParser<T> parser( Registry<T> registry ) {
        return parser( registry.key() );
    }
    
    /** @return The parser appropriate for a particular registry key. */
    public static <T> IFuzzyKeyParser<T> parser( ResourceKey<? extends Registry<T>> registryKey ) {
        ResourceLocation regName = registryKey.location();
        if( !PARSERS.containsKey( regName ) ) {
            final Parser<T> parser = new Parser<>( IRegWrapper.forKey( registryKey ) );
            PARSERS.put( regName, parser );
            return parser;
        }
        //noinspection unchecked
        return (Parser<T>) PARSERS.get( regName );
    }
    
    /** @return A new key based on the resource location. */
    public static <T> Basic<T> of( IRegWrapper<T> reg, String resLoc, boolean blacklist ) {
        return of( reg, ResourceLocation.parse( resLoc ), blacklist );
    }
    
    /** @return A new key based on the resource location. */
    public static <T> Basic<T> of( IRegWrapper<T> reg, ResourceLocation resLoc, boolean blacklist ) {
        return new Basic<>( reg, resLoc, blacklist );
    }
    
    /** @return A new key based on the registry object. */
    public static <T> Basic<T> of( IRegWrapper<T> reg, RegistryObject<? extends T> regObj, boolean blacklist ) {
        //noinspection DataFlowIssue
        return of( reg, regObj.getId(), blacklist );
    }
    
    /** @return A new key based on the resource key. */
    public static <T> Basic<T> of( IRegWrapper<T> reg, ResourceKey<? extends T> resKey, boolean blacklist ) {
        return of( reg, resKey.location(), blacklist );
    }
    
    /**
     * @return A new key based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static <T> Basic<T> of( IRegWrapper<T> reg, T obj, boolean blacklist ) {
        return of( reg, Objects.requireNonNull( reg.getKey( obj ) ), blacklist );
    }
    
    /** @return A new wildcard key, based on the partial resource location. */
    public static <T> Wildcard<T> ofWildcard( IRegWrapper<T> reg, ResourceLocation partialResLoc, boolean blacklist ) {
        return ofWildcard( reg, partialResLoc.getNamespace(), partialResLoc.getPath(), blacklist );
    }
    
    /** @return A new wildcard key, based on the namespace. */
    public static <T> Wildcard<T> ofWildcard( IRegWrapper<T> reg, String namespace, boolean blacklist ) {
        return ofWildcard( reg, namespace, "", blacklist );
    }
    
    /** @return A new wildcard key, based on the namespace and partial path. */
    public static <T> Wildcard<T> ofWildcard( IRegWrapper<T> reg, String namespace, String partialPath, boolean blacklist ) {
        return new Wildcard<>( reg, namespace, partialPath, blacklist );
    }
    
    /** @return A new tag key based on the tag resource location. */
    public static <T> Tag<T> ofTag( IRegWrapper<T> reg, String resLoc, boolean blacklist ) {
        return ofTag( reg, ResourceLocation.parse( resLoc ), blacklist );
    }
    
    /** @return A new tag key based on the tag resource location. */
    public static <T> Tag<T> ofTag( IRegWrapper<T> reg, ResourceLocation resLoc, boolean blacklist ) {
        return ofTag( reg, TagKey.create( reg.registryKey(), resLoc ), blacklist );
    }
    
    /** @return A new tag key based on the tag key (well, different kind of tag key). */
    public static <T> Tag<T> ofTag( IRegWrapper<T> reg, TagKey<? extends T> tag, boolean blacklist ) {
        return new Tag<>( reg, tag, blacklist );
    }
    
    
    // ---- Key Implementations ---- //
    
    /** This key's target registry. */
    protected final IRegWrapper<T> registry;
    
    protected RegObjKey( IRegWrapper<T> reg, boolean blacklist ) {
        super( blacklist );
        registry = reg;
    }
    
    
    /**
     * A key that matches one specific registered object.
     */
    @ApiStatus.Experimental
    public static class Basic<T> extends RegObjKey<T> implements IReverseKey<T> {
        public static final String PATTERN = "namespace:path";
        
        /** @return A new key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Basic<T> parse( IRegWrapper<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key );
            return resLoc == null ? null : of( reg, resLoc, blacklist );
        }
        
        
        protected final ResourceLocation resLoc;
        
        protected Basic( IRegWrapper<T> reg, ResourceLocation rl, boolean blacklist ) {
            super( reg, blacklist );
            resLoc = rl;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return resLoc.toString(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) { return resLoc.equals( registry.getKey( target ) ); }
        
        
        /** @return The value that matches this key, or null if anything goes wrong. */
        @Override // IReverseKey
        @Nullable
        public T asValue() { return registry.get( resLoc ); }
    }
    
    
    /**
     * A key that matches all registered objects in a namespace that have a path starting with a specific string.
     */
    @ApiStatus.Experimental
    public static class Wildcard<T> extends RegObjKey<T> {// implements IMultiKey<T> { // Note: We could do this, if we want
        public static final String CODE = "*";
        public static final String PATTERN = "namespace:partial_path" + CODE;
        
        /** @return A new wildcard key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Wildcard<T> parse( IRegWrapper<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key.substring( 0, key.length() - CODE.length() ) );
            return resLoc == null ? null : ofWildcard( reg, resLoc.getNamespace(), resLoc.getPath(), blacklist );
        }
        
        
        protected final String namespace;
        protected final String path;
        
        protected Wildcard( IRegWrapper<T> reg, String ns, String p, boolean blacklist ) {
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
     * A key that matches all registered objects contained by a specific tag.
     */
    @ApiStatus.Experimental
    public static class Tag<T> extends RegObjKey<T> implements IMultiKey<T> {
        public static final String CODE = "#";
        public static final String PATTERN = CODE + "namespace:tag_path";
        
        /** @return A new tag key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static <T> Tag<T> parse( IRegWrapper<T> reg, String key, boolean blacklist ) {
            ResourceLocation resLoc = ResourceLocation.tryParse( key.substring( CODE.length() ) );
            return resLoc == null ? null : ofTag( reg, resLoc, blacklist );
        }
        
        
        protected final TagKey<T> tagKey;
        
        protected Tag( IRegWrapper<T> reg, TagKey<? extends T> tag, boolean blacklist ) {
            super( reg, blacklist );
            //noinspection unchecked
            tagKey = (TagKey<T>) tag;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return CODE + tagKey.location(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) { return registry.tagContains( tagKey, target ); }
        
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Override // IRandomKey
        @Nullable
        public T nextValue( RandomSource random ) { return registry.nextOfTag( tagKey, random ); }
        
        /** @return An iterator over all values that match this key, or null if anything goes wrong. */
        @Override // IMultiKey
        @Nullable
        public Iterator<T> getValueIterator() { return registry.tagIterator( tagKey ); }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private static final Map<ResourceLocation, Parser<?>> PARSERS = new HashMap<>();
    
    private record Parser<T>(IRegWrapper<T> registry) implements IFuzzyKeyParser<T> {
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() {
            return "\"" + ConfigUtil.toString( registry.registryName() ) + "\" Registry";
        }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return switch( usage ) {
                case MATCH -> TomlHelper.toLiteralList( Basic.PATTERN, Wildcard.PATTERN, Tag.PATTERN );
                case POLL, ITERATE -> TomlHelper.toLiteralList( Basic.PATTERN, Tag.PATTERN );
            };
        }
        
        /**
         * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
         * and if the config field is not null, provide useful feedback and identify the field.
         *
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param key   The key string to parse from.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Override
        @Nullable
        public FuzzyKey<T> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            FuzzyKey<T> loadedKey;
            if( key.startsWith( Tag.CODE ) ) {
                loadedKey = Tag.parse( registry, key, blacklist );
                if( field != null ) {
                    if( loadedKey == null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Registry entry has invalid tag key! Must follow pattern \"{}\". Skipping. Entry: {}",
                                Tag.PATTERN, line );
                    }
                    if( !registry.supportsTags() ) {
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