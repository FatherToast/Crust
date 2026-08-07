package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fuzzy set used to match item stacks.
 *
 * @see ItemStackKey
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackSetField
 * @see ItemStackMap ItemStackMap - A similar collection that allows values
 */
@SuppressWarnings( "unused" )
public class ItemStackSet extends FuzzySet<ItemStack> {
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public ItemStackSet() { super( ItemStackKey.PARSER ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackSet.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackSet( FuzzyKey<ItemStack>... keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackSet.Builder} is much easier.
     */
    public ItemStackSet( Collection<FuzzyKey<ItemStack>> keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackSet makeNew() { return new ItemStackSet(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack sets smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<ItemStack, ItemStackSet, B> {
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public ItemStackSet build() { return new ItemStackSet( list ); }
        
        
        // ---- Data-Tag-Only Keys ---- //
        
        /** Adds a data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B addDataTag( String tag ) { return add( ItemStackKey.ofDataTag( tag, false ) ); }
        
        /** Adds a data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B addDataTag( CompoundTag tag ) { return add( ItemStackKey.ofDataTag( tag, false ) ); }
        
        /** Adds a blacklist data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B addDataTagBlacklist( String tag ) { return add( ItemStackKey.ofDataTag( tag, true ) ); }
        
        /** Adds a blacklist data-tag-only key based on the resource location. Matches any item with an appropriate data tag. */
        public B addDataTagBlacklist( CompoundTag tag ) { return add( ItemStackKey.ofDataTag( tag, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( String resLocAndTag ) { return add( ItemStackKey.of( resLocAndTag, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resLoc, tag, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B add( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( regObj, tag, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided item regardless of data tag. */
        public B add( RegistryObject<? extends Item> regObj ) { return add( regObj, null ); }
        
        /** Adds a key based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B add( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resKey, tag, false ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided item with any state tag. */
        public B add( ResourceKey<? extends Item> resKey ) { return add( resKey, null ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( Item item, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( item, tag, false ) ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B add( Item item ) { return add( item, null ); }
        
        /** Adds a key based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( ItemStack itemStack ) { return add( ItemStackKey.of( itemStack, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( String resLocAndTag ) { return add( ItemStackKey.of( resLocAndTag, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resLoc, tag, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( regObj, tag, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided item regardless of data tag. */
        public B addBlacklist( RegistryObject<? extends Item> regObj ) { return addBlacklist( regObj, null ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resKey, tag, true ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided item regardless of data tag. */
        public B addBlacklist( ResourceKey<? extends Item> resKey ) { return addBlacklist( resKey, null ); }
        
        /** Adds a blacklist key based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( Item item, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( item, tag, true ) ); }
        
        /** Adds a blacklist key based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B addBlacklist( Item item ) { return addBlacklist( item, null ); }
        
        /** Adds a blacklist key based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B addBlacklist( ItemStack itemStack ) { return add( ItemStackKey.of( itemStack, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B addWildcard( ResourceLocation partialResLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( partialResLoc, tag, false ) ); }
        
        /** Adds a wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B addWildcard( ResourceLocation partialResLoc ) { return addWildcard( partialResLoc, null ); }
        
        /** Adds a wildcard key based on the namespace. Matches every item in the namespace with an appropriate data tag. */
        public B addWildcard( String namespace, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( namespace, tag, false ) ); }
        
        /** Adds a wildcard key based on the namespace. Matches every item in the namespace regardless of data tag. */
        public B addWildcard( String namespace ) { return addWildcard( namespace, (CompoundTag) null ); }
        
        /** Adds a wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B addWildcard( String namespace, String partialPath, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( namespace, partialPath, tag, false ) ); }
        
        /** Adds a wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B addWildcard( String namespace, String partialPath ) { return addWildcard( namespace, partialPath, null ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( partialResLoc, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc ) { return addWildcardBlacklist( partialResLoc, null ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every item in the namespace with an appropriate data tag. */
        public B addWildcardBlacklist( String namespace, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( namespace, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every item in the namespace regardless of data tag. */
        public B addWildcardBlacklist( String namespace ) { return addWildcardBlacklist( namespace, (CompoundTag) null ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path with an appropriate data tag. */
        public B addWildcardBlacklist( String namespace, String partialPath, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofWildcard( namespace, partialPath, tag, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every item in the namespace that starts with the partial path regardless of data tag. */
        public B addWildcardBlacklist( String namespace, String partialPath ) { return addWildcardBlacklist( namespace, partialPath, null ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( String resLocAndTag ) { return add( ItemStackKey.ofTag( resLocAndTag, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofTag( resLoc, tag, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag regardless of data tag. */
        public B addTag( ResourceLocation resLoc ) { return addTag( resLoc, null ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag with an appropriate data tag. */
        public B addTag( TagKey<Item> tag, @Nullable CompoundTag dTag ) { return add( ItemStackKey.ofTag( tag, dTag, false ) ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag regardless of data tag. */
        public B addTag( TagKey<Item> tag ) { return addTag( tag, null ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTagBlacklist( String resLocAndTag ) { return add( ItemStackKey.ofTag( resLocAndTag, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTagBlacklist( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofTag( resLoc, tag, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every item in the tag regardless of data tag. */
        public B addTagBlacklist( ResourceLocation resLoc ) { return addTagBlacklist( resLoc, null ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every item in the tag with an appropriate data tag. */
        public B addTagBlacklist( TagKey<Item> tag, @Nullable CompoundTag dTag ) { return add( ItemStackKey.ofTag( tag, dTag, true ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every item in the tag regardless of data tag. */
        public B addTagBlacklist( TagKey<Item> tag ) { return addTagBlacklist( tag, null ); }
    }
}