package fathertoast.crust.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fathertoast.crust.api.entity.CrustFishingHook;
import fathertoast.crust.api.entity.IAngler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CrustFishingHookRenderer<T extends CrustFishingHook> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace( "textures/entity/fishing_hook.png" );
    private static final RenderType RENDER_TYPE = RenderType.entityCutout( TEXTURE_LOCATION );
    
    public CrustFishingHookRenderer( EntityRendererProvider.Context context ) { super( context ); }
    
    @Override
    public ResourceLocation getTextureLocation( T entity ) { return TEXTURE_LOCATION; }
    
    @Override
    public void render( T hook, float rotation, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        poseStack.pushPose();
        
        // Render the hook/bobber texture
        poseStack.pushPose();
        renderFishingHook( hook, rotation, partialTick, poseStack, buffer, packedLight );
        poseStack.popPose();
        
        // Render the fishing line connecting the top of the bobber to the tip of angler's fishing rod
        renderFishingLine( hook, rotation, partialTick, poseStack, buffer, packedLight );
        
        poseStack.popPose();
        
        super.render( hook, rotation, partialTick, poseStack, buffer, packedLight );
    }
    
    /** Render the fishing hook itself. */
    protected void renderFishingHook( T hook, float rotation, float partialTick,
                                      PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        poseStack.scale( 0.5F, 0.5F, 0.5F );
        poseStack.mulPose( entityRenderDispatcher.cameraOrientation() );
        poseStack.mulPose( Axis.YP.rotationDegrees( 180.0F ) );
        final PoseStack.Pose lastPose = poseStack.last();
        drawQuad( buffer.getBuffer( RENDER_TYPE ), lastPose.pose(), lastPose.normal(), packedLight );
    }
    
    /** Render the fishing line from the hook to the angler. */
    protected void renderFishingLine( T hook, float rotation, float partialTick,
                                      PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        // Get the position to draw the line to; if null, skip line drawing
        final IAngler angler = hook.getAngler();
        final Vec3 linePos = angler == null ? null : angler.getLinePos( partialTick );
        if( linePos == null ) return;
        
        // Get the position of the hook
        final double xHook = Mth.lerp( partialTick, hook.xo, hook.getX() );
        final double yHook = Mth.lerp( partialTick, hook.yo, hook.getY() ) +
                getHookYOffset( hook, rotation, partialTick );
        final double zHook = Mth.lerp( partialTick, hook.zo, hook.getZ() );
        
        drawFishingLine( buffer.getBuffer( RenderType.lineStrip() ), poseStack.last(),
                16, xHook, yHook, zHook, linePos.x(), linePos.y(), linePos.z() );
    }
    
    /** @return The y-offset from the hook to draw the line to. */
    protected double getHookYOffset( T hook, float rotation, float partialTick ) { return 0.25; }
    
    /** Creates the vertexes for a quad. */
    public static void drawQuad( VertexConsumer vertexConsumer, Matrix4f pose, Matrix3f normal, int packedLight ) {
        quadVertex( vertexConsumer, pose, normal, packedLight, 0.0F, 0.0F, 0, 1 );
        quadVertex( vertexConsumer, pose, normal, packedLight, 1.0F, 0.0F, 1, 1 );
        quadVertex( vertexConsumer, pose, normal, packedLight, 1.0F, 1.0F, 1, 0 );
        quadVertex( vertexConsumer, pose, normal, packedLight, 0.0F, 1.0F, 0, 0 );
    }
    
    public static void quadVertex( VertexConsumer vertexConsumer, Matrix4f pose, Matrix3f normal, int packedLight,
                                   float dX, float dY, int u, int v ) {
        vertexConsumer.vertex( pose, dX - 0.5F, dY - 0.5F, 0.0F )
                .color( 0xFF, 0xFF, 0xFF, 0xFF ) // RGBA - white is no tint
                .uv( u, v ).overlayCoords( OverlayTexture.NO_OVERLAY ).uv2( packedLight )
                .normal( normal, 0.0F, 1.0F, 0.0F ).endVertex();
    }
    
    /** Creates the vertexes for the fishing line; a line from the bobber to the rod that hangs down a little in the y-axis. */
    public static void drawFishingLine( VertexConsumer vertexConsumer, PoseStack.Pose pose, int resolution,
                                        double x1, double y1, double z1, double x2, double y2, double z2 ) {
        final float dX = (float) (x2 - x1);
        final float dY = (float) (y2 - y1);
        final float dZ = (float) (z2 - z1);
        
        for( int segment = 0; segment <= resolution; segment++ ) { // resolution + 1 vertexes
            // Each line segment is defined by 2 vertexes
            lineVertex( dX, dY, dZ, vertexConsumer, pose, segment, resolution );
        }
    }
    
    public static void lineVertex( float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose,
                                   float segment, float totalSegments ) {
        final float r = segment / totalSegments;
        final float k = segment + 1 / totalSegments;
        
        float vertX = x * r;
        float vertY = y * (r * r + r) * 0.5F + 0.25F;
        float vertZ = z * r;
        
        float normalX = x * k - vertX;
        float normalY = y * (k * k + k) * 0.5F + 0.25F - vertY;
        float normalZ = z * k - vertZ;
        
        float sqRoot = Mth.sqrt( normalX * normalX + normalY * normalY + normalZ * normalZ );
        
        normalX /= sqRoot;
        normalY /= sqRoot;
        normalZ /= sqRoot;
        
        vertexConsumer.vertex( pose.pose(), vertX, vertY, vertZ )
                .color( 0x00, 0x00, 0x00, 0xFF )
                .normal( pose.normal(), normalX, normalY, normalZ )
                .endVertex();
    }
}