package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a config field containing an item stack value
 * wrapped in a {@link ItemStackKey.Basic} key.
 */
@ApiStatus.Experimental
public class ItemStackField extends GenericField<ItemStackKey.Basic> {
    
    /** @return A non-blacklist, basic item stack key of the given item ID and NBT. */
    public static ItemStackKey.Basic of( ResourceLocation resLoc, @Nullable CompoundTag tag ) {
        return ItemStackKey.of( resLoc, tag, false );
    }
    
    /** @return A non-blacklist, basic item stack key of the given item stack. */
    public static ItemStackKey.Basic of( ItemStack value ) {
        return ItemStackKey.of( value, false );
    }
    
    /**
     * @return A non-blacklist, basic item stack key containing
     * the {@link ItemStack#EMPTY} instance.
     */
    public static ItemStackKey.Basic emptyKey() {
        return of( ItemStack.EMPTY );
    }
    
    
    /** Creates a new field using a string-described block state. */
    public ItemStackField( String key, String defaultValue, @Nullable String... description ) {
        super( key, ItemStackKey.of( defaultValue, false ), description );
        final String[] split = BlockStatePropertyMap.split( defaultValue );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, ResourceLocation defaultResLoc, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        super( key, ItemStackKey.of( defaultResLoc, defaultTag, false ), description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, RegistryObject<? extends Item> defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        // noinspection DataFlowIssue
        this( key, defaultItem.getId(), defaultTag, description );
    }
    
    /** Creates a new field. */
    public ItemStackField( String key, ResourceKey<? extends Item> defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, defaultItem.location(), defaultTag, description );
    }
    
    /**
     * Creates a new field using an item and a compound tag. This only works for vanilla
     * items unless you hold off config initialization until after the item registry is populated.
     */
    public ItemStackField( String key, Item defaultItem, @Nullable CompoundTag defaultTag, @Nullable String... description ) {
        this( key, Objects.requireNonNull( ForgeRegistries.ITEMS.getKey( defaultItem ) ), defaultTag, description );
    }
    
    /**
     * Creates a new field using a pre-existing item stack. This only works for vanilla items
     * unless you hold off config initialization until after the item registry is populated.
     */
    public ItemStackField( String key, ItemStack defaultValue, @Nullable String... description ) {
        super( key, of( defaultValue ), description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Item Stack", valueDefault,
                "\"namespace:path{tag1:value1,tag2:value2,...}\"" ) );
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
        final String s = raw.toString();
        final FuzzyKey<ItemStack> key = ItemStackKey.PARSER.parseKeyString( this, s, s, false );
        
        if( key instanceof ItemStackKey.Basic basicKey ) {
            value = basicKey;
        }
        else {
            value = getDefaultValue();
        }
    }
    
    /** @return Returns the config field's value. */
    @Override
    public ItemStackKey.Basic get() {
        ItemStackKey.Basic key = getValue();
        return key == null ? emptyKey() : key;
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public ItemStackKey.Basic getValue() {
        if( value == null ) {
            return value = getDefaultValue();
        }
        return value;
    }
    
    /** @return This config field's item stack value, if it exists. */
    @Nullable
    private ItemStack getItemStack() {
        return get().asValue();
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() {
        return new EntryViewWidgetProvider.Simple<>( this::getItemStack, EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK ), ( s ) -> {
            ResourceLocation id = ResourceLocation.tryParse( s.split( "\\{" )[0] );
            return id != null && ForgeRegistries.BLOCKS.containsKey( id );
        } );
    }
}
