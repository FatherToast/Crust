
package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.util.shape.SphereShape;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderer for a circle shape. Semi-well rounded.
 */
public class SphereShapeRenderer implements IDebugShapeRenderer<SphereShape> {
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( SphereShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        final Vec3 p = pos == null ? shape.pos() : shape.pos().add( pos );
        final Vector3f n = shape.direction().step();
        final float r = shape.radius();
        final List<Vector3f> vertexCollector = new ArrayList<>();
        CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                (float) p.x(), (float) p.y(), (float) p.z(),
                shape.red(), shape.green(), shape.blue(), shape.alpha(),
                n, r, vertexCollector );
        
        // Draw longitudinal lines
        int lgt = vertexCollector.size() / 2;
        for( int i = 0; i < lgt; i += 2 ) {
            Vector3f vertex = vertexCollector.get( i );
            poseStack.pushPose();
            CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                    (float) p.x(), (float) p.y(), (float) p.z(),
                    shape.red(), shape.green(), shape.blue(), shape.alpha(),
                    vertex, r, null );
            poseStack.popPose();
        }
        
        // Draw latitudinal lines
        // Same resolution as circle vertexes, but divided by 8 (half as many, for a quarter the circumference)
        final int resolution = Math.max( Mth.ceil( Mth.PI * r / 4.0F ) / 2, 2 );
        final float step = Mth.PI / 2.0F / resolution;
        for( int s = 1; s < resolution; s++ ) {
            float angle = s * step;
            float cos = Mth.cos( angle );
            float sin = Mth.sin( angle );
            
            float x = r * sin * n.x();
            float y = r * sin * n.y();
            float z = r * sin * n.z();
            
            poseStack.pushPose();
            CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                    (float) p.x() - x, (float) p.y() - y, (float) p.z() - z,
                    shape.red(), shape.green(), shape.blue(), shape.alpha(),
                    n, cos * r, null );
            poseStack.popPose();
            poseStack.pushPose();
            CircleShapeRenderer.renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(),
                    (float) p.x() + x, (float) p.y() + y, (float) p.z() + z,
                    shape.red(), shape.green(), shape.blue(), shape.alpha(),
                    n, cos * r, null );
            poseStack.popPose();
        }
    }
}