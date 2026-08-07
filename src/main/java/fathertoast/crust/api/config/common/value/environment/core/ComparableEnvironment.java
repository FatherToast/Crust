package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.AbstractEnvironment;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;

import javax.annotation.Nullable;

/**
 * Boilerplate environment that adds all necessary support for value-comparing ("<", "==", etc.).
 */
public abstract class ComparableEnvironment<V extends Comparable<V>> extends AbstractEnvironment {
    
    /** How the actual value is compared to this environment's value. */
    protected final ComparatorValue COMPARATOR;
    /** This environment's value. */
    protected final V VALUE;
    
    public ComparableEnvironment( ComparatorValue op, V value ) {
        COMPARATOR = op;
        VALUE = value;
    }
    
    public ComparableEnvironment( @Nullable IConfigField<?> field, String value ) {
        String line = name() + " " + value;
        if( value.isEmpty() ) {
            COMPARATOR = ComparatorValue.Codec.ANY.getDefaultValue();
            VALUE = getValueCodec().getDefaultValue();
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Environment entry missing operator and value! Defaulting to \"{}\". Entry: {}",
                        value(), line );
            }
        }
        else {
            String[] args = value.split( " ", 2 );
            String v;
            if( args.length < 2 ) {
                // Not enough arguments; assume the operator was the one left out
                COMPARATOR = ComparatorValue.Codec.ANY.getDefaultValue();
                v = args[0];
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Environment entry missing operator! Must be in the set [ {} ]. Defaulting to \"{}\". Entry: {}",
                            TomlHelper.toLiteralList( (Object[]) ComparatorValue.values() ), COMPARATOR, line );
                }
            }
            else {
                COMPARATOR = ComparatorValue.Codec.ANY.parseTomlString( field, line, args[0] );
                v = args[1].trim();
            }
            VALUE = getValueCodec().parseTomlString( field, line, v );
        }
    }
    
    /** @return The value codec used. */
    protected abstract IValueCodec<V> getValueCodec();
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() { return COMPARATOR.toTomlString() + " " + getValueCodec().toTomlString( VALUE ); }
    
    /** @return True if this environment matches the provided environment. */
    @Override // Predicate
    public boolean test( EnvironmentContext context ) {
        final V actual = getActual( context );
        return actual != null && COMPARATOR.evaluate( actual, VALUE );
    }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Nullable
    protected abstract V getActual( EnvironmentContext context );
}