package fathertoast.crust.common.mixin_work;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.config.client.ITileBoundingBoxProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ClientMixinHooks {

    // TODO - Test the integrity of this thing with an actual
    //        tile entity that has boxes (DeadlyWorld most likely)
    /**
     * Render various bounding boxes for tile entities
     * that provide them through {@link ITileBoundingBoxProvider}
     */
    public static void handleTileEntityRendering(BlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (blockEntity instanceof ITileBoundingBoxProvider) {
            List<AABB> boxes = ((ITileBoundingBoxProvider) blockEntity).getBoundingBoxes();

            if (boxes == null || boxes.isEmpty())
                return;

            EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

            if (renderDispatcher.shouldRenderHitBoxes() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                BlockPos pos = blockEntity.getBlockPos();

                for (AABB box : boxes) {
                    box = box.move(-pos.getX(), -pos.getY(), -pos.getZ());
                    LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), box, 0.0F, 1.0F, 0.0F, 1.0F);
                }
            }
        }
    }
}
