package fathertoast.crust.mixin_work;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.common.util.tile.ITileBoundingBoxProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ClientMixinHooks {

    // TODO - Test the integrity of this thing with an actual
    //        tile entity that has boxes (DeadlyWorld most likely)
    /**
     * Render various bounding boxes for tile entities
     * that provide them through {@link ITileBoundingBoxProvider}
     */
    public static void handleTileEntityRendering(TileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        if (tileEntity instanceof ITileBoundingBoxProvider) {
            List<AxisAlignedBB> boxes = ((ITileBoundingBoxProvider) tileEntity).getBoundingBoxes();

            if (boxes == null || boxes.isEmpty())
                return;

            EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();

            if (rendererManager.shouldRenderHitBoxes() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                BlockPos pos = tileEntity.getBlockPos();

                for (AxisAlignedBB box : boxes) {
                    box = box.move(-pos.getX(), -pos.getY(), -pos.getZ());
                    WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), box, 0.0F, 1.0F, 0.0F, 1.0F);
                }
            }
        }
    }
}
