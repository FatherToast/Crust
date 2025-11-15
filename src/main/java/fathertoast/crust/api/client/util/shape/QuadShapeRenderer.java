
package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.util.shape.QuadShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * Renderer for a quad shape (rectangle). Face it.
 */
public class QuadShapeRenderer implements IDebugShapeRenderer<QuadShape> {
    
    private static final float PI_4 = (float) Math.sin( Math.PI / 4.0 );
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( QuadShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        Vec3 p = pos == null ? shape.pos() : shape.pos().add( pos );
        renderQuad( poseStack, DebugShapeRenderManager.getLineStripBuffer(), (float) p.x(), (float) p.y(), (float) p.z(),
                shape.red(), shape.green(), shape.blue(), shape.alpha(),
                shape.direction().getAxis(), shape.halfWidth(), shape.halfHeight() );
    }
    
    public static void renderQuad( PoseStack poseStack, VertexConsumer lineStripBuffer, float x, float y, float z,
                                   float r, float g, float b, float a, Direction.Axis axis, float hw, float hh ) {
        switch( axis ) {
            case X -> renderQuadX( poseStack, lineStripBuffer, x, y, z, r, g, b, a, hw, hh );
            case Y -> renderQuadY( poseStack, lineStripBuffer, x, y, z, r, g, b, a, hw, hh );
            case Z -> renderQuadZ( poseStack, lineStripBuffer, x, y, z, r, g, b, a, hw, hh );
        }
    }
    
    /* Z
     * ^
     * |
     * |----> Y
     */
    public static void renderQuadX( PoseStack poseStack, VertexConsumer lineStripBuffer, float x, float y, float z,
                                    float r, float g, float b, float a, float hw, float hh ) {
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();
        lineStripBuffer.vertex( pose, x, y - hh, z - hw )
                .color( r, g, b, a )
                .normal( normal, 0.0F, -PI_4, -PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x, y + hh, z - hw )
                .color( r, g, b, a )
                .normal( normal, 0.0F, +PI_4, -PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x, y + hh, z + hw )
                .color( r, g, b, a )
                .normal( normal, 0.0F, +PI_4, +PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x, y - hh, z + hw )
                .color( r, g, b, a )
                .normal( normal, 0.0F, -PI_4, +PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x, y - hh, z - hw )
                .color( r, g, b, a )
                .normal( normal, 0.0F, -PI_4, -PI_4 ).endVertex();
    }
    
    /* X
     * ^
     * |
     * |----> Z
     */
    public static void renderQuadY( PoseStack poseStack, VertexConsumer lineStripBuffer, float x, float y, float z,
                                    float r, float g, float b, float a, float hw, float hh ) {
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();
        lineStripBuffer.vertex( pose, x - hw, y, z - hh )
                .color( r, g, b, a )
                .normal( normal, -PI_4, 0.0F, -PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x - hw, y, z + hh )
                .color( r, g, b, a )
                .normal( normal, -PI_4, 0.0F, +PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x + hw, y, z + hh )
                .color( r, g, b, a )
                .normal( normal, +PI_4, 0.0F, +PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x + hw, y, z - hh )
                .color( r, g, b, a )
                .normal( normal, +PI_4, 0.0F, -PI_4 ).endVertex();
        lineStripBuffer.vertex( pose, x - hw, y, z - hh )
                .color( r, g, b, a )
                .normal( normal, -PI_4, 0.0F, -PI_4 ).endVertex();
    }
    
    /* Y
     * ^
     * |
     * |----> X
     */
    public static void renderQuadZ( PoseStack poseStack, VertexConsumer lineStripBuffer, float x, float y, float z,
                                    float r, float g, float b, float a, float hw, float hh ) {
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();
        lineStripBuffer.vertex( pose, x - hw, y - hh, z )
                .color( r, g, b, a )
                .normal( normal, -PI_4, -PI_4, 0.0F ).endVertex();
        lineStripBuffer.vertex( pose, x + hw, y - hh, z )
                .color( r, g, b, a )
                .normal( normal, +PI_4, -PI_4, 0.0F ).endVertex();
        lineStripBuffer.vertex( pose, x + hw, y + hh, z )
                .color( r, g, b, a )
                .normal( normal, +PI_4, +PI_4, 0.0F ).endVertex();
        lineStripBuffer.vertex( pose, x - hw, y + hh, z )
                .color( r, g, b, a )
                .normal( normal, -PI_4, +PI_4, 0.0F ).endVertex();
        lineStripBuffer.vertex( pose, x - hw, y - hh, z )
                .color( r, g, b, a )
                .normal( normal, -PI_4, -PI_4, 0.0F ).endVertex();
    }
}