package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;

import javax.annotation.Nullable;
import java.util.PrimitiveIterator;
import java.util.function.Predicate;

/**
 * Parses an environment entry's condition string.
 * <p>
 * Recursively generates sub-parsers for each environment group (environments inside parentheses).
 *
 * @see EnvironmentEntry
 */
public class EnvironmentConditionParser {
    
    /**
     * @param field     The config field we are parsing for.
     * @param line      The entire environment entry line to include in warning feedback.
     * @param condition Condition string to parse.
     * @return Generates and returns a new environment condition parser. You should store a reference to the parser,
     * call {@link #parse()} to get the result, and then call {@link #getCorrectedConditionString()} to get the
     * corrected condition string for saving back to file.
     */
    public static EnvironmentConditionParser of( @Nullable IConfigField<?> field, String line, String condition ) {
        return new EnvironmentConditionParser( field, line, condition );
    }
    
    /**
     * @param condition Condition string to parse.
     * @return Parses the condition string and returns the resulting condition.
     * Use this method only when parsing for something other than a config field.
     */
    public static Predicate<EnvironmentContext> parse( String condition ) {
        return new EnvironmentConditionParser( null, "", condition ).parse();
    }
    
    
    /** The field we are parsing for. If null, all warnings are suppressed. */
    @Nullable
    private final IConfigField<?> field;
    /** The full line of the environment entry; used for warning context. */
    private final String line;
    /** Iterator over the characters in the condition string; this is what we are parsing. */
    private final PrimitiveIterator.OfInt iterator;
    /** What the condition string should look like when formatted nicely; built as we go based on the logic parsed. */
    private final StringBuilder stringCondition;
    /** True if this parser expects close parentheses to terminate it. */
    private final boolean canClose;
    
    /** The current environment string. */
    private final StringBuilder env = new StringBuilder();
    
    /** Top-level predicate, a chain of predicates ORed together. */
    private Predicate<EnvironmentContext> orChain = null;
    /** Intermediate-level predicate, a chain of predicates ANDed together. */
    private Predicate<EnvironmentContext> andChain = null;
    /**
     * A parsed predicate. Outside the environment resolve logic itself, this is only non-null if a
     * lower-level group has been parsed since the last resolve.
     */
    private Predicate<EnvironmentContext> envToAnd = null;
    
    private EnvironmentConditionParser( @Nullable IConfigField<?> f, String l, String c ) {
        field = f;
        line = l;
        iterator = c.chars().iterator();
        stringCondition = new StringBuilder();
        canClose = false;
    }
    
    private EnvironmentConditionParser( @Nullable IConfigField<?> f, String l, PrimitiveIterator.OfInt i, StringBuilder sc ) {
        field = f;
        line = l;
        iterator = i;
        stringCondition = sc;
        canClose = true;
    }
    
    /**
     * @return The parsed condition string, nicely formatted and error-corrected.
     * Returns an empty string if you have not yet called {@link #parse()}.
     */
    public String getCorrectedConditionString() { return stringCondition.toString(); }
    
    /** @return Performs the parse and returns the resulting condition. */
    public Predicate<EnvironmentContext> parse() {
        boolean closed = false;
        while( iterator.hasNext() ) {
            char c = (char) iterator.next().intValue();
            // Recursively process opened parentheses group
            if( c == '(' ) {
                resolveEnv( true );
                
                stringCondition.append( "(" );
                envToAnd = new EnvironmentConditionParser( field, line, iterator, stringCondition ).parse();
            }
            // Just skip excess close parentheses, I guess
            else if( !canClose && c == ')' ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Environment entry has too many closing parentheses! Skipping. Entry: {}", line );
                }
            }
            // Operators that trigger closing resolve
            else if( c == '&' || c == '|' || c == ')' ) {
                resolveEnv( false );
                
                // Perform whatever is needed for the specific operation
                if( c == '&' ) {
                    stringCondition.append( " & " );
                }
                else if( c == '|' ) {
                    resolveAndChain();
                    stringCondition.append( " | " );
                }
                else { // c == ')'
                    closed = true;
                    break;
                }
            }
            // Build the environment string
            else {
                env.append( c );
            }
        }
        
        // Resolve parse and return result
        if( canClose ) {
            if( !closed ) {
                // Was not properly resolved; do now
                resolveEnv( false );
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Environment entry is missing closing parentheses! Adding to end. Entry: {}", line );
                }
            }
            stringCondition.append( ")" );
        }
        else resolveEnv( false );
        resolveAndChain();
        return orChain;
    }
    
    /**
     * Consumes the current environment string and lower-level parsed environment.
     * After call, {@link #env} will be empty and {@link #envToAnd} will be null.
     * If not opening, the {@link #andChain} is guaranteed to be non-null.
     */
    private void resolveEnv( boolean opening ) {
        // Resolve the current environment string
        if( envToAnd == null ) {
            // First resolve or last resolved at an operator
            envToAnd = opening ? consumeEnvNullable() : consumeEnv();
            if( envToAnd != null ) stringCondition.append( ((AbstractEnvironment) envToAnd).toTomlString() );
        }
        else {
            // Last resolved at a group
            String s = env.toString().trim();
            if( s.isEmpty() ) clearEnv(); // Good; clear any excess whitespace
            else {
                AbstractEnvironment orphanedEnv = consumeEnvNullable();
                if( orphanedEnv == null ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Environment entry has unparsable text ({}) between closing parentheses and next operator! Deleting. Entry: {}",
                                s, line );
                    }
                }
                else {
                    // Parse succeeded, AND on the salvaged environment
                    stringCondition.append( " & " ).append( orphanedEnv.toTomlString() );
                    envToAnd = envToAnd.and( orphanedEnv );
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Environment entry is missing operator between closing parentheses and environment! Inserting \"&\" operator. Entry: {}", line );
                    }
                }
            }
        }
        
        // Handle resolve result, if any
        if( envToAnd != null ) {
            if( opening ) {
                stringCondition.append( " & " );
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Environment entry is missing operator between environment and opening parentheses! Inserting \"&\" operator. Entry: {}", line );
                }
            }
            // AND the environment onto the AND chain
            andChain = andChain == null ? envToAnd : andChain.and( envToAnd );
            envToAnd = null;
        }
    }
    
    /** @return A non-null environment parsed from the current environment string; environment string is cleared. */
    private AbstractEnvironment consumeEnv() {
        return CrustEnvironmentRegistry.parse( field, clearEnv() );
    }
    
    /** @return A possibly null environment parsed from the current environment string; environment string is cleared. */
    @Nullable
    private AbstractEnvironment consumeEnvNullable() {
        return CrustEnvironmentRegistry.parseNullable( field, clearEnv() );
    }
    
    /** @return Clears the environment string and returns the string value it held. */
    private String clearEnv() {
        String envString = env.toString();
        env.setLength( 0 );
        return envString;
    }
    
    /** ORs the current AND chain onto the OR chain. Throws an NPE if the AND chain is null. */
    private void resolveAndChain() {
        orChain = orChain == null ? andChain : orChain.or( andChain );
        andChain = null;
    }
}