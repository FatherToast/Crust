package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
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
 * A fuzzy list used to iterate over block states with associated values.
 *
 * @param <V> The value type.
 * @see BlockStateKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateValueListField
 * @see BlockStateList BlockStateList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class BlockStateValueList<V> extends FuzzyValueList<BlockState, V> {
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public BlockStateValueList( IValueCodec<V> codec ) { super( BlockStateKey.PARSER, codec ); }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateValueList.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateValueList( IValueCodec<V> codec, FuzzyEntry<BlockState, V>... keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateValueList.Builder} is much easier.
     */
    public BlockStateValueList( IValueCodec<V> codec, Collection<FuzzyEntry<BlockState, V>> keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateValueList<V> makeNew() { return new BlockStateValueList<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<BlockState, V, BlockStateValueList<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public BlockStateValueList<V> build() { return new BlockStateValueList<>( valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( String resLocAndProperties, V value ) { return put( BlockStateKey.of( resLocAndProperties, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( resLoc, properties, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B put( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( regObj, properties, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B put( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( resKey, properties, false ), value ); }
        
        /** Adds a key-value pair based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B put( Block obj, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( obj, properties, false ), value ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( String resLocAndProperties, V value ) { return put( BlockStateKey.ofTag( resLocAndProperties, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofTag( resLoc, properties, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( TagKey<Block> tag, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofTag( tag, properties, false ), value ); }
    }
}