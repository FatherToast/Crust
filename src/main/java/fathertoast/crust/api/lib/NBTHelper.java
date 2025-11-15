package fathertoast.crust.api.lib;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static net.minecraft.nbt.Tag.*;

@SuppressWarnings( "unused" )
public final class NBTHelper {
    
    /** Logger instance for the Crust NBT helper. */
    public static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID + "/nbt" );
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list value.
     */
    public static boolean containsList( CompoundTag tag, String name ) { return contains( tag, name, TAG_LIST ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores another compound value.
     */
    public static boolean containsCompound( CompoundTag tag, String name ) { return contains( tag, name, TAG_COMPOUND ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a string value.
     */
    public static boolean containsString( CompoundTag tag, String name ) { return contains( tag, name, TAG_STRING ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a numerical value.
     */
    public static boolean containsNumber( CompoundTag tag, String name ) { return contains( tag, name, TAG_ANY_NUMERIC ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a byte array value.
     */
    public static boolean containsByteArray( CompoundTag tag, String name ) { return contains( tag, name, TAG_BYTE_ARRAY ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores an int array value.
     */
    public static boolean containsIntArray( CompoundTag tag, String name ) { return contains( tag, name, TAG_INT_ARRAY ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a long array value.
     */
    public static boolean containsLongArray( CompoundTag tag, String name ) { return contains( tag, name, TAG_LONG_ARRAY ); }
    
    /**
     * Performs the actual 'contains' check.
     *
     * @see net.minecraft.nbt.Tag
     */
    private static boolean contains( CompoundTag tag, String name, int id ) { return tag.contains( name, id ); }
    
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the desired compound tag.
     * @return The retrieved compound tag, or a newly created and saved tag if none existed.
     */
    public static CompoundTag getOrCreateCompound( CompoundTag tag, String name ) {
        if( !containsCompound( tag, name ) ) tag.put( name, new CompoundTag() );
        return tag.getCompound( name );
    }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to not store data in the Forge data compound directly, but instead store your mod's data in
     * a compound tag within the Forge data compound.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(Player, String)}.
     *
     * @return The entity's Forge data at 'ForgeData'.
     * @see NBTHelper#getForgeData(Entity, String)
     */
    public static CompoundTag getForgeData( Entity entity ) { return entity.getPersistentData(); }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(Player, String)}.
     *
     * @return A compound tag at 'ForgeData/name'.
     * @see NBTHelper#getForgeData(Entity, String, String)
     */
    public static CompoundTag getForgeData( Entity entity, String name ) {
        return getOrCreateCompound( getForgeData( entity ), name );
    }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(Player, String)}.
     *
     * @return A compound tag at 'ForgeData/name/subName'.
     * @see NBTHelper#getForgeData(Entity, String)
     */
    public static CompoundTag getForgeData( Entity entity, String name, String subName ) {
        return getOrCreateCompound( getOrCreateCompound( getForgeData( entity ), name ), subName );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     * <p>
     * It is recommended to not store data into the persistent player data directly, but
     * instead store your mod's data in a compound tag within the persistent player data tag.
     *
     * @return The player's persistent data at 'ForgeData/PlayerPersisted'.
     * @see NBTHelper#getPlayerData(Player, String)
     * @see NBTHelper#getPlayerData(Player, String, String)
     */
    public static CompoundTag getPlayerData( Player player ) {
        return getForgeData( player, Player.PERSISTED_NBT_TAG );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     *
     * @return A compound tag at 'ForgeData/PlayerPersisted/name'.
     * @see NBTHelper#getPlayerData(Player, String, String)
     */
    public static CompoundTag getPlayerData( Player player, String name ) {
        return getOrCreateCompound( getPlayerData( player ), name );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     *
     * @return A compound tag at 'ForgeData/PlayerPersisted/name/subName'.
     * @see NBTHelper#getPlayerData(Player, String)
     */
    public static CompoundTag getPlayerData( Player player, String name, String subName ) {
        return getOrCreateCompound( getPlayerData( player, name ), subName );
    }
    
    /**
     * Convenience method for writing a block state to NBT.
     *
     * @param tag        The compound tag to write to.
     * @param name       The name of the desired block state's compound tag.
     * @param blockState The block state to write.
     */
    public static void putBlockState( CompoundTag tag, String name, BlockState blockState ) {
        tag.put( name, writeBlockState( blockState ) );
    }
    
    /**
     * Convenience method for loading a block state from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired block state's compound tag.
     * @return The loaded block state, or air if the block state tag does not exist or encounters a problem.
     */
    public static BlockState getBlockState( CompoundTag tag, String name ) {
        return readBlockState( tag.getCompound( name ) );
    }
    
    /**
     * @param blockState The state to save.
     * @return A new compound tag containing the block state's data.
     */
    public static CompoundTag writeBlockState( BlockState blockState ) {
        return NbtUtils.writeBlockState( blockState );
    }
    
    /**
     * Modified copy-paste of {@link net.minecraft.nbt.NbtUtils#readBlockState(HolderGetter, CompoundTag)}.<br>
     * Original implementation is not as friendly. This one checks the forge registry for blocks.
     *
     * @param blockTag The compound tag containing the block state data, such as the one returned by writeBlockState.
     * @return The block state as described by the block tag, or air if anything goes wrong.
     */
    public static BlockState readBlockState( CompoundTag blockTag ) {
        if( !blockTag.contains( "Name", Tag.TAG_STRING ) ) {
            return Blocks.AIR.defaultBlockState();
        }
        else {
            ResourceLocation blockId = ResourceLocation.parse( blockTag.getString( "Name" ) );
            Block block = ForgeRegistries.BLOCKS.getValue( blockId );
            
            if( block == null ) {
                return Blocks.AIR.defaultBlockState();
            }
            else {
                BlockState blockState = block.defaultBlockState();
                if( blockTag.contains( "Properties", Tag.TAG_COMPOUND ) ) {
                    CompoundTag propertiesTag = blockTag.getCompound( "Properties" );
                    StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
                    
                    for( String key : propertiesTag.getAllKeys() ) {
                        Property<?> property = statedefinition.getProperty( key );
                        
                        if( property != null ) {
                            blockState = setValueHelper( blockState, property, key, propertiesTag, blockTag );
                        }
                    }
                }
                return blockState;
            }
        }
    }
    
    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper( S state, Property<T> property, String key,
                                                                                            CompoundTag propertiesTag, CompoundTag blockTag ) {
        Optional<T> optional = property.getValue( propertiesTag.getString( key ) );
        if( optional.isPresent() ) {
            return state.setValue( property, optional.get() );
        }
        else {
            LOG.warn( "Unable to read property: {} with value: {} for blockstate: {}", key, propertiesTag.getString( key ), blockTag.toString() );
            return state;
        }
    }
    
    // Utility class
    private NBTHelper() { }
}