package fathertoast.crust.common.util.tile;

import fathertoast.crust.common.util.annotations.OnClient;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

@OnClient
public interface ITileBoundingBoxProvider {

    List<AxisAlignedBB> getBoundingBoxes();
}
