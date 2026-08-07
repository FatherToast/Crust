package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
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
 * A fuzzy list used to iterate over item stacks.
 *
 * @see ItemStackKey
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackListField
 * @see ItemStackValueList ItemStackValueList - A similar collection that allows values
 */
@SuppressWarnings( "unused" )
public class ItemStackList extends FuzzyList<ItemStack> {
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public ItemStackList() { super( ItemStackKey.PARSER ); }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackList( FuzzyKey<ItemStack>... keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    public ItemStackList( Collection<FuzzyKey<ItemStack>> keys ) { super( ItemStackKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackList makeNew() { return new ItemStackList(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack lists smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<ItemStack, ItemStackList, B> {
        
        /** @return A new fuzzy list reflecting the current state of this builder. */
        @Override
        public ItemStackList build() { return new ItemStackList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( String resLocAndTag ) { return add( ItemStackKey.of( resLocAndTag, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided item regardless of data tag. */
        public B add( ResourceLocation resLoc ) { return add( resLoc, null ); }
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B add( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resLoc, tag, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided item regardless of data tag. */
        public B add( RegistryObject<? extends Item> regObj ) { return add( regObj, null ); }
        
        /** Adds a key based on the registry object. Matches only the provided item with an appropriate data tag. */
        public B add( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( regObj, tag, false ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided item regardless of data tag. */
        public B add( ResourceKey<? extends Item> resKey ) { return add( resKey, null ); }
        
        /** Adds a key based on the resource key. Matches only the provided item with an appropriate data tag. */
        public B add( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( resKey, tag, false ) ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item regardless of data tag. */
        public B add( Item item ) { return add( item, null ); }
        
        /** Adds a key based on the item. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( Item item, @Nullable CompoundTag tag ) { return add( ItemStackKey.of( item, tag, false ) ); }
        
        /** Adds a key based on the item stack. Only suitable for vanilla stuff. Matches only the provided item with an appropriate data tag. */
        public B add( ItemStack itemStack ) { return add( ItemStackKey.of( itemStack, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( String resLocAndTag ) { return add( ItemStackKey.ofTag( resLocAndTag, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag regardless of data tag. */
        public B addTag( ResourceLocation resLoc ) { return addTag( resLoc, null ); }
        
        /** Adds a tag key based on the resource location. Matches every item in the tag with an appropriate data tag. */
        public B addTag( ResourceLocation resLoc, @Nullable CompoundTag tag ) { return add( ItemStackKey.ofTag( resLoc, tag, false ) ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag regardless of data tag. */
        public B addTag( TagKey<Item> tag ) { return addTag( tag, null ); }
        
        /** Adds a tag key based on the tag. Matches every item in the tag with an appropriate data tag. */ // tag, tag, tag
        public B addTag( TagKey<Item> tag, @Nullable CompoundTag dTag ) { return add( ItemStackKey.ofTag( tag, dTag, false ) ); }
    }
}