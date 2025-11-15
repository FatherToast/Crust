package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.util.BoxShape;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * Renderer for a box shape (cuboid). A classic.
 */
public class BoxShapeRenderer implements IDebugShapeRenderer<BoxShape> {
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( BoxShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        final AABB box = pos == null ? shape.bounds() : shape.bounds().move( pos );
        LevelRenderer.renderLineBox( poseStack, DebugShapeRenderManager.getLinesBuffer(), box,
                shape.red(), shape.green(), shape.blue(), shape.alpha() );
    }
}