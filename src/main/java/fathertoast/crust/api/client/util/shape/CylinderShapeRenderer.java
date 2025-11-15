
package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.util.shape.CylinderShape;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderer for a circle shape. Semi-well rounded.
 */
public class CylinderShapeRenderer implements IDebugShapeRenderer<CylinderShape> {
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( CylinderShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        final Vec3 p = pos == null ? shape.pos() : shape.pos().add( pos );
        final Vector3f n = shape.direction().step();
        final float r = shape.radius();
        final float hh = shape.height() / 2.0F;
        float hX = hh * n.x();
        float hY = hh * n.y();
        float hZ = hh * n.z();
        final List<Vector3f> vertexCollector = new ArrayList<>();
        CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                (float) p.x() + hX, (float) p.y() + hh * n.y(), (float) p.z() + hh * n.z(),
                shape.red(), shape.green(), shape.blue(), shape.alpha(),
                n, r, vertexCollector );
        CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                (float) p.x() - hX, (float) p.y() - hh * n.y(), (float) p.z() - hh * n.z(),
                shape.red(), shape.green(), shape.blue(), shape.alpha(),
                n, r, null );
        
        // Draw connecting lines
        final VertexConsumer linesBuffer = DebugShapeRenderManager.getLinesBuffer();
        int lgt = vertexCollector.size();
        poseStack.pushPose();
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();
        for( int i = 0; i < lgt; i += 2 ) {
            Vector3f vertex = vertexCollector.get( i );
            
            float x = (float) p.x() + r * vertex.x();
            float y = (float) p.y() + r * vertex.y();
            float z = (float) p.z() + r * vertex.z();
            
            linesBuffer.vertex( pose, x - hX, y - hY, z - hZ )
                    .color( shape.red(), shape.green(), shape.blue(), shape.alpha() )
                    .normal( normal, vertex.x(), vertex.y(), vertex.z() ).endVertex();
            linesBuffer.vertex( pose, x + hX, y + hY, z + hZ )
                    .color( shape.red(), shape.green(), shape.blue(), shape.alpha() )
                    .normal( normal, vertex.x(), vertex.y(), vertex.z() ).endVertex();
            
        }
        poseStack.popPose();
    }
}