
package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.util.shape.CircleShape;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Renderer for a circle shape. Semi-well rounded.
 */
public class CircleShapeRenderer implements IDebugShapeRenderer<CircleShape> {
    
    private static final Vector3f VEC_X = new Vector3f( 1.0F, 0.0F, 0.0F );
    private static final Vector3f VEC_Y = new Vector3f( 0.0F, 1.0F, 0.0F );
    private static final Vector3f VEC_Z = new Vector3f( 0.0F, 0.0F, 1.0F );
    
    /**
     * @param shape            The shape instance being rendered.
     * @param pos              The world position of the shape. May be null.
     * @param poseStack        The pose stack.
     * @param projectionMatrix The projection matrix.
     */
    @Override
    public void renderShape( CircleShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        Vec3 p = pos == null ? shape.pos() : shape.pos().add( pos );
        renderCircle( poseStack, DebugShapeRenderManager.getLineStripBuffer(), (float) p.x(), (float) p.y(), (float) p.z(),
                shape.red(), shape.green(), shape.blue(), shape.alpha(),
                shape.direction().step(), shape.radius(), null );
    }
    
    // Please ensure 'n' is a non-zero vector
    public static void renderCircle( PoseStack poseStack, VertexConsumer lineStripBuffer, float x, float y, float z,
                                     float r, float g, float b, float a, Vector3f n, float radius,
                                     @Nullable List<Vector3f> vertexCollector ) {
        // Keep resolution in multiples of 4 so there's a vertex on each axis,
        //  but aim to place at least 1 vertex every 2 blocks of distance
        final int resolution = Math.max( 4 * Mth.ceil( Mth.PI * radius / 4.0F ), 16 );
        final float step = 2.0F * Mth.PI / resolution;
        
        // Make two perpendicular vectors
        final Vector3f u = n.y() == 0.0F ? VEC_Y : n.x() == 0.0F ? VEC_X : n.z() == 0.0F ? VEC_Z :
                new Vector3f( 1.0F, n.x() + n.z() / -n.y(), 1.0F ).normalize();
        final Vector3f v = new Vector3f( n ).cross( u ).normalize();
        
        // Do the thing
        final Matrix4f pose = poseStack.last().pose();
        final Matrix3f normal = poseStack.last().normal();
        for( int s = 0; s <= resolution; s++ ) {
            float angle = s * step;
            float cos = Mth.cos( angle );
            float sin = Mth.sin( angle );
            
            float vX = cos * u.x() + sin * v.x();
            float vY = cos * u.y() + sin * v.y();
            float vZ = cos * u.z() + sin * v.z();
            
            lineStripBuffer.vertex( pose, x + radius * vX, y + radius * vY, z + radius * vZ )
                    .color( r, g, b, a )
                    .normal( normal, vX, vY, vZ ).endVertex();
            
            // So we can do stuff at the vertexes
            if( vertexCollector != null ) vertexCollector.add( new Vector3f( vX, vY, vZ ) );
        }
        // Remove repeat vertex
        if( vertexCollector != null ) vertexCollector.remove( vertexCollector.size() - 1 );
    }
}