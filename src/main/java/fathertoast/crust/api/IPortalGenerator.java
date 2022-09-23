package fathertoast.crust.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface IPortalGenerator {

    /**
     * Generates a portal in the world. This method is always
     * called sever-side, via command.<br>
     * <br>
     *
     * @param world The command sender's world object.
     * @param currentPos The position of the command sender.
     * @param forward The direction the command sender is facing.
     */
    void generate(World world, BlockPos.Mutable currentPos, Direction forward);
}
