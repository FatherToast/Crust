package fathertoast.crust.common.mode;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.config.CrustModesConfigFile;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.core.CrustForgeEvents;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class CrustModesData {
    
    /** The name for the Crust modes data tag. */
    public static final String TAG_NAME = "modes";
    
    /** @return A data helper that provides simple access to the player's Crust mode save data. */
    // We can cache by player UUID here, if needed - might be a little messy
    public static CrustModesData of( PlayerEntity player ) { return new CrustModesData( player ); }
    
    /** The player. */
    private final PlayerEntity PLAYER;
    /** The NBT compound that stores all mode save data. */
    private final CompoundNBT SAVE_TAG;
    
    /** Creates a new data helper that provides simple access to mode save data. */
    private CrustModesData( PlayerEntity player ) {
        PLAYER = player;
        CompoundNBT modTag = NBTHelper.getPlayerData( player, Crust.MOD_ID );
        boolean setDefaults = !player.level.isClientSide() && !modTag.contains( TAG_NAME, NBTHelper.ID_COMPOUND );
        SAVE_TAG = NBTHelper.getOrCreateCompound( modTag, TAG_NAME );
        
        if( setDefaults ) {
            CrustModesConfigFile.General config = CrustConfig.MODES.GENERAL;
            
            if( config.magnetDefault.get() > 0.0 ) enable( CrustModes.MAGNET, config.magnetDefault.getFloat() );
            //if( config.multiMineDefault.get() > 0 ) enable( CrustModes.MULTI_MINE, config.multiMineDefault.get() );
            if( config.undyingDefault.get() ) enable( CrustModes.UNDYING, (byte) 1 );
            if( config.unbreakingDefault.get() ) enable( CrustModes.UNBREAKING, (byte) 1 );
            if( config.uneatingDefault.get() > 0 ) enable( CrustModes.UNEATING, config.uneatingDefault.getByte() );
            if( config.visionDefault.get() ) enable( CrustModes.SUPER_VISION, (byte) 1 );
            if( config.speedDefault.get() > 1.0 ) enable( CrustModes.SUPER_SPEED, config.speedDefault.getFloat() );
            if( config.noPickupDefault.get() ) enable( CrustModes.DESTROY_ON_PICKUP, (byte) 1 );
        }
    }
    
    /** A string representation of this object. */
    @Override
    public String toString() { return PLAYER.getScoreboardName() + ":" + SAVE_TAG.toString(); }
    
    /** @return True if the mode is enabled; that is, if any save data for the mode exists. */
    public boolean enabled( CrustMode<?> mode ) { return mode.enabled( SAVE_TAG ); }
    
    /** @return The mode's saved data, or its non-null default value if no save data exists. */
    public <T> T get( CrustMode<T> mode ) { return mode.get( SAVE_TAG ); }
    
    /** Saves the mode's data. */
    public <T> void enable( CrustMode<T> mode, T data ) {
        if( !get( mode ).equals( data ) ) {
            mode.enable( SAVE_TAG, data );
            if( !PLAYER.level.isClientSide() ) CrustForgeEvents.markModesDirty( PLAYER );
        }
    }
    
    /** Disables the mode by deleting any existing save data. */
    public void disable( CrustMode<?> mode ) {
        if( mode.enabled( SAVE_TAG ) ) {
            mode.disable( SAVE_TAG );
            if( !PLAYER.level.isClientSide() ) CrustForgeEvents.markModesDirty( PLAYER );
        }
    }
    
    
    /** The NBT compound that stores all mode save data. Do NOT modify this directly if you don't need to. */
    public CompoundNBT getSaveTag() { return SAVE_TAG; }
}