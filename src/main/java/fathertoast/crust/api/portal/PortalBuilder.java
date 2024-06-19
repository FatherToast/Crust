package fathertoast.crust.api.portal;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This class represents a portal builder.
 * Portal builders are used by Crust to generate
 * a specific type of portal with the <strong>/crustportal</strong>
 * command, or by binding the command to a Crust inventory button.<p></p>
 * To register your own portal builder, create a DeferredRegister with PortalBuilder.class as the base class.
 */
public abstract class PortalBuilder {
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public boolean isValidDimension( Level level ) { return isValidDimension( level.dimension().location() ); }
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public abstract boolean isValidDimension( ResourceLocation dimension );
    
    /**
     * Generates the portal in the world with a particular position and direction.
     *
     * @param level      The world to generate in.
     * @param currentPos The front-center position of the portal. This is often a block position directly
     *                   above a solid 'floor block'. This is mutable so that you can #move() it rather than
     *                   create numerous BlockPos objects.
     * @param forward    Horizontal facing of the portal; defined as the direction that a player is facing to
     *                   see the portal. By convention, the transverse direction is forward.getClockWise().
     */
    public abstract void generate( Level level, BlockPos.MutableBlockPos currentPos, Direction forward );
}