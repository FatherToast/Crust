package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a config field containing an item stack value
 * wrapped in a {@link ItemStackKey.Basic} key.
 */
@SuppressWarnings( "unused" )
public class ItemStackField extends AbstractConfigField<ItemStackKey.Basic> {
    
    /** Creates a new field using a string-described block state. */
    public ItemStackField( String key, ItemStackKey.Basic defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
        
        // Sanity checks
        if( defaultValue.isBlacklist() ) {
            throw new IllegalArgumentException( "Item stack field cannot use a blacklist key! Invalid field: " + getKey() );
        }
    }
    
    /** Creates a new field using a string-described block state. */
    public ItemStackField( String key, String defaultValue, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultValue, false ), description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, String defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultItem, defaultTag, false ), description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, ResourceLocation defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultItem, defaultTag, false ), description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, RegistryObject<? extends Item> defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultItem, defaultTag, false ), description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, ResourceKey<? extends Item> defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultItem, defaultTag, false ), description );
    }
    
    /**
     * Creates a new field using an item and a compound tag. This only works for vanilla
     * items unless you hold off config initialization until after the item registry is populated.
     */
    public ItemStackField( String key, Item defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, ItemStackKey.of( defaultItem, defaultTag, false ), description );
    }
    
    /**
     * Creates a new field using a pre-existing item stack. This only works for vanilla items
     * unless you hold off config initialization until after the item registry is populated.
     */
    public ItemStackField( String key, ItemStack defaultValue, @Nullable String... description ) {
        super( key, ItemStackKey.of( defaultValue, false ), description );
    }
    
    /** @return This config field's item stack value, or air if it does not exist. */
    public ItemStack getItemStack() {
        return Objects.requireNonNullElse( get().asValue(), ItemStack.EMPTY );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Item Stack", getDefaultValue(),
                "\"namespace:path{tag1:value1,tag2:value2,...}\"" ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public ItemStackKey.Basic parse( Object raw ) {
        ItemStackKey.Basic value;
        if( raw instanceof ItemStackKey.Basic key ) {
            value = key;
        }
        else {
            String s = raw.toString();
            FuzzyKey<ItemStack> parsed = ItemStackKey.parse( null, // Suppresses parser's warnings; we'll do our own
                    s, s, false );
            if( !(parsed instanceof ItemStackKey.Basic key) ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Invalid item stack! Must follow the pattern \"namespace:path{tag1:value1,tag2:value2,...}\". Falling back to default ({}). Invalid value: {}",
                        getDefaultValue(), raw );
                return getDefaultValue();
            }
            value = key;
        }
        if( value.isBlacklist() ) {
            ConfigUtil.errorFor( this );
            ConfigUtil.LOG.error( "Attempted to assign blacklist to object! Falling back to default. Invalid value: {}",
                    raw );
            return getDefaultValue();
        }
        return value;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( ItemStackKey.Basic value, FriendlyByteBuf buffer ) {
        buffer.writeItemStack( Objects.requireNonNullElse( value.asValue(), ItemStack.EMPTY ), false );
    }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public ItemStackKey.Basic deserialize( FriendlyByteBuf buffer ) {
        return ItemStackKey.of( buffer.readItem(), false );
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<ItemStackKey.Basic> getWidgetProvider() {
        return new EntryViewWidgetProvider.SimpleMapped<>( ItemStackKey.Basic::asValue,
                EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK ),
                ( string ) -> {
                    ResourceLocation id = ResourceLocation.tryParse( string.split( "\\{" )[0] );
                    return id != null && ForgeRegistries.ITEMS.containsKey( id );
                } );
    }
}