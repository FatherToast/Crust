package fathertoast.crust.api.util;

import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This interface can be implemented into a TileEntity
 * that have bounding boxes that should be rendered in
 * debug mode (similar to how entity hitboxes are rendered).
 */
public interface IBlockEntityBBProvider {
    
    /**
     * @return A List of bounding boxes that should be rendered in the world.
     * The provided bounding boxes will be rendered right before the TE.
     */
    @Nullable
    List<AABB> getBoundingBoxes();
}