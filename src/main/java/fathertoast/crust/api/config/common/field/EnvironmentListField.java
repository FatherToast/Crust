package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.environment.EnvironmentList;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a config field with an environment list value.
 */
public class EnvironmentListField<V> extends AbstractConfigField<EnvironmentList<V>> {
    
    /**
     * Provides a description of how to use environment lists. Recommended to put at the top of any file using environment lists.
     * Always put the environment condition descriptions at the bottom of the file if this is used!
     */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Environment List fields: General format =" );
        comment.add( "    [ \"value environment1 condition1 & environment2 condition2 & ...\", ... ]" );
        comment.add( "  Environment lists are arrays of environment entries. Each entry is a value followed by the " +
                "environment conditions that must be satisfied for the value to be chosen. The environments are tested " +
                "in the order listed, and the first matching entry is chosen." );
        
        comment.add( "See the bottom of this file for a detailed explanation on each environment condition available." );
        return comment;
    }
    
    /**
     * Inserts the first part of a detailed description of how to use this field type.
     * Recommended to include either in a README or at the start of each config that contains any field of this type.
     * <br><br>
     * This is NOT shown in the GUI.
     */
    public static void describe1of2( CrustConfigSpec spec ) { spec.paddedFileOnlyComment( verboseDescription() ); }
    
    /**
     * Inserts the second and last part of a detailed description of how to use this field type.
     * Should go at the bottom of a config, preferably after the appendix header (if used).
     * <br><br>
     * This is NOT shown in the GUI.
     */
    public static void describe2of2( CrustConfigSpec spec ) {
        spec.paddedFileOnlyComment( CrustEnvironmentRegistry.getDescriptions() );
    }
    
    
    /** This list's value codec. */
    protected final IValueCodec<V> valueCodec;
    /** Number of arguments used by the value codec. */
    protected final int valueArgs;
    
    /** Creates a new field. */
    public EnvironmentListField( String key, EnvironmentList<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
        valueCodec = defaultValue.codec();
        valueArgs = defaultValue.args();
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Environment List", getDefaultValue(),
                "[ \"value condition1 state1 | condition2 state2 & ...\", ... ]" ) );
        comment.add( "   Values: " + TomlHelper.fieldInfoNoHelp( valueCodec.getFormat(), valueCodec.getDefaultValue() ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public EnvironmentList<V> parse( Object raw ) {
        if( raw instanceof EnvironmentList<?> ) {
            try {
                //noinspection unchecked
                return (EnvironmentList<V>) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign environment collection of the wrong type! Falling back to default. Invalid value: {}",
                        raw );
                return getDefaultValue();
            }
        }
        // All the actual loading is done through the object
        EnvironmentList<V> value = new EnvironmentList<>( valueCodec, valueArgs );
        value.load( this, raw );
        return value;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( EnvironmentList<V> value, FriendlyByteBuf buffer ) {
        value.serialize( buffer );
    }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public EnvironmentList<V> deserialize( FriendlyByteBuf buffer ) {
        EnvironmentList<V> value = new EnvironmentList<>( valueCodec, valueArgs );
        value.deserialize( buffer );
        return value;
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<EnvironmentList<V>> getWidgetProvider() {
        return new StringListFieldWidgetProvider<>( EnvironmentList::toStringList,
                line -> {
                    EnvironmentEntry<V> loaded = new EnvironmentEntry<>( null, valueCodec, valueArgs, line );
                    return ConfigUtil.noSpaces( line ).equalsIgnoreCase( ConfigUtil.noSpaces( loaded.toTomlString() ) );
                } );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or null if no matching environment is defined.
     */
    @Nullable
    public V get( EnvironmentContext context ) { return get().get( context ); }
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or the default value if no matching environment is defined.
     */
    public V getOrElse( EnvironmentContext context, V defaultValue ) { return get().getOrElse( context, defaultValue ); }
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or the default value if no matching environment is defined.
     */
    public V getOrElse( EnvironmentContext context, Supplier<V> defaultValue ) { return get().getOrElse( context, defaultValue ); }
    
    /**
     * Note: This method is less preferred over the others, as the codec default is not really
     * configurable. However, in some cases this may be perfectly acceptable.
     *
     * @return The value for the first entry that matches the given environment context,
     * or the codec's default value if no matching environment is defined.
     */
    public V getOrDefault( EnvironmentContext context ) { return get().getOrDefault( context ); }
    
    /** @return True if this contains no elements. */
    public boolean isEmpty() { return get().isEmpty(); }
}