package fathertoast.crust.api.entity;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents Crust's server-side player velocity watcher that tracks all players' current positions and their positions last tick.
 * The velocity watcher also listens to all child events of {@link net.minecraftforge.event.entity.EntityTeleportEvent} to
 * reset tracked velocity when players are teleported.
 * <br><br>
 * The implementation provided by Crust is thread-safe.
 * <br><br>
 * The instance can be obtained via Crust's {@link ICrustApi} instance with a crust plugin.
 */
public interface IPlayerVelocityWatcher {
    
    /** @return The player's velocity tracker entry. If none exists, a new one will be created. */
    Entry get( Player player );
    
    /**
     * @return The entity's velocity. For players, their tracked velocity is returned.
     * For other entities, their {@link Entity#getDeltaMovement()} is returned.
     */
    Vec3 getVelocity( Entity entity );
    
    
    /** Stores the tracked data for a single player. */
    class Entry {
        
        public double xPrev, yPrev, zPrev, x, y, z;
        
        public Entry( Vec3 pos ) { reset( pos ); }
        
        /** @return X-displacement since the last tick. */
        public double dX() { return x - xPrev; }
        
        /** @return Y-displacement since the last tick. */
        public double dY() { return y - yPrev; }
        
        /** @return Z-displacement since the last tick. */
        public double dZ() { return z - zPrev; }
        
        /** @return Displacement since the last tick; this is effectively 'instantaneous velocity'. */
        public Vec3 velocity() { return new Vec3( dX(), dY(), dZ() ); }
        
        /** @return True if speed is non-zero (above a small dead zone of about 0.0001). */
        public boolean isMoving() {
            return isMoving( 1.0E-4 );
        }
        
        /** @return True if speed is greater than the specified dead zone. */
        public boolean isMoving( double deadZone ) {
            return Math.abs( dX() ) > deadZone || Math.abs( dZ() ) > deadZone || Math.abs( dY() ) > deadZone;
        }
        
        /** Sets the current and previous positions to the same, current values. */
        public void reset( Vec3 pos ) {
            xPrev = x = pos.x;
            yPrev = y = pos.y;
            zPrev = z = pos.z;
        }
        
        /**
         * Called once per tick to update the current and previous positions.
         * Not meant to be called outside Crust's implementation of the velocity watcher.
         */
        @ApiStatus.Internal
        public void update( Vec3 pos ) {
            xPrev = x;
            yPrev = y;
            zPrev = z;
            x = pos.x;
            y = pos.y;
            z = pos.z;
        }
    }
}
