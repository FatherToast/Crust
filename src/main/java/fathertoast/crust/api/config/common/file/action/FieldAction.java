package fathertoast.crust.api.config.common.file.action;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/** Represents a spec action that reads and writes to a field. */
public class FieldAction<T> implements ISpecAction {
    
    /** The spec this action belongs to. */
    private final CrustConfigSpec PARENT;
    /** The added field info comment. */
    private final ArrayList<String> ADDED_COMMENT;
    /** The provided restart note. */
    private final RestartNote RESTART_NOTE;
    
    /** The underlying config field to perform actions for. */
    public final IConfigField<T> FIELD;
    /** True if the config field should be synced from server. */
    public final boolean SYNC;
    
    /** Create a new field action that will load/create and save the field value. */
    public FieldAction( CrustConfigSpec parent, IConfigField<T> field, boolean sync, @Nullable RestartNote restartNote ) {
        PARENT = parent;
        FIELD = field;
        
        ADDED_COMMENT = new ArrayList<>();
        field.appendFieldInfo( ADDED_COMMENT );
        ADDED_COMMENT.trimToSize();
        SYNC = sync;
        RESTART_NOTE = restartNote;
    }
    
    /** Called when the config is loaded. */
    @Override
    public boolean onLoad() {
        // Get cached value to detect changes
        final Object oldValue = FIELD.getLocalValue();
        
        // Fetch the newly loaded value
        final Object raw = PARENT.getNightConfig().getOptional( FIELD.getKey() ).orElse( null );
        FIELD.load( raw );
        
        // Push the field's value back to the config if its value was changed
        final Object newValue = FIELD.getLocalValue();
        if( raw == null || !Objects.equals( oldValue, newValue ) ) {
            PARENT.getNightConfig().set( FIELD.getKey(), newValue );
            return true;
        }
        return false;
    }
    
    /** Called when the config is saved. */
    @Override
    public void write( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeField( FIELD, RESTART_NOTE, ADDED_COMMENT, output );
    }
    
    /** Called when the config edit screen is opened. */
    @Override
    public void initGui( ICrustConfigGuiSpec guiSpec ) {
        guiSpec.field( FIELD, RESTART_NOTE, ADDED_COMMENT );
    }
    
    /** Serializes a value. */
    public void serialize( Iterator<Object> iterator, FriendlyByteBuf buffer ) { //noinspection unchecked
        FIELD.serialize( (T) iterator.next(), buffer );
    }
    
    /** Deserializes a value. */
    public void deserialize( List<Object> values, FriendlyByteBuf buffer ) { values.add( FIELD.deserialize( buffer ) ); }
    
    /** Applies a deserialized remote value. */
    @SuppressWarnings( "unchecked" )
    public void applyRemote( Iterator<Object> iterator, boolean forSync ) {
        if( forSync ) FIELD.setSyncValue( (T) iterator.next() );
        else FIELD.setRemoteValue( (T) iterator.next() );
    }
    
    /** Applies a deserialized local value. */
    public void applyLocal( Iterator<Object> iterator ) {
        FIELD.getSpec().getNightConfig().set( FIELD.getKey(), iterator.next() );
    }
}