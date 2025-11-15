package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.util.IDebugShape;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * Renderer for a shape that doesn't have anything to render.
 */
public final class NullShapeRenderer implements IDebugShapeRenderer<IDebugShape> {
    
    private static final NullShapeRenderer INSTANCE = new NullShapeRenderer();
    
    public static NullShapeRenderer getInstance() { return INSTANCE; }
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( IDebugShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) { }
    
    private NullShapeRenderer() { }
}