package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Wraps a single key-value mapping in a config.
 * <p>
 * When constructing, ensure that you pass the outermost wrapper to {@link CrustConfigSpec#define} so
 * that it functions correctly. You can then call {@link #field()} on the wrapper returned by #define
 * if you would like to store a reference to the inner/wrapped field instead of the wrapper.
 * <p>
 * This is a boilerplate wrapper intended to simplify field wrapper implementations by taking care of
 * all basic functionalities the wrapper needs to provide to the wrapped field.
 */
public abstract class AbstractWrapperField<T, F extends IConfigField<T>> implements IConfigField<T> {
    
    /** The wrapped field. */
    private final F wrappedField;
    
    /** Creates a new wrapper field around the supplied field. */
    public AbstractWrapperField( F wrapped ) { wrappedField = wrapped; }
    
    /** @return Returns the wrapped config field. */
    public F field() { return wrappedField; }
    
    /** @return Unwraps this config field (if wrapped) and returns it. */
    @Override
    public IConfigField<T> unwrap() { return field().unwrap(); }
    
    
    // ---- Spec-related Methods ---- //
    
    /** @return The config spec this field exists in. */
    @Override
    public final CrustConfigSpec getSpec() { return wrappedField.getSpec(); }
    
    /** @return A description of where to find this field. Primarily used for error reporting/feedback. */
    @Override
    public String describeLocation() { return wrappedField.describeLocation(); }
    
    /** @return The unique config key that maps to this field in the config file. */
    @Override
    public String getKey() { return wrappedField.getKey(); }
    
    /** @return The default value of this field. */
    @Override
    public T getDefaultValue() { return wrappedField.getDefaultValue(); }
    
    /** @return A list of single-line comments to be placed directly above this field in the config file. */
    @Override
    @Nullable
    public List<String> getComment() { return wrappedField.getComment(); }
    
    /**
     * Called to set the config spec of this field. Once called, this field can no longer be registered to specs (#define).
     * Note that {@link CrustConfigSpec#define(IConfigField)} calls this method itself, so you rarely need to.
     */
    @Override
    public void setSpec( CrustConfigSpec spec ) { wrappedField.setSpec( spec ); }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) { wrappedField.appendFieldInfo( comment ); }
    
    
    // ---- Value Getting/Saving/Loading Methods ---- //
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) { wrappedField.load( raw ); }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public T parse( Object raw ) { return wrappedField.parse( raw ); }
    
    /** @return The value of this config field for use in game logic. */
    @Override // Supplier
    public T get() { return wrappedField.get(); }
    
    /** @return True if this field has been synced with a value from the server. */
    @Override
    public boolean isSynced() { return wrappedField.isSynced(); }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) { wrappedField.writeValue( writer, output ); }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize(FriendlyByteBuf)}. */
    @Override
    public void serialize( T value, FriendlyByteBuf buffer ) { wrappedField.serialize( value, buffer ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize(T, FriendlyByteBuf)}. */
    @Override
    public T deserialize( FriendlyByteBuf buffer ) { return wrappedField.deserialize( buffer ); }
    
    //    /**
    //     * Reads this field's remote value from the byte buffer.
    //     * If deserializing for a sync event, then this sets the remote value as the 'active' one.
    //     */
    //    @Override
    //    public void deserializeRemoteValue( FriendlyByteBuf buffer, boolean forSync ) { wrappedField.deserializeRemoteValue( buffer, forSync ); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<T> getWidgetProvider() { return wrappedField.getWidgetProvider(); }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value as assigned in the local config file.
     */
    @Override
    public T getLocalValue() { return wrappedField.getLocalValue(); }
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value assigned to this field externally, if any.
     * A non-null remote value does not necessarily mean this is a synced field, as fields may be synced
     * for the sole purpose of populating the config editor GUI.
     */
    @Override
    @Nullable
    public T getRemoteValue() { return wrappedField.getRemoteValue(); }
    
    /** Assigns a remote value. This is used for syncing values to or from the server. */
    @Override
    public void setRemoteValue( @Nullable T value ) { wrappedField.setRemoteValue( value ); }
    
    /** Assigns a remote value and makes it 'active'. This is used for syncing values from the server. */
    @Override
    public void setSyncValue( @Nullable T value ) { wrappedField.setSyncValue( value ); }
}