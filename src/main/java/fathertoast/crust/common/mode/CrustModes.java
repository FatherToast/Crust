package fathertoast.crust.common.mode;

import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.mode.type.CrustByteMode;
import fathertoast.crust.common.mode.type.CrustFloatMode;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.potion.Effects;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The "modes" added by Crust.
 * <p>
 * Most of these modes have settings. In general, mode settings are requested by the client,
 * then approved, limited, or denied by the server's settings.
 */
public final class CrustModes {
    
    /** Registry of all modes. */
    private static final Map<String, CrustMode<?>> MODE_REGISTRY = new HashMap<>();
    
    /**
     * Registers the mode. Throws an exception if the mode's id is already taken.
     * This is normally called by the crust mode constructor, so there is no need to call this yourself.
     */
    public static void register( CrustMode<?> mode ) {
        if( MODE_REGISTRY.containsKey( mode.ID ) ) {
            throw new IllegalArgumentException( "Cannot register duplicate mode id \"" + mode.ID + "\"!" );
        }
        MODE_REGISTRY.put( mode.ID, mode );
    }
    
    
    /** @return The mode by id, or null if no such mode exists. */
    @SuppressWarnings( "unused" )
    @Nullable
    public static CrustMode<?> getMode( String id ) { return MODE_REGISTRY.get( id ); }
    
    /** @return A read-only view of the mode registry. */
    public static Map<String, CrustMode<?>> registry() { return Collections.unmodifiableMap( MODE_REGISTRY ); }
    
    
    /** Pulls nearby items toward you. */
    public static final CrustMode<Float> MAGNET = new CrustFloatMode( "magnet", CrustConfig.MODES.GENERAL.magnetOpLevel::get,
            ( player, value ) -> value == null || value <= 0.0F ? null :
                    Math.min( value, CrustConfig.MODES.MAGNET.maxRangeLimit.getFloat() ) );
    ///** Break multiple blocks at once. */
    //public static final CrustMode<Long> MULTI_MINE = new CrustLongMode( "multiMine", CrustConfig.MODES.GENERAL.multiMineOpLevel::get,
    //        ( player, value ) -> value );
    
    /** Cancels death and restores full health instead. */
    public static final CrustMode<Byte> UNDYING = new CrustByteMode( "undying", CrustConfig.MODES.GENERAL.undyingOpLevel::get );
    /** Fully repairs items periodically. */
    public static final CrustMode<Byte> UNBREAKING = new CrustByteMode( "unbreaking", CrustConfig.MODES.GENERAL.unbreakingOpLevel::get );
    /** Restores hunger and saturation when food level drops below a set threshold (1-20). */
    public static final CrustMode<Byte> UNEATING = new CrustByteMode( "uneating", CrustConfig.MODES.GENERAL.uneatingOpLevel::get,
            ( player, value ) -> value == null || value <= 0 ? null : (byte) Math.min( value, 20 ) );
    
    /** Grants night vision, removes fog, and clears blindness. */
    public static final CrustMode<Byte> SUPER_VISION = new CrustByteMode( "vision", CrustConfig.MODES.GENERAL.visionOpLevel::get,
            ( player, value ) -> {
                if( value == null ) player.removeEffect( Effects.NIGHT_VISION );
                return value;
            } );
    /** Dramatically increases sprint speed. */
    public static final CrustMode<Float> SUPER_SPEED = new CrustFloatMode( "speed", CrustConfig.MODES.GENERAL.speedOpLevel::get,
            ( player, value ) -> value == null || value <= 1.0F ? null :
                    Math.min( value, CrustConfig.MODES.SPEED.speedLimit.getFloat() ) );
    /** Prevents picked-up items from being added to your inventory (like creative mode arrows). */
    public static final CrustMode<Byte> DESTROY_ON_PICKUP = new CrustByteMode( "noPickup", CrustConfig.MODES.GENERAL.noPickupOpLevel::get );
}