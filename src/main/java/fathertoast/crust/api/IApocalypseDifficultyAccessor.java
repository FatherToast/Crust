package fathertoast.crust.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Interface for accessing Apocalypse difficulty data.
 */
public interface IApocalypseDifficultyAccessor {
    
    /** @return The rate at which difficulty is increasing for a player. */
    double getDifficultyRate( Player player );
    
    /** @return The difficulty for a player. */
    long getPlayerDifficulty( Player player );
    
    /** @return The difficulty for the player nearest to a location. */
    long getNearestPlayerDifficulty( Level level, BlockPos origin );
    
    /** @return The difficulty for the player nearest to a location, with a max search radius. */
    long getNearestPlayerDifficulty( Level level, BlockPos origin, double searchRadius );
    
    /** @return The max difficulty for a player. */
    long getMaxPlayerDifficulty( Player player );
    
    /** @return The id for the currently running event. */
    List<Integer> currentEventIds( ServerPlayer player );
}