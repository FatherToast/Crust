package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BoxShape extends ColoredShape {
    
    protected AABB box;
    
    public BoxShape( double minX, double minY, double minZ, double maxX, double maxY, double maxZ ) {
        this( new AABB( minX, minY, minZ, maxX, maxY, maxZ ) );
    }
    
    public BoxShape( double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float a ) {
        this( new AABB( minX, minY, minZ, maxX, maxY, maxZ ), a );
    }
    
    public BoxShape( double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
                     float a, float r, float g, float b ) {
        this( new AABB( minX, minY, minZ, maxX, maxY, maxZ ),
                a, r, g, b );
    }
    
    public BoxShape( AABB boundingBox ) { this( boundingBox, 1.0F ); }
    
    public BoxShape( AABB boundingBox, float a ) { this( boundingBox, a, 0.0F, 1.0F, 0.0F ); }
    
    public BoxShape( AABB boundingBox, float a, float r, float g, float b ) {
        super( a, r, g, b );
        box = boundingBox;
    }
    
    @Override
    public void renderShape (PoseStack poseStack, @Nullable BlockPos pos, Vec3 cameraPos, VertexConsumer buffer ) {
        LevelRenderer.renderLineBox( poseStack, buffer, box,
                red, green, blue, alpha );
    }

    public void setBounds( double minX, double minY, double minZ, double maxX, double maxY, double maxZ ) {
        box.setMinX( minX ); box.setMaxX( maxX );
        box.setMinY( minY ); box.setMaxY( maxY );
        box.setMinZ( minZ ); box.setMaxZ( maxZ );
    }
}