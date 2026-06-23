package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import net.minecraft.nbt.CompoundTag;
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

import java.util.Objects;

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
    public static final IFuzzyKeyParser<Entity> PARSER = new Parser();
    
    /** @return A new key based on the resource location. */
    public static Extends extending( String resLoc, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new key based on the resource location.
     */
    public static Extends extending( String resLoc, int hierarchySteps, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ), hierarchySteps );
    }
    
    /** @return A new key based on the resource location. */
    public static Extends extending( ResourceLocation resLoc, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ) );
    }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new key based on the resource location.
     */
    public static Extends extending( ResourceLocation resLoc, int hierarchySteps, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resLoc, blacklist ), hierarchySteps );
    }
    
    /** @return A new key based on the registry object. */
    public static Extends extending( RegistryObject<? extends EntityType<?>> regObj, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, regObj, blacklist ) );
    }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new key based on the registry object.
     */
    public static Extends extending( RegistryObject<? extends EntityType<?>> regObj, int hierarchySteps, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, regObj, blacklist ), hierarchySteps );
    }
    
    /** @return A new key based on the resource key. */
    public static Extends extending( ResourceKey<? extends EntityType<?>> resKey, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resKey, blacklist ) );
    }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new key based on the resource key.
     */
    public static Extends extending( ResourceKey<? extends EntityType<?>> resKey, int hierarchySteps, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, resKey, blacklist ), hierarchySteps );
    }
    
    /**
     * @return A new key based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static Extends extending( EntityType<?> entityType, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, entityType, blacklist ) );
    }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new key based on the registered object, or throws an exception if the object is not registered.
     * When building default config values, this is only suitable for vanilla objects.
     */
    public static Extends extending( EntityType<?> entityType, int hierarchySteps, boolean blacklist ) {
        return extending( RegObjKey.of( REGISTRY, entityType, blacklist ), hierarchySteps );
    }
    
    /** @return A new extends key based on the basic entity type registry object key. */
    public static Extends extending( RegObjKey.Basic<EntityType<?>> key ) { return new Extends( key ); }
    
    /**
     * @param hierarchySteps The amount of steps to climb the entity class hierarchy by.
     * @return A new extends key based on the basic entity type registry object key.
     */
    public static Extends extending( RegObjKey.Basic<EntityType<?>> key, int hierarchySteps ) {
        return new Extends( key, hierarchySteps );
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
        public static final String CODE_DEFAULT = "~";
        public static final String CODE_CLIMB = "^";
        public static final String PATTERN = "~ | ~x^" + RegObjKey.Basic.PATTERN;
        
        /** @return A new extends key, parsed from a key string, or null if the key was invalid. */
        @Nullable
        public static Extends parse( String key, @Nullable AbstractConfigField field, boolean blacklist ) {
            RegObjKey.Basic<EntityType<?>> loadedKey;
            Integer steps = null;
            
            // Check if this is a "special" extends key
            // with a number of superclass steps
            if( key.contains( "^" ) ) {
                final String[] args = key.split( "\\^", 2 );
                final boolean missingArg = args.length != 2;
                
                if( !missingArg ) {
                    // Try and parse number of steps
                    try {
                        steps = Integer.parseInt( args[0].replaceFirst( "~", "" ) );
                    }
                    // Default to 0
                    catch( NumberFormatException e ) {
                        steps = 0;
                    }
                }
                loadedKey = RegObjKey.Basic.parse( REGISTRY, missingArg ? args[0] : args[1], blacklist );
            }
            else {
                loadedKey = RegObjKey.Basic.parse( REGISTRY, key.substring( CODE_DEFAULT.length() ), blacklist );
            }
            return loadedKey == null ? null : extending( loadedKey, steps == null ? 0 : steps );
        }
        
        
        protected IReverseKey<EntityType<?>> regObjReversible;
        protected Class<? extends Entity> entityClass;
        protected int hierarchySteps;
        
        
        protected Extends( RegObjKey.Basic<EntityType<?>> k ) {
            this( k, 0 );
        }
        
        protected Extends( RegObjKey.Basic<EntityType<?>> k, int steps ) {
            super( k );
            regObjReversible = k;
            hierarchySteps = steps;
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() {
            final String keyString = regObjKey.keyString();
            
            if( hierarchySteps > 0 ) {
                // Should result in "~X^namespace:path"
                return CODE_DEFAULT + hierarchySteps + CODE_CLIMB + keyString;
            }
            return CODE_DEFAULT + keyString;
        }
        
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
                        Class<? extends Entity> clazz = entity.getClass();
                        // Check if we should climb the class hierarchy
                        if( hierarchySteps > 0 ) {
                            entityClass = climbHierarchy( clazz, hierarchySteps );
                        }
                        else {
                            entityClass = clazz;
                        }
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
        
        /**
         * Climbs the class hierarchy of the specified class by X steps
         * and returns the found super class. If this method ends up climbing all the way up
         * to the base {@link Entity} class, climbing stops and the base entity class is returned.
         *
         * @param entityClass The entity class whose class hierarchy should be climbed.
         * @param steps       The amount of steps to climb. If this is 0 (or less),
         *                    the specified <strong>entityClass</strong> parameter is returned.
         * @return The super class at the end of the climb, OR the base {@link Entity}
         * class if we ended up "climbing too far".
         */
        private static Class<? extends Entity> climbHierarchy( Class<? extends Entity> entityClass, int steps ) {
            // If we are not climbing, just return the first parameter.
            if( steps <= 0 ) return entityClass;
            
            final Class<? extends Entity> originalClass = entityClass;
            
            for( int i = 0; i < steps; ++i ) {
                // Is the current "entity class" assignable from Entity.class?
                if( Entity.class.isAssignableFrom( entityClass ) ) {
                    // noinspection unchecked
                    entityClass = (Class<? extends Entity>) entityClass.getSuperclass();
                }
                else {
                    // We hit the top of the entity class hierarchy, return Entity.class
                    return Entity.class;
                }
            }
            // Make sure the class isn't null for whatever bizarre reason
            // and return the original parameter if so.
            return Objects.requireNonNullElse( entityClass, originalClass );
        }
        
        /** Just used to prevent repeated attempts to use factories that don't work for us. */
        public static class ErroredEntity extends Entity {
            public ErroredEntity( EntityType<?> type, Level level ) { super( type, level ); }
            
            @Override
            protected void defineSynchedData() { }
            
            @Override
            protected void readAdditionalSaveData( CompoundTag tag ) { }
            
            @Override
            protected void addAdditionalSaveData( CompoundTag tag ) { }
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
            if( key.startsWith( Extends.CODE_DEFAULT ) ) {
                FuzzyKey<Entity> loadedKey = Extends.parse( key, field, blacklist );
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