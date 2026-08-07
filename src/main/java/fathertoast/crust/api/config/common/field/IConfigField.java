package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.UnsupportedWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a single key-value mapping in a config.
 * <p>
 * Note that it is important for field value types to have a non-default implementation of equals(), as
 * this is how the config spec determines when it does not need to rewrite to disk.
 */
public interface IConfigField<T> extends Supplier<T> {
    /**
     * @return A description of where to find the field, if possible. Used for error reporting/feedback.
     * Note: Null field references should only exist for default values and non-config loading contexts.
     * Users should only ever see the 'null' message for non-config contexts, since otherwise means there
     * is something wrong with a hard coded default value.
     */
    static String describeNullable( @Nullable IConfigField<?> field ) {
        return field == null ? "non-config value" : field.describeLocation();
    }
    
    /** @return Unwraps this config field (if wrapped) and returns it. */
    default IConfigField<T> unwrap() { return this; }
    
    
    // ---- Spec-related Methods ---- //
    
    /** @return The config spec this field exists in. */
    CrustConfigSpec getSpec();
    
    /** @return A description of where to find this field. Primarily used for error reporting/feedback. */
    String describeLocation();
    
    /** @return The unique config key that maps to this field in the config file. */
    String getKey();
    
    /** @return The default value of this field. */
    T getDefaultValue();
    
    /** @return A list of single-line comments to be placed directly above this field in the config file. */
    @Nullable
    List<String> getComment();
    
    /**
     * Called to set the config spec of this field. Once called, this field can no longer be registered to specs (#define).
     * Note that {@link CrustConfigSpec#define(IConfigField)} calls this method itself, so you rarely need to.
     */
    void setSpec( CrustConfigSpec spec );
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    void appendFieldInfo( List<String> comment );
    
    
    // ---- Value Getting/Saving/Loading Methods ---- //
    
    /** Loads this field's local value from the given value or raw toml. */
    void load( @Nullable Object raw );
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    T parse( Object raw );
    
    /** @return The value of this config field for use in game logic. */
    @Override // Supplier
    default T get() { //noinspection DataFlowIssue an NPE means you are accessing the field too early
        return isSynced() ? getRemoteValue() : getLocalValue();
    }
    
    /** @return True if this field has been synced with a value from the server. */
    boolean isSynced();
    
    /** Writes this field's value to file. */
    default void writeValue( CrustTomlWriter writer, CharacterOutput output ) { writer.writeValue( getLocalValue(), output ); }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    void serialize( T value, FriendlyByteBuf buffer );
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    T deserialize( FriendlyByteBuf buffer );
    
    //    /**
    //     * Reads this field's remote value from the byte buffer.
    //     * If deserializing for a sync event, then this sets the remote value as the 'active' one.
    //     */
    //    void deserializeRemoteValue( FriendlyByteBuf buffer, boolean forSync );
    
    /** @return This field's gui component provider. */
    @OnClient
    default IConfigFieldWidgetProvider<T> getWidgetProvider() { return new UnsupportedWidgetProvider<>(); }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value as assigned in the local config file.
     */
    T getLocalValue();
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value assigned to this field externally, if any.
     * A non-null remote value does not necessarily mean this is a synced field, as fields may be synced
     * for the sole purpose of populating the config editor GUI.
     */
    @Nullable
    T getRemoteValue();
    
    /** Assigns a remote value. This is used for sending values to or receiving values from the server. */
    void setRemoteValue( @Nullable T value );
    
    /** Assigns a remote value and makes it 'active'. This is used for syncing values from the server. */
    void setSyncValue( @Nullable T value );
}