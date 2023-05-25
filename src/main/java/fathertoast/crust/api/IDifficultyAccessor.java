package fathertoast.crust.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for accessing Apocalypse Rebooted difficulty data.
 */
public interface IDifficultyAccessor {
    
    /** @return The rate at which difficulty is increasing for a player. */
    double getDifficultyRate( PlayerEntity player );
    
    /** @return The difficulty for a player. */
    long getPlayerDifficulty( PlayerEntity player );
    
    /** @return The difficulty for the player nearest to a location. */
    long getNearestPlayerDifficulty( World world, BlockPos origin );
    
    /** @return The difficulty for the player nearest to a location, with a max search radius. */
    long getNearestPlayerDifficulty( World world, BlockPos origin, double searchRadius );
    
    /** @return The max difficulty for a player. */
    long getMaxPlayerDifficulty( PlayerEntity player );
    
    /** @return The id for the currently running event. */
    int currentEventId( ServerPlayerEntity player );
}