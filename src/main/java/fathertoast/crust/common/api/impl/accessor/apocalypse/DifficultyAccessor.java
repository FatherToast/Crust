package fathertoast.crust.common.api.impl.accessor.apocalypse;

import com.toast.apocalypse.api.plugin.DifficultyProvider;
import com.toast.apocalypse.api.plugin.IApocalypseApi;
import fathertoast.crust.api.IDifficultyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Helper class for accessing Apocalypse difficulty data.
 * (Take care not to call any of this if Apocalypse is not installed, will result in runtime-anger :biglist:)
 */
public final class DifficultyAccessor implements IDifficultyAccessor {

    // Note to Toast man: I am opting for using Apocalypse's API
    // instead of directly accessing the capability helper getters
    // so we don't have to worry about me suddenly changing something and forgetting about it
    private DifficultyProvider provider;

    public void setDifficultyProvider( DifficultyProvider provider ) {
        this.provider = provider;
    }

    @Override
    public double getDifficultyRate( Player player ) { return provider.getDifficultyRate( player ); }
    
    @Override
    public long getPlayerDifficulty( Player player ) { return provider.getPlayerDifficulty( player ); }
    
    @Override
    public long getNearestPlayerDifficulty( Level level, BlockPos origin ) {
        return getNearestPlayerDifficulty( level, origin, -1 );
    }
    
    @Override
    public long getNearestPlayerDifficulty( Level level, BlockPos origin, double searchRadius ) {
        Player player = level.getNearestPlayer( origin.getX(), origin.getY(), origin.getZ(),
                searchRadius, false );
        return player == null ? 0 : provider.getPlayerDifficulty( player );
    }
    
    @Override
    public long getMaxPlayerDifficulty( Player player ) { return provider.getMaxPlayerDifficulty( player ); }
    
    @Override
    public int currentEventId( ServerPlayer player ) { return provider.currentEventId( player ); }
}