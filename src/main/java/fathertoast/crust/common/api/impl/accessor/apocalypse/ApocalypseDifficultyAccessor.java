package fathertoast.crust.common.api.impl.accessor.apocalypse;

import com.toast.apocalypse.api.IDifficultyAccessor;
import fathertoast.crust.api.IApocalypseDifficultyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Helper class for accessing Apocalypse difficulty data.
 * (Take care not to call any of this if Apocalypse is not installed, will result in runtime-anger :biglist:)
 */
public final class ApocalypseDifficultyAccessor implements IApocalypseDifficultyAccessor {
    
    // Apocalypse's provider.
    private IDifficultyAccessor provider;
    
    public void setDifficultyProvider( IDifficultyAccessor provider ) {
        this.provider = provider;
    }
    
    /** @return The rate at which difficulty is increasing for a player. */
    @Override
    public double getDifficultyRate( Player player ) { return provider.getDifficultyRate( player ); }
    
    /** @return The difficulty for a player. */
    @Override
    public long getPlayerDifficulty( Player player ) { return provider.getPlayerDifficulty( player ); }
    
    /** @return The difficulty for the player nearest to a location. */
    @Override
    public long getNearestPlayerDifficulty( Level level, BlockPos origin ) {
        return getNearestPlayerDifficulty( level, origin, -1 );
    }
    
    /** @return The difficulty for the player nearest to a location, with a max search radius. */
    @Override
    public long getNearestPlayerDifficulty( Level level, BlockPos origin, double searchRadius ) {
        Player player = level.getNearestPlayer( origin.getX(), origin.getY(), origin.getZ(),
                searchRadius, false );
        return player == null ? 0 : provider.getPlayerDifficulty( player );
    }
    
    /** @return The max difficulty for a player. */
    @Override
    public long getMaxPlayerDifficulty( Player player ) { return provider.getMaxPlayerDifficulty( player ); }
    
    /** @return The id for the currently running event. */
    @Override
    public List<Integer> currentEventIds( ServerPlayer player ) { return provider.getEventIds( player ); }
}