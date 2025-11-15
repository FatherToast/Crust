package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.util.IDebugShape;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * Renders a shape in the world.
 * <br>
 * Must be registered to the shape(s) it can render in {@link DebugShapeRenderManager}.
 */
public interface IDebugShapeRenderer<S extends IDebugShape> {
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    void renderShape( S shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix );
}