package fathertoast.crust.api.impl.accessor.apocalypse;

import com.toast.apocalypse.common.util.CapabilityHelper;
import fathertoast.crust.api.IDifficultyAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Helper class for accessing Apocalypse difficulty data.
 * (Take care not to call any of this if Apocalypse is not installed, will result in runtime-anger :biglist:)
 */
public final class DifficultyAccessor implements IDifficultyAccessor {
    
    @Override
    public double getDifficultyRate( PlayerEntity player ) { return CapabilityHelper.getPlayerDifficultyMult( player ); }
    
    @Override
    public long getPlayerDifficulty( PlayerEntity player ) { return CapabilityHelper.getPlayerDifficulty( player ); }
    
    @Override
    public long getNearestPlayerDifficulty( World world, BlockPos origin ) {
        return getNearestPlayerDifficulty( world, origin, -1 );
    }
    
    @Override
    public long getNearestPlayerDifficulty( World world, BlockPos origin, double searchRadius ) {
        PlayerEntity player = world.getNearestPlayer( origin.getX(), origin.getY(), origin.getZ(),
                searchRadius, false );
        return player == null ? 0 : CapabilityHelper.getPlayerDifficulty( player );
    }
    
    @Override
    public long getMaxPlayerDifficulty( PlayerEntity player ) { return CapabilityHelper.getMaxPlayerDifficulty( player ); }
    
    @Override
    public int currentEventId( ServerPlayerEntity player ) { return CapabilityHelper.getEventId( player ); }
}