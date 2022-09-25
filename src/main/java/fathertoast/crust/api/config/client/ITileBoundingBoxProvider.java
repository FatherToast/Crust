package fathertoast.crust.api.config.client;

import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 *  This interface can be implemented into a TileEntity
 *  that have bounding boxes that should be rendered
 *  (similar to how entity hitboxes are rendered).
 */
public interface ITileBoundingBoxProvider {

    /**
     * @return A List of bounding boxes that should be rendered in the world
     */
    List<AxisAlignedBB> getBoundingBoxes();
}
