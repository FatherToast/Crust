package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single key-value mapping in a config.
 * <p>
 * Note that it is important for field types to have a non-default implementation of equals(), as this is how the
 * config spec determines when it does not need to rewrite to disk.
 */
public abstract class AbstractConfigField<T> implements IConfigField<T> {
    
    /** @see #getSpec() */
    private CrustConfigSpec SPEC;
    
    /** @see #getKey() */
    private String KEY;
    /** @see #getDefaultValue() */
    private final T DEFAULT;
    /** @see #getComment() */
    private final List<String> COMMENT;
    
    /** @see #getLocalValue() */
    private T localValue;
    /** @see #getRemoteValue() */
    private T remoteValue;
    /** @see #getRemoteValue() */
    private boolean synced;
    
    
    /**
     * Creates a new field with the supplied key and description.
     * If the description is null, it will cancel the entire comment, including the automatic field info text.
     */
    protected AbstractConfigField( String key, T defaultValue, @Nullable String... description ) {
        this( key, defaultValue, description == null ? null : TomlHelper.newComment( description ) );
    }
    
    /**
     * Creates a new field with the supplied key and comment. This method is only used for very special circumstances.
     * If the comment is null, it will cancel the entire comment, including the automatic field info text.
     */
    AbstractConfigField( String key, T defaultValue, @Nullable List<String> comment ) {
        if( !TomlHelper.isValidBareKey( key ) ) {
            throw new IllegalArgumentException( "Key '" + key + "' is invalid! Keys may only contain characters " +
                    "usable for TOML bare dotted keys (A-Za-z0-9_-.)" );
        }
        KEY = key;
        DEFAULT = defaultValue;
        COMMENT = comment == null ? null : Collections.unmodifiableList( comment );
    }
    
    
    // ---- Spec-related Methods ---- //
    
    /** @return The config spec this field exists in. */
    @Override
    public final CrustConfigSpec getSpec() { return SPEC; }
    
    /** @return A description of where to find this field. Primarily used for error reporting/feedback. */
    @Override
    public final String describeLocation() { return "\"" + KEY + "\" in " + SPEC.getFilePath(); }
    
    /** @return The unique config key that maps to this field in the config file. */
    @Override
    public final String getKey() { return KEY; }
    
    /** @return The default value of this field. */
    @Override
    public T getDefaultValue() { return DEFAULT; }
    
    /** @return A list of single-line comments to be placed directly above this field in the config file. */
    @Override
    @Nullable
    public List<String> getComment() { return COMMENT; }
    
    /**
     * Called to set the config spec of this field. Once called, this field can no longer be registered to specs (#define).
     * Note that {@link CrustConfigSpec#define(IConfigField)} calls this method itself, so you rarely need to.
     */
    @Override
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
    
    
    // ---- Value Getting/Saving/Loading Methods ---- //
    
    /** Loads this field's local value from the given value or raw toml. */
    @Override
    public final void load( @Nullable Object raw ) {
        localValue = raw == null ? getDefaultValue() : parse( raw );
    }
    
    /** @return True if this field has been synced with a value from the server. */
    @Override
    public boolean isSynced() { return synced; }
    
    //    /**
    //     * Reads this field's remote value from the byte buffer.
    //     * If deserializing for a sync event, then this sets the remote value as the 'active' one.
    //     */
    //    @Override
    //    public void deserializeRemoteValue( FriendlyByteBuf buffer, boolean forSync ) {
    //        if( forSync ) synced = true;
    //        setRemoteValue( deserialize( buffer ) );
    //    }
    
    /** @return True if the object is a config field with the same key and value. */
    @Override
    public boolean equals( Object other ) {
        return other instanceof IConfigField<?> field &&
                Objects.equals( getKey(), field.getKey() ) && Objects.equals( get(), field.get() );
    }
    
    /** @return Hash code; based on this field's key and value. */
    @Override
    public int hashCode() { return Objects.hash( getKey(), get() ); }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value as assigned in the local config file.
     */
    @Override
    public final T getLocalValue() { return localValue; }
    
    /**
     * If you need the field's value for game logic, use {@link #get()} instead.
     *
     * @return The value assigned to this field externally, if any.
     * A non-null remote value does not necessarily mean this is a synced field, as fields may be synced
     * for the sole purpose of populating the config editor GUI.
     */
    @Override
    @Nullable
    public final T getRemoteValue() { return remoteValue; }
    
    /** Assigns a remote value. This is used for syncing values to or from the server. */
    @Override
    public final void setRemoteValue( @Nullable T value ) { if( value != null || !isSynced() ) remoteValue = value; }
    
    /** Assigns a remote value and makes it 'active'. This is used for syncing values from the server. */
    @Override
    public final void setSyncValue( @Nullable T value ) {
        synced = value != null;
        setRemoteValue( value );
    }
}