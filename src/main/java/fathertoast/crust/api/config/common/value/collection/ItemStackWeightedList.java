package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
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
 * A fuzzy weighted list used to randomly pick item stacks.
 *
 * @see ItemStackKey
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackWeightedListField
 * @see ItemStackWeightedValueList ItemStackWeightedValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class ItemStackWeightedList extends FuzzyWeightedList<ItemStack> {
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public ItemStackWeightedList() { super( ItemStackKey.PARSER ); }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackWeightedList.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackWeightedList( WeightedKey<ItemStack>... keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackWeightedList.Builder} is much easier.
     */
    public ItemStackWeightedList( Collection<WeightedKey<ItemStack>> keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackWeightedList makeNew() { return new ItemStackWeightedList(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<ItemStack, ItemStackWeightedList, B> {
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public ItemStackWeightedList build() { return new ItemStackWeightedList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, String resLocAndTag ) { return add( weight, ItemStackKey.of( resLocAndTag, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( weight, ItemStackKey.of( resLoc, tag, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided item regardless of data tag. */
        public B add( int weight, ResourceLocation resLoc ) { return add( weight, resLoc, null ); }
        
        /** Adds a key based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag ) { return add( weight, ItemStackKey.of( regObj, tag, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided item regardless of data tag. */
        public B add( int weight, RegistryObject<? extends Item> regObj ) { return add( weight, regObj, null ); }
        
        /** Adds a key based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag ) { return add( weight, ItemStackKey.of( resKey, tag, false ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided item regardless of data tag. */
        public B add( int weight, ResourceKey<? extends Item> resKey ) { return add( weight, resKey, null ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, Item Item, @Nullable CompoundTag tag ) { return add( weight, ItemStackKey.of( Item, tag, false ) ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B add( int weight, Item item ) { return add( weight, item, null ); }
        
        /** Adds a key based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( int weight, ItemStack itemStack ) { return add( weight, ItemStackKey.of( itemStack, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( int weight, String resLocAndTag ) { return add( weight, ItemStackKey.ofTag( resLocAndTag, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( int weight, ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( weight, ItemStackKey.ofTag( resLoc, tag, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag regardless of data tag. */
        public B addTag( int weight, ResourceLocation resLoc ) { return addTag( weight, resLoc, null ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag with an appropriate data tag. */
        public B addTag( int weight, TagKey<Item> tag, @Nullable CompoundTag dTag ) { return add( weight, ItemStackKey.ofTag( tag, dTag, false ) ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag regardless of data tag. */
        public B addTag( int weight, TagKey<Item> tag ) { return addTag( weight, tag, null ); }
    }
}