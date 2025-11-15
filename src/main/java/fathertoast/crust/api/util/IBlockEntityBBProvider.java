package fathertoast.crust.api.util;

import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This interface can be implemented into a BlockEntity
 * that has bounding boxes that should be rendered in
 * debug mode (when entity hitboxes are rendered).
 * <p>
 * Will be removed beyond MC 1.20, use {@link IDebugShapeProvider}
 * instead for improved debug tools.
 */
@Deprecated( forRemoval = true )
public interface IBlockEntityBBProvider {
    
    /**
     * @return A List of bounding boxes that should be rendered in the world.
     * The provided bounding boxes will be rendered right before block entities.
     */
    @Nullable
    List<AABB> getBoundingBoxes();
}