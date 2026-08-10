package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.SoundPlayerWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.SoundData;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;


/**
 * Represents a config field with a single {@link RegObjKey.Basic} value.
 * This field attempts to fetch its key's registry object value from
 * the registry when {@link RegObjectField#getRegistryObject()} is called.
 */
public class RegObjectField<T> extends AbstractConfigField<RegObjKey.Basic<T>> {
    
    /** Creates a new field. */
    public RegObjectField( String key, RegObjKey.Basic<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
        
        // Sanity checks
        if( defaultValue.isBlacklist() ) {
            throw new IllegalArgumentException( "Registry object field cannot use a blacklist key! Invalid field: " + getKey() );
        }
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, String defaultValue, @Nullable String... description ) {
        this( key, RegObjKey.of( regWrapper, defaultValue, false ), description );
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, ResourceLocation defaultValue, @Nullable String... description ) {
        this( key, RegObjKey.of( regWrapper, defaultValue, false ), description );
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, RegistryObject<? extends T> defaultValue, @Nullable String... description ) {
        this( key, RegObjKey.of( regWrapper, defaultValue, false ), description );
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, ResourceKey<? extends T> defaultValue, @Nullable String... description ) {
        this( key, RegObjKey.of( regWrapper, defaultValue, false ), description );
    }
    
    /**
     * Creates a new field using the given default value. This only works for vanilla registry objects
     * unless you hold off config initialization until after the target registry is populated. This is in
     * other words not possible for dynamic registries.
     */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, T defaultValue, @Nullable String... description ) {
        this( key, RegObjKey.of( regWrapper, defaultValue, false ), description );
    }
    
    /** @return This field's target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().registry(); }
    
    /**
     * @return This field's registry object value.
     * If the field value is not registered, then the registry's default value is returned (null for some registries).
     */
    @Nullable
    public T getRegistryObject() { return get().asValue(); }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "\"" + ConfigUtil.toString( getRegistry().registryName() ) +
                        "\" Registry Object", getDefaultValue(),
                "\"namespace:path\"" ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public RegObjKey.Basic<T> parse( Object raw ) {
        RegObjKey.Basic<?> precastValue;
        if( raw instanceof RegObjKey.Basic<?> key ) {
            precastValue = key;
        }
        else {
            String s = raw.toString();
            FuzzyKey<T> value = getRegistry().getParser().parseKeyString( null, // Suppresses parser's warnings; we'll do our own
                    s, s, false );
            if( !(value instanceof RegObjKey.Basic<?> key) ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Invalid registry object! Must follow the pattern \"namespace:path{tag1:value1,tag2:value2,...}\". Falling back to default ({}). Invalid value: {}",
                        getDefaultValue(), raw );
                return getDefaultValue();
            }
            precastValue = key;
        }
        try {
            if( precastValue.isBlacklist() ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign blacklist to object! Falling back to default. Invalid value: {}",
                        raw );
                return getDefaultValue();
            }
            //noinspection unchecked
            return (RegObjKey.Basic<T>) precastValue;
        }
        catch( ClassCastException ex ) {
            ConfigUtil.errorFor( this );
            ConfigUtil.LOG.error( "Attempted to assign registry object of the wrong type! Falling back to default. Invalid value: {}",
                    raw );
            return getDefaultValue();
        }
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( RegObjKey.Basic<T> value, FriendlyByteBuf buffer ) {
        buffer.writeUtf( value.keyString() );
    }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public RegObjKey.Basic<T> deserialize( FriendlyByteBuf buffer ) {
        return RegObjKey.of( getRegistry(), buffer.readUtf(), false );
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<RegObjKey.Basic<T>> getWidgetProvider() {
        // Special case for sound events
        if( getRegistry().asForgeRegistry() == ForgeRegistries.SOUND_EVENTS ) {
            return new SoundPlayerWidgetProvider<>( key ->
                    SoundData.of( ResourceLocation.parse( key.keyString() ) ), lineValidator() );
        }
        return new EntryViewWidgetProvider<>( RegObjKey.Basic::asValue,
                EntryViewRendererRegistry.getForRegistry( getRegistry().registryKey() ),
                lineValidator() );
        
    }
    
    /** @return A registry based line validator using the given field. */
    public Predicate<String> lineValidator() {
        return ( s ) -> {
            final ResourceLocation id = ResourceLocation.tryParse( s );
            
            if( id != null ) {
                final T regObj = getRegistry().get( id );
                final ResourceLocation defaultKey = getRegistry().getDefaultKey();
                
                if( regObj == null ) return false;
                
                // Make sure we only allow the default value if
                // it was specified intentionally.
                if( defaultKey != null ) {
                    Object defaultValue = getRegistry().get( defaultKey );
                    return regObj != defaultValue || id.equals( defaultKey );
                }
                else return true;
            }
            return false;
        };
    }
}