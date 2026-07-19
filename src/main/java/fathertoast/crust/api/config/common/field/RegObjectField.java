package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.SoundPlayerWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.util.OnClient;
import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * Represents a config field with a single {@link RegObjKey.Basic} value.
 * This field attempts to fetch its key's registry object value from
 * the registry once {@link RegObjectField#getRegistryObject()} is called.
 */
@ApiStatus.Experimental
public class RegObjectField<T> extends GenericField<RegObjKey.Basic<T>> {
    
    /** A wrapper holding the registry associated with the type of registry object this field contains. */
    private final IRegWrapper<T> WRAPPED_REGISTRY;
    
    
    /** @return A non-blacklist, basic registry object key of the given ID. */
    public static <T> RegObjKey.Basic<T> of( ResourceLocation id, IRegWrapper<T> regWrapper ) {
        return RegObjKey.of( regWrapper, id, false );
    }
    
    /**
     * @return A non-blacklist, basic registry object key of the target registry's
     * default value key. If the registry does not have one, {@link ResourceLocationUtils#EMPTY} is picked instead.
     */
    public static <T> RegObjKey.Basic<T> defaultKey( IRegWrapper<T> regWrapper ) {
        ResourceLocation defaultKey = regWrapper.getDefaultKey();
        if( defaultKey == null ) defaultKey = ResourceLocationUtils.EMPTY;
        return RegObjKey.of( regWrapper, defaultKey, false );
    }
    
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, String resLoc, @Nullable String... description ) {
        super( key, RegObjKey.of( regWrapper, resLoc, false ), description );
        WRAPPED_REGISTRY = regWrapper;
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, ResourceLocation defaultResLoc, @Nullable String... description ) {
        super( key, RegObjKey.of( regWrapper, defaultResLoc, false ), description );
        WRAPPED_REGISTRY = regWrapper;
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, RegistryObject<? extends T> regObj, @Nullable String... description ) {
        // noinspection DataFlowIssue
        this( key, regWrapper, regObj.getId(), description );
    }
    
    /** Creates a new field. */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, ResourceKey<? extends T> resourceKey, @Nullable String... description ) {
        this( key, regWrapper, resourceKey.location(), description );
    }
    
    /**
     * Creates a new field using the given default value. This only works for vanilla registry objects
     * unless you hold off config initialization until after the target registry is populated. This is in
     * other words not possible for dynamic registries.
     */
    public RegObjectField( String key, IRegWrapper<T> regWrapper, T value, @Nullable String... description ) {
        this( key, regWrapper, Objects.requireNonNull( regWrapper.getKey( value ) ), description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Registry Object", valueDefault,
                "\"namespace:path]\"" ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = getDefaultValue();
            return;
        }
        final ResourceLocation regObjId = ResourceLocation.tryParse( raw.toString() );
        
        if( regObjId == null ) {
            // Invalid resource location
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid resource location! Must follow the pattern \"namespace:path\". Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            value = getDefaultValue();
            return;
        }
        value = RegObjKey.of( WRAPPED_REGISTRY, regObjId, false );
    }
    
    /** @return Returns the config field's value. */
    @Override
    public RegObjKey.Basic<T> get() {
        RegObjKey.Basic<T> key = getValue();
        return key == null ? defaultKey( WRAPPED_REGISTRY ) : key;
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public RegObjKey.Basic<T> getValue() {
        if( value == null ) {
            return value = getDefaultValue();
        }
        return value;
    }
    
    /**
     * @return This config field's registry object value,
     * or null if the value does not exist.
     */
    @Nullable
    public T getRegistryObject() {
        if( getValue() == null ) return null;
        return getValue().asValue();
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() {
        // Special case for sound events
        if( WRAPPED_REGISTRY.asForgeRegistry() == ForgeRegistries.SOUND_EVENTS ) {
            // noinspection unchecked
            return SoundPlayerWidgetProvider.ofField( (RegObjectField<SoundEvent>) this );
        }
        return new EntryViewWidgetProvider.Simple<>(
                this::getRegistryObject,
                EntryViewRendererRegistry.getForRegistry( WRAPPED_REGISTRY.registryKey() ),
                lineValidator() );
        
    }
    
    /** @return A registry based line validator using the given field. */
    public Predicate<String> lineValidator() {
        return ( s ) -> {
            final ResourceLocation id = ResourceLocation.tryParse( s );
            
            if( id != null ) {
                final Object regObj = WRAPPED_REGISTRY.get( id );
                final ResourceLocation defaultKey = WRAPPED_REGISTRY.getDefaultKey();
                
                if( regObj == null ) return false;
                
                // Make sure we only allow the default value if
                // it was specified intentionally.
                if( defaultKey != null ) {
                    Object defaultValue = WRAPPED_REGISTRY.get( defaultKey );
                    return regObj != defaultValue || id.equals( defaultKey );
                }
                else return true;
            }
            return false;
        };
    }
}
