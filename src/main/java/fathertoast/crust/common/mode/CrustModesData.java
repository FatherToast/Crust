package fathertoast.crust.common.mode;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.core.CrustForgeEvents;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class CrustModesData {
    /** The name for the Crust modes data tag. */
    public static final String TAG_NAME = "modes";
    
    /** The player. */
    private final PlayerEntity PLAYER;
    /** The NBT compound that stores all mode save data. */
    private final CompoundNBT SAVE_TAG;
    
    /** Creates a new data helper that provides simple access to mode save data. */
    public CrustModesData( PlayerEntity player ) {
        PLAYER = player;
        SAVE_TAG = NBTHelper.getPlayerData( player, Crust.MOD_ID, TAG_NAME );
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