package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.BooleanFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a boolean value.
 */
@SuppressWarnings( "unused" )
public class BooleanField extends AbstractConfigField<Boolean> {
    
    /** Creates a new field. */
    public BooleanField( String key, boolean defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoValidValues( "Boolean", getDefaultValue(), true, false ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public Boolean parse( Object raw ) {
        Boolean value = TomlHelper.readAsBoolean( this, raw );
        if( value == null ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid boolean! Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        return value;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( Boolean value, FriendlyByteBuf buffer ) { buffer.writeBoolean( value ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public Boolean deserialize( FriendlyByteBuf buffer ) { return buffer.readBoolean(); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<Boolean> getWidgetProvider() { return new BooleanFieldWidgetProvider(); }
}