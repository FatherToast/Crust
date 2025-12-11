package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy weighted list used to randomly pick block state-value pairs.
 *
 * @param <V> The value type.
 * @see BlockStateKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateWeightedValueListField
 * @see BlockStateWeightedList BlockStateWeightedList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class BlockStateWeightedValueList<V> extends FuzzyWeightedValueList<BlockState, V> {
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public BlockStateWeightedValueList( IValueCodec<V> codec ) { super( BlockStateKey.PARSER, codec ); }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateWeightedValueList.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateWeightedValueList( IValueCodec<V> codec, WeightedEntry<BlockState, V>... keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateWeightedValueList.Builder} is much easier.
     */
    public BlockStateWeightedValueList( IValueCodec<V> codec, Collection<WeightedEntry<BlockState, V>> keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateWeightedValueList<V> makeNew() { return new BlockStateWeightedValueList<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<BlockState, V, BlockStateWeightedValueList<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy weighted value list reflecting the current state of this builder. */
        @Override
        public BlockStateWeightedValueList<V> build() { return new BlockStateWeightedValueList<>( valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( int weight, String resLocAndProperties, V value ) { return put( weight, BlockStateKey.of( resLocAndProperties, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( int weight, ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.of( resLoc, properties, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B put( int weight, RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.of( regObj, properties, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B put( int weight, ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.of( resKey, properties, false ), value ); }
        
        /** Adds a key-value pair based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B put( int weight, Block block, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.of( block, properties, false ), value ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( int weight, String resLocAndProperties, V value ) { return put( weight, BlockStateKey.ofTag( resLocAndProperties, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( int weight, ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.ofTag( resLoc, properties, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( int weight, TagKey<Block> tag, BlockStatePropertyMap properties, V value ) { return put( weight, BlockStateKey.ofTag( tag, properties, false ), value ); }
    }
}