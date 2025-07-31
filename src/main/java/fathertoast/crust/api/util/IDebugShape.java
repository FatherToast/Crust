package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Represents a shape that may be rendered in the world.<br><br>
 * Primarily used to render debug shapes for block entities when<br>
 * entity bounding box rendering is enabled (F3 + B).
 */
public interface IDebugShape {

    /**
     * @param pos If we are rendering for a block entity, this is the position of the block entity in question.
     * @param cameraPos The current position of the camera.
     * @param buffer Usually a buffer instance using {@link RenderType#lines()}
     */
    void renderShape( PoseStack poseStack, @Nullable BlockPos pos, Vec3 cameraPos, VertexConsumer buffer );
}