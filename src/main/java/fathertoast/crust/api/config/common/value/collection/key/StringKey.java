package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * A key for fuzzy collections that test against or contain strings. Spaces are not supported.<p>
 * When used for anything other than matching, the keys function like simple string values.<p>
 * When used for matching, these keys are case-sensitive and provide a number of options:<p>
 * * Strict equals comparison (the default behavior).<p>
 * * Wildcard (*) to match anything, allowed at the beginning and/or end of the key.<p>
 * * Regex pattern matching by using a caret (^) at the beginning of the key.<p>
 * <p>
 * Note that all fuzzy collections have a built-in builder for easily using string-type keys.
 *
 * @see fathertoast.crust.api.config.common.value.collection.FuzzySet.StrBuilder
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyMap.StrBuilder
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyList.StrBuilder
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyValueList.StrBuilder
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyWeightedList.StrBuilder
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyWeightedValueList.StrBuilder
 */
@ApiStatus.Experimental
public abstract class StringKey extends FuzzyKey<String> implements IReverseKey<String> {
    
    /** The parser for string keys. */
    public static final IFuzzyKeyParser<String> PARSER = new Parser();
    
    
    // ---- Key Implementations ---- //
    
    protected final String string;
    
    protected StringKey( String s, boolean blacklist ) {
        super( blacklist );
        string = s;
    }
    
    
    /** @return The value that matches this key, or null if anything goes wrong. */
    @Override // IReverseKey
    public String asValue() { return keyString(); } // Allow the wildcard char to be used like a normal char when not matching
    
    
    public static final String WILDCARD = "*";
    protected static final String PATTERN = "string";
    
    /**
     * A key that matches one specific string.
     */
    @ApiStatus.Experimental
    public static class Basic extends StringKey {
        
        /** @return A new string key. */
        public static Basic of( String string, boolean blacklist ) { return new Basic( string, blacklist ); }
        
        
        protected Basic( String s, boolean blacklist ) { super( s, blacklist ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return string; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( String target ) { return string.equals( target ); }
    }
    
    
    /**
     * A key that matches all strings that contain a specific string.
     */
    @ApiStatus.Experimental
    public static class Contains extends StringKey {
        public static final String PATTERN = WILDCARD + StringKey.PATTERN + WILDCARD;
        
        /** @return A new string contains key, parsed from a key string. */
        public static StartsWith parse( String key, boolean blacklist ) {
            return of( key.substring( WILDCARD.length(), key.length() - WILDCARD.length() ), blacklist );
        }
        
        /** @return A new string contains key. */
        public static StartsWith of( String string, boolean blacklist ) { return new StartsWith( string, blacklist ); }
        
        
        protected Contains( String s, boolean blacklist ) { super( s, blacklist ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return WILDCARD + string + WILDCARD; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( String target ) { return target.contains( string ); }
    }
    
    
    /**
     * A key that matches all strings that start with a specific string.
     */
    @ApiStatus.Experimental
    public static class StartsWith extends StringKey {
        public static final String PATTERN = StringKey.PATTERN + WILDCARD;
        
        /** @return A new string starts with key, parsed from a key string. */
        public static StartsWith parse( String key, boolean blacklist ) {
            return of( key.substring( 0, key.length() - WILDCARD.length() ), blacklist );
        }
        
        /** @return A new string starts with key. */
        public static StartsWith of( String string, boolean blacklist ) { return new StartsWith( string, blacklist ); }
        
        
        protected StartsWith( String s, boolean blacklist ) { super( s, blacklist ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return string + WILDCARD; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( String target ) { return target.startsWith( string ); }
    }
    
    
    /**
     * A key that matches all strings that end with a specific string.
     */
    @ApiStatus.Experimental
    public static class EndsWith extends StringKey {
        public static final String PATTERN = WILDCARD + StringKey.PATTERN;
        
        /** @return A new string ends with key, parsed from a key string. */
        public static EndsWith parse( String key, boolean blacklist ) {
            return of( key.substring( WILDCARD.length() ), blacklist );
        }
        
        /** @return A new string ends with key. */
        public static EndsWith of( String string, boolean blacklist ) { return new EndsWith( string, blacklist ); }
        
        
        protected EndsWith( String s, boolean blacklist ) { super( s, blacklist ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return WILDCARD + string; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( String target ) { return target.endsWith( string ); }
    }
    
    /**
     * A key that matches a string based on a regex pattern.
     */
    @ApiStatus.Experimental
    public static class Regex extends StringKey {
        
        public static final String CODE = "^"; // The regex char for 'start of sequence'
        public static final String PATTERN = CODE + "regex";
        
        /** @return A new string regex key, parsed from a key string. */
        public static Regex parse( String key, boolean blacklist ) {
            return of( key.substring( CODE.length() ), blacklist );
        }
        
        /** @return A new string regex key. */
        public static Regex of( String pattern, boolean blacklist ) { return new Regex( pattern, blacklist ); }
        
        
        protected final Pattern pattern;
        
        protected Regex( String p, boolean blacklist ) {
            super( p, blacklist );
            pattern = Pattern.compile( p );
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return CODE + string; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( String target ) { return pattern.matcher( target ).matches(); }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private record Parser( ) implements IFuzzyKeyParser<String> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() { return "String"; }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return switch( usage ) {
                case MATCH -> TomlHelper.toLiteralList( Basic.PATTERN, Regex.PATTERN, Contains.PATTERN,
                        StartsWith.PATTERN, EndsWith.PATTERN );
                case POLL, ITERATE -> TomlHelper.toLiteralList( Basic.PATTERN );
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
        public FuzzyKey<String> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            if( key.startsWith( Regex.CODE ) ) return Regex.parse( key, blacklist );
            boolean startsWithWildcard = key.startsWith( WILDCARD );
            boolean endsWithWildcard = key.endsWith( WILDCARD );
            return startsWithWildcard && endsWithWildcard ? Contains.parse( key, blacklist ) :
                    startsWithWildcard ? EndsWith.parse( key, blacklist ) :
                            endsWithWildcard ? StartsWith.parse( key, blacklist ) :
                                    Basic.of( key, blacklist );
        }
    }
}