package fathertoast.crust.common.util.accessor.apocalypse;

import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Helper class for accessing Apocalypse difficulty data.
 * (Take care not to call any of this if Apocalypse is not installed, will result in runtime-anger :biglist:)
 */
public final class DifficultyAccessor {

    public double getDifficultyRate(PlayerEntity player) {
        return CapabilityHelper.getPlayerDifficultyMult(player);
    }

    public long getPlayerDifficulty(PlayerEntity player) {
        return CapabilityHelper.getPlayerDifficulty(player);
    }

    public long getNearestPlayerDifficulty(World world, BlockPos origin, double searchRadius) {
        PlayerEntity player = world.getNearestPlayer(origin.getX(), origin.getY(), origin.getZ(), searchRadius, false);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    public long getMaxPlayerDifficulty(PlayerEntity player) {
        return CapabilityHelper.getMaxPlayerDifficulty(player);
    }

    public int currentEventId(ServerPlayerEntity player) {
        return CapabilityHelper.getEventId(player);
    }
}
