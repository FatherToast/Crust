package fathertoast.crust.api.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import static net.minecraftforge.common.util.Constants.NBT.*;

@SuppressWarnings( "unused" )
public class NBTHelper {
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a list value.
     */
    public static boolean containsList( CompoundNBT tag, String name ) { return contains( tag, name, TAG_LIST ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores another compound value.
     */
    public static boolean containsCompound( CompoundNBT tag, String name ) { return contains( tag, name, TAG_COMPOUND ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a string value.
     */
    public static boolean containsString( CompoundNBT tag, String name ) { return contains( tag, name, TAG_STRING ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a numerical value.
     */
    public static boolean containsNumber( CompoundNBT tag, String name ) { return contains( tag, name, TAG_ANY_NUMERIC ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a byte array value.
     */
    public static boolean containsByteArray( CompoundNBT tag, String name ) { return contains( tag, name, TAG_BYTE_ARRAY ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores an int array value.
     */
    public static boolean containsIntArray( CompoundNBT tag, String name ) { return contains( tag, name, TAG_INT_ARRAY ); }
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the tag to check.
     * @return True if the compound contains a tag with the given name that stores a long array value.
     */
    public static boolean containsLongArray( CompoundNBT tag, String name ) { return contains( tag, name, TAG_LONG_ARRAY ); }
    
    /**
     * Performs the actual 'contains' check.
     *
     * @see net.minecraftforge.common.util.Constants.NBT
     */
    private static boolean contains( CompoundNBT tag, String name, int id ) { return tag.contains( name, id ); }
    
    
    /**
     * @param tag  The compound tag to read from.
     * @param name The name of the desired compound tag.
     * @return The retrieved compound tag, or a newly created and saved tag if none existed.
     */
    public static CompoundNBT getOrCreateCompound( CompoundNBT tag, String name ) {
        if( !containsCompound( tag, name ) ) tag.put( name, new CompoundNBT() );
        return tag.getCompound( name );
    }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to not store data in the Forge data compound directly, but instead store your mod's data in
     * a compound tag within the Forge data compound.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(PlayerEntity, String)}.
     *
     * @return The entity's Forge data at 'ForgeData'.
     * @see NBTHelper#getForgeData(Entity, String)
     */
    public static CompoundNBT getForgeData( Entity entity ) { return entity.getPersistentData(); }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(PlayerEntity, String)}.
     *
     * @return A compound tag at 'ForgeData/name'.
     * @see NBTHelper#getForgeData(Entity, String, String)
     */
    public static CompoundNBT getForgeData( Entity entity, String name ) {
        return getOrCreateCompound( getForgeData( entity ), name );
    }
    
    /**
     * This allows additional data to be stored with the entity.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     * <p>
     * For long-term player data, see {@link NBTHelper#getPlayerData(PlayerEntity, String)}.
     *
     * @return A compound tag at 'ForgeData/name/subName'.
     * @see NBTHelper#getForgeData(Entity, String)
     */
    public static CompoundNBT getForgeData( Entity entity, String name, String subName ) {
        return getOrCreateCompound( getOrCreateCompound( getForgeData( entity ), name ), subName );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     * <p>
     * It is recommended to not store data into the persistent player data directly, but
     * instead store your mod's data in a compound tag within the persistent player data tag.
     *
     * @return The player's persistent data at 'ForgeData/PlayerPersisted'.
     * @see NBTHelper#getPlayerData(PlayerEntity, String)
     * @see NBTHelper#getPlayerData(PlayerEntity, String, String)
     */
    public static CompoundNBT getPlayerData( PlayerEntity player ) {
        return getForgeData( player, PlayerEntity.PERSISTED_NBT_TAG );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     * <p>
     * It is recommended to use your mod's id as the tag name.
     *
     * @return A compound tag at 'ForgeData/PlayerPersisted/name'.
     * @see NBTHelper#getPlayerData(PlayerEntity, String, String)
     */
    public static CompoundNBT getPlayerData( PlayerEntity player, String name ) {
        return getOrCreateCompound( getPlayerData( player ), name );
    }
    
    /**
     * This tag differs from 'Forge data' in that it is preserved through death and interdimensional travel.
     *
     * @return A compound tag at 'ForgeData/PlayerPersisted/name/subName'.
     * @see NBTHelper#getPlayerData(PlayerEntity, String)
     */
    public static CompoundNBT getPlayerData( PlayerEntity player, String name, String subName ) {
        return getOrCreateCompound( getPlayerData( player, name ), subName );
    }
}