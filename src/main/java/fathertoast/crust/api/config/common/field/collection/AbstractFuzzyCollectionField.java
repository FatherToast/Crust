package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.AbstractFuzzyCollection;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

/**
 * Boilerplate for fuzzy collection fields.
 *
 * @param <T> The collection type.
 * @param <K> The type of fuzzy key.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 */
public abstract class AbstractFuzzyCollectionField<T, K extends FuzzyKey<T>, C extends AbstractFuzzyCollection<T, K>>
        extends AbstractConfigField<C> {
    
    /** Creates a new field. */
    public AbstractFuzzyCollectionField( String key, C defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public C parse( Object raw ) {
        if( raw instanceof AbstractFuzzyCollection<?, ?> ) {
            try {
                //noinspection unchecked
                return (C) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign fuzzy collection of the wrong type! Falling back to default. Invalid value: {}",
                        raw );
                return getDefaultValue();
            }
        }
        // All the actual loading is done through the object
        try {
            //noinspection unchecked
            C value = (C) getDefaultValue().makeNew();
            value.load( this, raw );
            return value;
        }
        catch( ClassCastException ex ) {
            ConfigUtil.errorFor( this );
            ConfigUtil.LOG.error( "Fuzzy collection factory returns the wrong type! Falling back to default. Invalid factory method: {}#makeNew()",
                    getDefaultValue().getClass().getName() );
            return getDefaultValue();
        }
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( C value, FriendlyByteBuf buffer ) {
        value.serialize( buffer );
    }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public C deserialize( FriendlyByteBuf buffer ) {
        //noinspection unchecked
        C value = (C) getDefaultValue().makeNew();
        value.deserialize( buffer );
        return value;
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<C> getWidgetProvider() {
        return new StringListFieldWidgetProvider<>( AbstractFuzzyCollection::toStringList,
                line -> {
                    final K loaded = getDefaultValue().loadLine( null, line );
                    return loaded != null && (!loaded.isNull() || FuzzyKey.NULL_KEY.equalsIgnoreCase( loaded.keyString() ));
                } );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return True if this contains no elements. */
    public boolean isEmpty() { return get().isEmpty(); }
}