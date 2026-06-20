package fathertoast.crust.common.api.impl;

import fathertoast.crust.api.entity.IPlayerVelocityWatcher;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class that tracks all players' current positions and their positions last tick.
 */
public final class PlayerVelocityWatcher implements IPlayerVelocityWatcher {
    
    /** The player velocity instance used by Crust and passed around by the API. */
    public static final PlayerVelocityWatcher INSTANCE = new PlayerVelocityWatcher();
    
    /**
     * A map that tracks all players in existence.
     * We use a concurrency map implementation because we have
     * no way to guarantee what thread teleport events are fired on.
     */
    private final ConcurrentMap<UUID, Entry> TRACKER = new ConcurrentHashMap<>();
    
    /** Number of server ticks since the last cleanup. */
    private int cleanupCounter;
    
    
    // Singleton, only one global instance
    private PlayerVelocityWatcher() { }
    
    
    /** @return The player's velocity tracker entry. If none exists, a new one will be created. */
    @Override
    public Entry get( Player player ) {
        Entry trackerEntry = TRACKER.get( player.getUUID() );
        if( trackerEntry == null ) {
            trackerEntry = new Entry( player.position() );
            TRACKER.put( player.getUUID(), trackerEntry );
        }
        return trackerEntry;
    }
    
    /**
     * @return The entity's velocity. For players, their tracked velocity is returned.
     * For other entities, their {@link Entity#getDeltaMovement()} is returned.
     */
    @Override
    public Vec3 getVelocity( Entity entity ) {
        Objects.requireNonNull( entity );
        return entity instanceof Player player ? get( player ).velocity() : entity.getDeltaMovement();
    }
    
    /** Called for every player tick event. */
    @SubscribeEvent
    public void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.side.isServer() && event.phase == TickEvent.Phase.END ) {
            Entry trackerEntry = get( event.player );
            trackerEntry.update( event.player.position() );
        }
    }
    
    /** Called for every server tick event. */
    @SubscribeEvent
    public void onServerTick( TickEvent.ServerTickEvent event ) {
        // Periodically remove all currently disconnected players;
        //  this is a hugely low priority, so we only do it about once per hour
        if( event.phase == TickEvent.Phase.END && ++cleanupCounter >= 69_420 ) {
            cleanupCounter = 0;
            final PlayerList onlinePlayers = ServerLifecycleHooks.getCurrentServer().getPlayerList();
            TRACKER.keySet().removeIf( ( uuid ) -> onlinePlayers.getPlayer( uuid ) == null );
        }
    }
    
    /** Called when any child event of EntityTeleportEvent is fired, when an entity teleports. */
    @SubscribeEvent
    public void onEntityTeleport( EntityTeleportEvent event ) {
        // Reset tracked velocity for players when they are teleported.
        if( event.getEntity() instanceof Player player ) {
            Entry trackerEntry = TRACKER.get( player.getUUID() );
            if( trackerEntry != null ) {
                trackerEntry.reset( player.position() );
            }
        }
    }
    
    /** Called when the server starts shutting down. */
    @SubscribeEvent
    public void onServerStopping( ServerStoppingEvent event ) {
        cleanupCounter = 0;
        TRACKER.clear();
    }
}
