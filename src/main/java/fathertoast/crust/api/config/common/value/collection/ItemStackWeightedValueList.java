package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
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
 * A fuzzy weighted list used to randomly pick item stack-value pairs.
 *
 * @param <V> The value type.
 * @see ItemStackKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackWeightedValueListField
 * @see ItemStackWeightedList ItemStackWeightedList - A similar collection that does not allow values
 */
@SuppressWarnings( "unused" )
public class ItemStackWeightedValueList<V> extends FuzzyWeightedValueList<ItemStack, V> {
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public ItemStackWeightedValueList( IValueCodec<V> codec ) { super( ItemStackKey.PARSER, codec ); }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackWeightedValueList.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackWeightedValueList( IValueCodec<V> codec, WeightedEntry<ItemStack, V>... keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackWeightedValueList.Builder} is much easier.
     */
    public ItemStackWeightedValueList( IValueCodec<V> codec, Collection<WeightedEntry<ItemStack, V>> keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackWeightedValueList<V> makeNew() { return new ItemStackWeightedValueList<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted value lists smoother. */
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<ItemStack, V, ItemStackWeightedValueList<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy weighted value list reflecting the current state of this builder. */
        @Override
        public ItemStackWeightedValueList<V> build() { return new ItemStackWeightedValueList<>( valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item with the appropriate item stack tag. */
        public B put( int weight, String resLocAndTag, V value ) { return put( weight, ItemStackKey.of( resLocAndTag, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item with the appropriate item stack tag. */
        public B put( int weight, ResourceLocation resLoc, @Nullable CompoundTag tag, V value ) { return put( weight, ItemStackKey.of( resLoc, tag, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided item with any item stack tag. */
        public B put( int weight, ResourceLocation resLoc, V value ) { return put( weight, resLoc, null, value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided item with the appropriate item stack tag. */
        public B put( int weight, RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag, V value ) { return put( weight, ItemStackKey.of( regObj, tag, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided item with any item stack tag. */
        public B put( int weight, RegistryObject<? extends Item> regObj, V value ) { return put( weight, regObj, null, value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided item with the appropriate item stack tag. */
        public B put( int weight, ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag, V value ) { return put( weight, ItemStackKey.of( resKey, tag, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided item with any item stack tag. */
        public B put( int weight, ResourceKey<? extends Item> resKey, V value ) { return put( weight, resKey, null, value ); }
        
        /** Adds a key-value pair based on the item. Only suitable for vanilla stuff. Matches only the provided item with the appropriate item stack tag. */
        public B put( int weight, Item item, @Nullable CompoundTag tag, V value ) { return put( weight, ItemStackKey.of( item, tag, false ), value ); }
        
        /** Adds a key-value pair based on the item. Only suitable for vanilla stuff. Matches only the provided item with any item stack tag. */
        public B put( int weight, Item item, V value ) { return put( weight, item, null, value ); }
        
        /** Adds a key-value pair based on the item stack. Only suitable for vanilla stuff. Matches only the provided item stack. */
        public B put( int weight, ItemStack itemStack, V value ) { return put( weight, ItemStackKey.of( itemStack, false ), value ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag with the appropriate item stack tag. */
        public B putTag( int weight, String resLocAndTag, V value ) { return put( weight, ItemStackKey.ofTag( resLocAndTag, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag with the appropriate item stack tag. */
        public B putTag( int weight, ResourceLocation resLoc, @Nullable CompoundTag tag, V value ) { return put( weight, ItemStackKey.ofTag( resLoc, tag, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every item in the tag with any item stack tag. */
        public B putTag( int weight, ResourceLocation resLoc, V value ) { return putTag( weight, resLoc, null, value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every item in the tag with the appropriate item stack tag. */
        public B putTag( int weight, TagKey<Item> tag, @Nullable CompoundTag dTag, V value ) { return put( weight, ItemStackKey.ofTag( tag, dTag, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every item in the tag with any item stack tag. */
        public B putTag( int weight, TagKey<Item> tag, V value ) { return putTag( weight, tag, null, value ); }
    }
}