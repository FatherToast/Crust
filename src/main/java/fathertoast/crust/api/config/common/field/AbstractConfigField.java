package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.UnsupportedWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single key-value mapping in a config.
 */
public abstract class AbstractConfigField {
    /**
     * @return A description of where to find the field, if possible. Used for error reporting/feedback.
     * Note: Null field references should only exist for default values and non-config loading contexts.
     * Users should only ever see the 'null' message for non-config contexts, since otherwise means there
     * is something wrong with a hard coded default value.
     */
    public static String describeNullable( @Nullable AbstractConfigField field ) {
        return field == null ? "non-config value" : field.describeLocation();
    }
    
    
    /** @see #getSpec() */
    private CrustConfigSpec SPEC;
    
    /** @see #getKey() */
    private String KEY;
    /** @see #getComment() */
    private final List<String> COMMENT;
    
    /**
     * Creates a new field with the supplied key and description.
     * If the description is null, it will cancel the entire comment, including the automatic field info text.
     */
    protected AbstractConfigField( String key, @Nullable String... description ) {
        this( key, description == null ? null : TomlHelper.newComment( description ) );
    }
    
    /**
     * Creates a new field with the supplied key and comment. This method is only used for very special circumstances.
     * If the comment is null, it will cancel the entire comment, including the automatic field info text.
     */
    AbstractConfigField( String key, @Nullable List<String> comment ) {
        if( !TomlHelper.isValidBareKey( key ) ) {
            throw new IllegalArgumentException( "Key '" + key + "' is invalid! Keys may only contain characters " +
                    "usable for TOML bare dotted keys (A-Za-z0-9_-.)" );
        }
        KEY = key;
        COMMENT = comment == null ? null : Collections.unmodifiableList( comment );
    }
    
    /** @return Unwraps this config field (if wrapped) and returns it. */
    public AbstractConfigField unwrap() { return this; }
    
    /** @return The config spec this field exists in. */
    public final CrustConfigSpec getSpec() { return SPEC; }
    
    /** @return A description of where to find this field. Primarily used for error reporting/feedback. */
    public final String describeLocation() { return "\"" + KEY + "\" in " + SPEC.getFilePath(); }
    
    /** @return The unique config key that maps to this field in the config file. */
    public final String getKey() { return KEY; }
    
    /** @return A list of single-line comments to be placed directly above this field in the config file. */
    @Nullable
    public final List<String> getComment() { return COMMENT; }
    
    /**
     * Called to set the config spec of this field. Once called, this field can no longer be registered to specs (#define).
     * Note that {@link CrustConfigSpec#define(AbstractConfigField)} calls this method itself, so you rarely need to.
     */
    public final void setSpec( CrustConfigSpec spec ) {
        if( SPEC != null ) {
            throw new IllegalStateException( "Attempted to register field '" + KEY + "' in two locations; first in " +
                    SPEC.NAME + " and then in " + spec.NAME );
        }
        SPEC = spec;
        KEY = spec.loadingCategory + KEY;
        onSpecSet();
    }
    
    /**
     * Called after the spec is set. Wrapper fields should override this method and call
     * {@link #setSpec(CrustConfigSpec)} on any underlying fields.
     */
    protected void onSpecSet() {}
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    public abstract void appendFieldInfo( List<String> comment );
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    public abstract void load( @Nullable Object raw );
    
    /** @return The value that should be assigned to this field in the config file. */
    @Nullable
    public abstract Object getValue();
    
    /** @return The default value of this field. */
    public abstract Object getDefaultValue();
    
    /** Writes this field's value to file. */
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) { writer.writeValue( getValue(), output ); }
    
    /** @return This field's gui component provider. */
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() { return new UnsupportedWidgetProvider(); }
}