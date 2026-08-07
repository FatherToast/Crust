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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over item stacks with associated values.
 *
 * @param <V> The value type.
 * @see ItemStackKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.ItemStackValueListField
 * @see ItemStackList ItemStackList - A similar collection that does not allow values
 */
@SuppressWarnings( "unused" )
public class ItemStackValueList<V> extends FuzzyValueList<ItemStack, V> {
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public ItemStackValueList( IValueCodec<V> codec ) { super( ItemStackKey.PARSER, codec ); }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackValueList.Builder} is much easier.
     */
    @SafeVarargs
    public ItemStackValueList( IValueCodec<V> codec, FuzzyEntry<ItemStack, V>... keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackValueList.Builder} is much easier.
     */
    public ItemStackValueList( IValueCodec<V> codec, Collection<FuzzyEntry<ItemStack, V>> keys ) {
        super( ItemStackKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemStackValueList<V> makeNew() { return new ItemStackValueList<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry value lists smoother. */
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<ItemStack, V, ItemStackValueList<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public ItemStackValueList<V> build() { return new ItemStackValueList<>( valueCodec, list ); }
        
        
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
    }
}