package fathertoast.crust.api.lib;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.*;
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
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.nbt.Tag.*;

@SuppressWarnings( "unused" )
public final class NBTHelper {
    
    /** Logger instance for the Crust NBT helper. */
    public static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID + "/nbt" );
    
    
    // ---- Contains Checks ---- //
    
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
     * Use this check for all numbers and booleans.
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
    private static boolean contains( CompoundTag tag, String name, byte id ) { return tag.contains( name, id ); }
    
    
    // ---- Compound Tags ---- //
    
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
    
    
    // ---- SNBT helpers ---- //
    
    /** @return An SNBT representation of the given NBT. */
    // Here for completeness, and maybe we'd like to format it nicer someday
    public static String toSNBT( CompoundTag nbt ) { return nbt.getAsString(); }
    
    /** @return An NBT representation of the given SNBT; null if there are any syntax errors in the SNBT. */
    @Nullable
    public static CompoundTag toNBT( String snbt ) {
        try {
            return TagParser.parseTag( snbt );
        }
        catch( Exception ex ) {
            LOG.warn( "Invalid SNBT data tags: {}", snbt, ex );
        }
        return null;
    }
    
    
    // ---- Matching ---- //
    
    /**
     * Depending on the pattern tag's type:<p>
     * * null - Matches anything.<p>
     * * Numeric - Matches tags with the same value.<p>
     * * String - Matches tags that produce the same SNBT value (such as an equivalent String).<p>
     * * Array/List - Matches tags with the same element values.<p>
     * * Compound - Matches compound tags that contain a matching tag for each tag in the pattern.<p>
     * * End - Only matches End tags.
     *
     * @param tag     The tag to check.
     * @param pattern A tag used as a pattern.
     * @return True if the checked tag matches the pattern.
     */
    public static boolean matches( @Nullable Tag tag, @Nullable Tag pattern ) {
        if( pattern == null ) return true;
        if( tag == null ) return false;
        try {
            return switch( tag.getId() ) {
                // Compound types recursively check that each of their tags matches a tag in the checked tag with the same name
                case TAG_COMPOUND -> {
                    if( !(tag instanceof CompoundTag tagCompound) ) yield false;
                    CompoundTag patternCompound = (CompoundTag) pattern;
                    for( String name : patternCompound.getAllKeys() ) {
                        if( !matches( tagCompound.get( name ), patternCompound.get( name ) ) ) yield false;
                    }
                    yield true;
                }
                
                // Value types attempt to convert the checked tag to their own type to test value equivalency
                case TAG_BYTE -> ((NumericTag) pattern).getAsByte() == ((NumericTag) tag).getAsByte();
                case TAG_SHORT -> ((NumericTag) pattern).getAsShort() == ((NumericTag) tag).getAsShort();
                case TAG_INT -> ((NumericTag) pattern).getAsInt() == ((NumericTag) tag).getAsInt();
                case TAG_LONG -> ((NumericTag) pattern).getAsLong() == ((NumericTag) tag).getAsLong();
                case TAG_FLOAT -> ((NumericTag) pattern).getAsFloat() == ((NumericTag) tag).getAsFloat();
                case TAG_DOUBLE -> ((NumericTag) pattern).getAsDouble() == ((NumericTag) tag).getAsDouble();
                // Note: This allows strings to be used as strict SNBT checks, since tag
                case TAG_STRING -> pattern.getAsString().equals( tag.getAsString() );
                
                // Collection types check for length equivalency and that each element recursively matches
                case TAG_LIST, TAG_BYTE_ARRAY, TAG_INT_ARRAY, TAG_LONG_ARRAY -> {
                    CollectionTag<?> patternList = (CollectionTag<?>) pattern;
                    CollectionTag<?> tagList = (CollectionTag<?>) tag;
                    int size = patternList.size();
                    if( size != tagList.size() ) yield false;
                    for( int i = 0; i < size; i++ ) {
                        if( !matches( tagList.get( i ), patternList.get( i ) ) ) yield false;
                    }
                    yield true;
                }
                
                // End tags only match other end tags
                case TAG_END -> tag.getId() == TAG_END;
                
                // Unknown types attempt to directly check equivalency
                default -> pattern.equals( tag );
            };
        }
        catch( ClassCastException ignored ) {
            // Checked tag's type is incompatible with the pattern's type
            return false;
        }
    }
    
    
    // ---- Forge Registry entries ---- //
    
    /**
     * Convenience method for writing a registry entry's ID to NBT.<br>
     * If anything goes wrong, an empty String is written.
     *
     * @param tag      The compound tag to write to.
     * @param registry The Forge registry to check.
     * @param name     The name of the String tag the ID will be written to.
     * @param entry    The registry entry to get an ID for.
     */
    public static <T> void putRegistryEntry( CompoundTag tag, IForgeRegistry<T> registry, String name, T entry ) {
        String value = "";
        if( registry.containsValue( entry ) ) {
            // noinspection ConstantConditions
            value = registry.getKey( entry ).toString();
        }
        tag.putString( name, value );
    }
    
    /**
     * Convenience method for loading a registry entry from NBT.
     *
     * @param tag      The compound tag to read from.
     * @param registry The Forge registry to check.
     * @param name     The name of the String tag to look for a registry ID in.
     * @return The registry entry that corresponds to the ID that was read.
     * Returns null if no valid ID was read or the registry doesn't contain the ID.
     */
    @Nullable
    public static <T> T getRegistryEntry( CompoundTag tag, IForgeRegistry<T> registry, String name ) {
        if( !containsString( tag, name ) ) return null;
        ResourceLocation id = ResourceLocation.tryParse( tag.getString( name ) );
        
        if( id == null )
            return null;
        
        return registry.getValue( id );
    }
    
    /**
     * Convenience method for loading a registry entry from NBT.
     * Supplies a default value that is returned if loading fails.
     *
     * @param tag      The compound tag to read from.
     * @param registry The Forge registry to check.
     * @param name     The name of the String tag to look for a registry ID in.
     * @return The registry entry that corresponds to the ID that was read.
     * Returns the desired default value if no valid ID was read or the registry doesn't contain the ID.
     */
    public static <T> T getRegEntryOrDefault( CompoundTag tag, IForgeRegistry<T> registry, String name, T defaultValue ) {
        T value = getRegistryEntry( tag, registry, name );
        return value == null ? defaultValue : value;
    }
    
    
    // ---- Block States ---- //
    
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
            LOG.warn( "Unable to read property: {} with value: {} for BlockState: {}", key, propertiesTag.getString( key ), blockTag.toString() );
            return state;
        }
    }
    
    
    // ---- List Tags ---- //
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores any kind of list value.
     */
    public static boolean containsList( CompoundTag tag, String name ) { return contains( tag, name, TAG_LIST ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of list values.
     */
    public static boolean containsListList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_LIST ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the lists in.
     * @param list The list to write.
     */
    public static void putListList( CompoundTag tag, String name, List<ListTag> list ) {
        final ListTag listTag = new ListTag();
        listTag.addAll( list );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<ListTag> getListList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_LIST ) {
            final List<ListTag> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( (ListTag) entry );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of compound values.
     */
    public static boolean containsCompoundList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_COMPOUND ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the desired compound list tag.
     * @param list The list to write.
     */
    public static void putCompoundList( CompoundTag tag, String name, List<CompoundTag> list ) {
        final ListTag listTag = new ListTag();
        listTag.addAll( list );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<CompoundTag> getCompoundList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_COMPOUND ) {
            final List<CompoundTag> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( (CompoundTag) entry );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of string values.
     */
    public static boolean containsStringList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_STRING ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the string list in.
     * @param list The list to write.
     */
    public static void putStringList( CompoundTag tag, String name, List<String> list ) {
        final ListTag listTag = new ListTag();
        for( String entry : list ) listTag.add( StringTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<String> getStringList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_STRING ) {
            final List<String> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( entry.getAsString() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of numerical values.
     */
    public static boolean containsNumberList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_ANY_NUMERIC ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of list tag to put the boolean list in.
     * @param list The list to write.
     */
    public static void putBooleanList( CompoundTag tag, String name, List<Boolean> list ) {
        final ListTag listTag = new ListTag();
        for( Boolean entry : list ) listTag.add( ByteTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Boolean> getBooleanList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Boolean> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsByte() != 0 );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the byte list in.
     * @param list The list to write.
     */
    public static void putByteList( CompoundTag tag, String name, List<Byte> list ) {
        final ListTag listTag = new ListTag();
        for( Byte entry : list ) listTag.add( ByteTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Byte> getByteList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Byte> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsByte() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the short list in.
     * @param list The list to write.
     */
    public static void putShortList( CompoundTag tag, String name, List<Short> list ) {
        final ListTag listTag = new ListTag();
        for( Short entry : list ) listTag.add( ShortTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Short> getShortList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Short> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsShort() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the integer list in.
     * @param list The list to write.
     */
    public static void putIntList( CompoundTag tag, String name, List<Integer> list ) {
        final ListTag listTag = new ListTag();
        for( Integer entry : list ) listTag.add( IntTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Integer> getIntList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Integer> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsInt() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the long list in.
     * @param list The list to write.
     */
    public static void putLongList( CompoundTag tag, String name, List<Long> list ) {
        final ListTag listTag = new ListTag();
        for( Long entry : list ) listTag.add( LongTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Long> getLongList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Long> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsLong() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the float list in.
     * @param list The list to write.
     */
    public static void putFloatList( CompoundTag tag, String name, List<Float> list ) {
        final ListTag listTag = new ListTag();
        for( Float entry : list ) listTag.add( FloatTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Float> getFloatList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Float> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsFloat() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the double list in.
     * @param list The list to write.
     */
    public static void putDoubleList( CompoundTag tag, String name, List<Double> list ) {
        final ListTag listTag = new ListTag();
        for( Double entry : list ) listTag.add( DoubleTag.valueOf( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<Double> getDoubleList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && isNumericId( listTag.getElementType() ) ) {
            final List<Double> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((NumericTag) entry).getAsDouble() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /** @return True if the id is a numeric tag id. */
    private static boolean isNumericId( byte id ) {
        return id == TAG_BYTE || id == TAG_SHORT || id == TAG_INT || id == TAG_LONG || id == TAG_FLOAT || id == TAG_DOUBLE;
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of byte array values.
     */
    public static boolean containsByteArrayList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_BYTE_ARRAY ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the byte array list in.
     * @param list The list to write.
     */
    public static void putByteArrayList( CompoundTag tag, String name, List<byte[]> list ) {
        final ListTag listTag = new ListTag();
        for( byte[] entry : list ) listTag.add( new ByteArrayTag( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<byte[]> getByteArrayList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_BYTE_ARRAY ) {
            final List<byte[]> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((ByteArrayTag) entry).getAsByteArray() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of int array values.
     */
    public static boolean containsIntArrayList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_INT_ARRAY ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the int array list in.
     * @param list The list to write.
     */
    public static void putIntArrayList( CompoundTag tag, String name, List<int[]> list ) {
        final ListTag listTag = new ListTag();
        for( int[] entry : list ) listTag.add( new IntArrayTag( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<int[]> getIntArrayList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_INT_ARRAY ) {
            final List<int[]> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((IntArrayTag) entry).getAsIntArray() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list of long array values.
     */
    public static boolean containsLongArrayList( CompoundTag tag, String name ) { return containsList( tag, name, TAG_LONG_ARRAY ); }
    
    /**
     * Convenience method for writing a list to NBT.
     *
     * @param tag  The compound tag to write to.
     * @param name The name of the list tag to put the long array list in.
     * @param list The list to write.
     */
    public static void putLongArrayList( CompoundTag tag, String name, List<long[]> list ) {
        final ListTag listTag = new ListTag();
        for( long[] entry : list ) listTag.add( new LongArrayTag( entry ) );
        tag.put( name, listTag );
    }
    
    /**
     * Convenience method for loading a list from NBT.
     *
     * @param tag  The compound tag to read from.
     * @param name The name of the desired list tag.
     * @return The loaded list, or an empty list if the tag does not exist or encounters a problem.
     */
    public static List<long[]> getLongArrayList( CompoundTag tag, String name ) {
        if( tag.get( name ) instanceof ListTag listTag && listTag.getElementType() == TAG_LONG_ARRAY ) {
            final List<long[]> list = new ArrayList<>( listTag.size() );
            for( Tag entry : listTag ) list.add( ((LongArrayTag) entry).getAsLongArray() );
            return list;
        }
        return new ArrayList<>();
    }
    
    /** Performs the actual 'contains' check for typed lists. */
    private static boolean containsList( CompoundTag tag, String name, byte id ) {
        return contains( tag, name, TAG_LIST ) && tag.get( name ) instanceof ListTag listTag &&
                (listTag.getElementType() == id || listTag.getElementType() == 0 /* Empty list */ ||
                        id == TAG_ANY_NUMERIC && isNumericId( listTag.getElementType() ));
    }
    
    
    // Utility class
    private NBTHelper() {}
}