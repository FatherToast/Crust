package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.field.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.field.UnsupportedWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.IStringArray;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single key-value mapping in a config.
 */
public abstract class AbstractConfigField {
    
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
        KEY = key;
        COMMENT = comment == null ? null : Collections.unmodifiableList( comment );
    }
    
    /** @return The config spec this field exists in. */
    public final CrustConfigSpec getSpec() { return SPEC; }
    
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
    protected void onSpecSet() { }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    public abstract void appendFieldInfo( List<String> comment );
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    public abstract void load( @Nullable Object raw );
    
    /** @return The raw toml value that should be assigned to this field in the config file. */
    @Nullable
    public abstract Object getRaw();
    
    /** @return The default raw toml value of this field. */
    public abstract Object getRawDefault();
    
    /** Writes this field's value to file. */
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        Object raw = getRaw();
        if( raw instanceof IStringArray ) {
            writer.writeStringArray( ((IStringArray) raw).toStringList(), output );
        }
        else {
            writer.writeLine( TomlHelper.toLiteral( raw ), output );
        }
    }
    
    /** @return This field's gui component provider. */
    public IConfigFieldWidgetProvider getWidgetProvider() { return new UnsupportedWidgetProvider(); }
}