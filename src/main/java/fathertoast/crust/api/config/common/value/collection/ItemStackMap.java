package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with item stacks.
 *
 * @param <V> The value type.
 * @see ItemStackKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackMapField
 * @see ItemStackSet ItemStackSet - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class ItemStackMap<V> extends FuzzyMap<ItemStack, V> {
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public ItemStackMap( IValueCodec<V> codec ) { super( ItemStackKey.PARSER, codec ); }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackMap.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackMap( IValueCodec<V> codec, FuzzyEntry<ItemStack, V>... keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackMap.Builder} is much easier.
     */
    public ItemStackMap( IValueCodec<V> codec, Collection<FuzzyEntry<ItemStack, V>> keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackMap<V> makeNew() { return new ItemStackMap<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack maps smoother. */
    @ApiStatus.Experimental
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<ItemStack, V, ItemStackMap<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public ItemStackMap<V> build() { return new ItemStackMap<>( valueCodec, list ); }
        
        
        // ---- Data-Tag-Only Keys ---- //
        
        /** Adds a data-tag-only key-value pair based on the resource location. Matches any item with an appropriate data tag. */
        public B putDataTag( String tag, V value ) { return put( ItemStackKey.ofDataTag( tag, false ), value ); }
        
        /** Adds a data-tag-only key-value pair based on the resource location. Matches any item with an appropriate data tag. */
        public B putDataTag( CompoundTag tag, V value ) { return put( ItemStackKey.ofDataTag( tag, false ), value ); }
        
        /** Adds a blacklist data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B putDataTagBlacklist( String tag ) { return putBlacklist( ItemStackKey.ofDataTag( tag, true ) ); }
        
        /** Adds a blacklist data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B putDataTagBlacklist( CompoundTag tag ) { return putBlacklist( ItemStackKey.ofDataTag( tag, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B put( String resLocAndTag, V value ) { return put( ItemStackKey.of( resLocAndTag, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B put( ResourceLocation resLoc, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.of( resLoc, tag, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item regardless of data tag. */
        public B put( ResourceLocation resLoc, V value ) { return put( resLoc, null, value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B put( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.of( regObj, tag, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided item regardless of data tag. */
        public B put( RegistryObject<? extends Item> regObj, V value ) { return put( regObj, null, value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B put( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.of( resKey, tag, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided item regardless of data tag. */
        public B put( ResourceKey<? extends Item> resKey, V value ) { return put( resKey, null, value ); }
        
        /** Adds a key-value pair based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B put( Item item, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.of( item, tag, false ), value ); }
        
        /** Adds a key-value pair based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B put( Item item, V value ) { return put( item, null, value ); }
        
        /** Adds a key-value pair based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B put( ItemStack itemStack, V value ) { return put( ItemStackKey.of( itemStack, false ), value ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( String resLocAndTag ) { return putBlacklist( ItemStackKey.of( resLocAndTag, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.of( resLoc, tag, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided item regardless of data tag. */
        public B putBlacklist( ResourceLocation resLoc ) { return putBlacklist( resLoc, null ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.of( regObj, tag, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided item regardless of data tag. */
        public B putBlacklist( RegistryObject<? extends Item> regObj ) { return putBlacklist( regObj, null ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.of( resKey, tag, true ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided item regardless of data tag. */
        public B putBlacklist( ResourceKey<? extends Item> resKey ) { return putBlacklist( resKey, null ); }
        
        /** Adds a blacklist key based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( Item item, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.of( item, tag, true ) ); }
        
        /** Adds a blacklist key based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B putBlacklist( Item item ) { return putBlacklist( item, null ); }
        
        /** Adds a blacklist key based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B putBlacklist( ItemStack itemStack ) { return putBlacklist( ItemStackKey.of( itemStack, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B putWildcard( ResourceLocation partialResLoc, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.ofWildcard( partialResLoc, tag, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B putWildcard( ResourceLocation partialResLoc, V value ) { return putWildcard( partialResLoc, null, value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches every item in the namespace with an appropriate data tag. */
        public B putWildcard( String namespace, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.ofWildcard( namespace, tag, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches every item in the namespace regardless of data tag. */
        public B putWildcard( String namespace, V value ) { return putWildcard( namespace, (CompoundTag) null, value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B putWildcard( String namespace, String partialPath, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.ofWildcard( namespace, partialPath, tag, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B putWildcard( String namespace, String partialPath, V value ) { return putWildcard( namespace, partialPath, null, value ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.ofWildcard( partialResLoc, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc ) { return putWildcardBlacklist( partialResLoc, null ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every item in the namespace with an appropriate data tag. */
        public B putWildcardBlacklist( String namespace, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.ofWildcard( namespace, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every item in the namespace regardless of data tag. */
        public B putWildcardBlacklist( String namespace ) { return putWildcardBlacklist( namespace, (CompoundTag) null ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B putWildcardBlacklist( String namespace, String partialPath, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.ofWildcard( namespace, partialPath, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B putWildcardBlacklist( String namespace, String partialPath ) { return putWildcardBlacklist( namespace, partialPath, null ); }
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B putTag( String resLocAndTag, V value ) { return put( ItemStackKey.ofTag( resLocAndTag, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B putTag( ResourceLocation resLoc, @Nullable CompoundTag tag, V value ) { return put( ItemStackKey.ofTag( resLoc, tag, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag regardless of data tag. */
        public B putTag( ResourceLocation resLoc, V value ) { return putTag( resLoc, null, value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every item in the tag with an appropriate data tag. */
        public B putTag( TagKey<Item> tag, @Nullable CompoundTag dTag, V value ) { return put( ItemStackKey.ofTag( tag, dTag, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every item in the tag regardless of data tag. */
        public B putTag( TagKey<Item> tag, V value ) { return putTag( tag, null, value ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B putTagBlacklist( String resLocAndTag ) { return putBlacklist( ItemStackKey.ofTag( resLocAndTag, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B putTagBlacklist( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return putBlacklist( ItemStackKey.ofTag( resLoc, tag, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag regardless of data tag. */
        public B putTagBlacklist( ResourceLocation resLoc ) { return putTagBlacklist( resLoc, null ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every item in the tag with an appropriate data tag. */
        public B putTagBlacklist( TagKey<Item> tag, @Nullable CompoundTag dTag ) { return putBlacklist( ItemStackKey.ofTag( tag, dTag, true ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every item in the tag regardless of data tag. */
        public B putTagBlacklist( TagKey<Item> tag ) { return putTagBlacklist( tag, null ); }
    }
}