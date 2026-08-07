package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A key for fuzzy collections that test against or contain resource locations.
 * <br><br>
 * Unlike {@link RegObjKey}, this key type does not have any relations to a registry,
 * and can therefore be useful when dealing with resource locations that do not necessarily
 * point to registry objects, such as loot table IDs.
 */
public class ResourceLocKey extends FuzzyKey<ResourceLocation> {
    
    /** The parser for block state keys. */
    public static IFuzzyKeyParser<ResourceLocation> PARSER = new ResourceLocKey.Parser();
    
    /** @return A new non-blacklist key based on the given resource location. */
    public static ResourceLocKey.Basic of( ResourceLocation value ) {
        return new ResourceLocKey.Basic( value, false );
    }
    
    /** @return A new key based on the given resource location. */
    public static ResourceLocKey.Basic of( ResourceLocation value, boolean blacklist ) {
        return new ResourceLocKey.Basic( value, blacklist );
    }
    
    /** @return A new non-blacklist key based on the given resource location string. */
    public static ResourceLocKey.Basic of( String resLoc ) {
        // noinspection ConstantConditions
        return of( ResourceLocation.tryParse( resLoc ), false );
    }
    
    /** @return A new key based on the given resource location string. */
    public static ResourceLocKey.Basic of( String resLoc, boolean blacklist ) {
        // noinspection ConstantConditions
        return of( ResourceLocation.tryParse( resLoc ), blacklist );
    }
    
    
    /** @return A new key, parsed from a key string, or null if the key was invalid. */
    @Nullable
    public static ResourceLocKey parse( @Nullable IConfigField<?> field, String line, String key, boolean blacklist ) {
        ResourceLocation rl = ResourceLocation.tryParse( key );
        return rl == null ? null : of( rl, blacklist );
    }
    
    
    // ---- Key Implementations ---- //
    
    protected static final String PATTERN = "namespace:path";
    
    protected final ResourceLocation key;
    
    protected ResourceLocKey( ResourceLocation key, boolean blacklist ) {
        super( blacklist );
        this.key = key;
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return key.toString(); }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( ResourceLocation target ) {
        return key.equals( target );
    }
    
    /**
     * A key that matches a resource location.
     */
    public static class Basic extends ResourceLocKey implements IReverseKey<ResourceLocation> {
        
        protected Basic( ResourceLocation resLoc, boolean blacklist ) {
            super( Objects.requireNonNull( resLoc ), blacklist );
        }
        
        /** @return The value that matches this key, or null if anything goes wrong. */
        @Override // IReverseKey
        @Nullable
        public ResourceLocation asValue() {
            return key;
        }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private record Parser( ) implements IFuzzyKeyParser<ResourceLocation> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() { return "Resource Location"; }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return PATTERN;
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
        public FuzzyKey<ResourceLocation> parseKeyString( @Nullable IConfigField<?> field, String line, String key, boolean blacklist ) {
            return parse( field, line, key, blacklist );
        }
    }
}