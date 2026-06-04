package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A key for fuzzy collections that test against entities. Very similar to an EntityType registry
 * object key, but compares against entities directly and allows using a tilde (~) at the start of
 * an entity type reg key to allow matching any entity that extends that entity type's class.
 * <p>
 * Only usable for matching (that is, as a set or map key).
 */
@ApiStatus.Experimental
public abstract class EntityKey extends FuzzyKey<Entity> {
    
    /** The parser for entity keys. */
    public static IFuzzyKeyParser<Entity> PARSER = new Parser();
    
    /** @return A new key based on the resource location. */
    public static Extends extending( String resLoc, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new key based on the resource location. */
    public static Extends extending( ResourceLocation resLoc, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new key based on the registry object. */
    public static Extends extending( RegistryObject<? extends EntityType<?>> regObj, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, regObj, blacklist ) );
    }
    
    /** @return A new key based on the resource key. */
    public static Extends extending( ResourceKey<? extends EntityType<?>> resKey, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resKey, blacklist ) );
    }
    
    /**
     * @return A new key based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static Extends extending( EntityType<?> entityType, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, entityType, blacklist ) );
    }
    
    /** @return A new key based on the resource location. */
    public static Basic of( String resLoc, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new key based on the resource location. */
    public static Basic of( ResourceLocation resLoc, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new key based on the registry object. */
    public static Basic of( RegistryObject<? extends EntityType<?>> regObj, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, regObj, blacklist ) );
    }
    
    /** @return A new key based on the resource key. */
    public static Basic of( ResourceKey<? extends EntityType<?>> resKey, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resKey, blacklist ) );
    }
    
    /**
     * @return A new key based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static Basic of( EntityType<?> entityType, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, entityType, blacklist ) );
    }
    
    /** @return A new wildcard key, based on the partial resource location. */
    public static Basic ofWildcard( ResourceLocation partialResLoc, boolean blacklist ) {
        return of( RegObjKey.ofWildcard( REGISTRY, partialResLoc, blacklist ) );
    }
    
    /** @return A new wildcard key, based on the namespace. */
    public static Basic ofWildcard( String namespace, boolean blacklist ) {
        return of( RegObjKey.ofWildcard( REGISTRY, namespace, blacklist ) );
    }
    
    /** @return A new wildcard key, based on the namespace and partial path. */
    public static Basic ofWildcard( String namespace, String partialPath, boolean blacklist ) {
        return of( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, blacklist ) );
    }
    
    /** @return A new tag key based on the tag resource location. */
    public static Basic ofTag( String resLoc, boolean blacklist ) {
        return of( RegObjKey.ofTag( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new tag key based on the tag resource location. */
    public static Basic ofTag( ResourceLocation resLoc, boolean blacklist ) {
        return of( RegObjKey.ofTag( REGISTRY, resLoc, blacklist ) );
    }
    
    /** @return A new tag key based on the tag key (well, different kind of tag key). */
    public static Basic ofTag( TagKey<? extends EntityType<?>> tag, boolean blacklist ) {
        return of( RegObjKey.ofTag( REGISTRY, tag, blacklist ) );
    }
    
    /** @return A new key based on the entity type registry object key. */
    public static Basic of( FuzzyKey<EntityType<?>> key ) { return new Basic( key ); }
    
    /** @return A new extends key based on the basic entity type registry object key. */
    public static Extends extending( RegObjKey.Basic<EntityType<?>> key ) { return new Extends( key ); }
    
    
    // ---- Key Implementations ---- //
    
    protected static final IRegWrapper<EntityType<?>> REGISTRY = IRegWrapper.of( ForgeRegistries.ENTITY_TYPES );
    protected static final IFuzzyKeyParser<EntityType<?>> REG_PARSER = REGISTRY.getParser();
    
    protected final FuzzyKey<EntityType<?>> regObjKey;
    
    protected EntityKey( FuzzyKey<EntityType<?>> k ) {
        super( k.isBlacklist() );
        regObjKey = k;
    }
    
    
    /**
     * A key that matches based on entity type.
     */
    @ApiStatus.Experimental
    public static class Basic extends EntityKey {
        
        /** @return A new key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static Basic parse( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            FuzzyKey<EntityType<?>> loadedKey = REG_PARSER.parseKeyString( field, line, key, blacklist );
            return loadedKey == null ? null : of( loadedKey );
        }
        
        
        protected Basic( FuzzyKey<EntityType<?>> k ) { super( k ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return regObjKey.keyString(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( Entity target ) { return regObjKey.matches( target.getType() ); }
    }
    
    
    /**
     * A key that matches an entity type, or any entity that extends the entity type's class.
     */
    @ApiStatus.Experimental
    public static class Extends extends Basic {
        public static final String CODE = "~";
        public static final String PATTERN = CODE + RegObjKey.Basic.PATTERN;
        
        /** @return A new extends key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static Extends parse( String key, boolean blacklist ) {
            RegObjKey.Basic<EntityType<?>> loadedKey = RegObjKey.Basic.parse( REGISTRY, key.substring( CODE.length() ), blacklist );
            return loadedKey == null ? null : extending( loadedKey );
        }
        
        
        protected IReverseKey<EntityType<?>> regObjReversible;
        protected Class<? extends Entity> entityClass;
        
        protected Extends( RegObjKey.Basic<EntityType<?>> k ) {
            super( k );
            regObjReversible = k;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return CODE + regObjKey.keyString(); }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( Entity target ) {
            if( super.matches( target ) ) return true;
            checkClass( target.level() );
            return entityClass != null && entityClass.isAssignableFrom( target.getClass() );
        }
        
        /** Called before checking assignability; tries to load the entity type's class if it has not yet been loaded. */
        private void checkClass( @Nullable Level level ) {
            if( entityClass == null && level != null ) {
                EntityType<?> entityType = regObjReversible.asValue();
                if( entityType == null ) return;
                
                // Special case; trying to use the player factory breaks things
                if( entityType == EntityType.PLAYER ) {
                    entityClass = Player.class;
                    return;
                }
                // Try extracting the class via the entity type's factory
                try {
                    Entity entity = entityType.create( level );
                    if( entity != null ) {
                        entityClass = entity.getClass();
                        entity.discard();
                    }
                }
                catch( Exception ex ) {
                    ConfigUtil.LOG.warn( "Failed to load class of entity type \"{}\"! Entry: {}", entityType, this );
                    // noinspection CallToPrintStackTrace
                    ex.printStackTrace();
                }
                // If the factory doesn't work, just kill the instanceof check capability of this key
                if( entityClass == null ) entityClass = ErroredEntity.class;
            }
        }
        
        /** Just used to prevent repeated attempts to use factories that don't work for us. */
        private static abstract class ErroredEntity extends Entity {
            public ErroredEntity( EntityType<?> type, Level level ) { super( type, level ); }
        }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private record Parser() implements IFuzzyKeyParser<Entity> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() { return "Entity"; }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return switch( usage ) {
                case MATCH -> "\"" + Extends.PATTERN + "\", " + REG_PARSER.getPatterns( usage );
                case POLL, ITERATE -> ""; // Not allowed
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
        public FuzzyKey<Entity> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            if( key.startsWith( Extends.CODE ) ) {
                FuzzyKey<Entity> loadedKey = Extends.parse( key, blacklist );
                if( field != null && loadedKey == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Registry entry has invalid extends key! Must follow pattern \"{}\". Skipping. Entry: {}",
                            Extends.PATTERN, line );
                }
                return loadedKey;
            }
            return Basic.parse( field, line, key, blacklist );
        }
    }
}