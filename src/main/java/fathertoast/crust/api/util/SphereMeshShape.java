package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SphereMeshShape extends ColoredShape {

    private double radius;

    public SphereMeshShape( double radius, int color ) {
        super( color );
    }

    @Override
    public void renderShape( PoseStack poseStack, @Nullable BlockPos pos, Vec3 cameraPos, VertexConsumer buffer ) {
        // Don't know where to draw the shape
        if ( pos == null ) return;

    }

    public void setRadius( double radius ) {
        this.radius = radius;
    }

    private void drawCircle( VertexConsumer vertexConsumer, PoseStack.Pose pose, int resolution,
                             double x1, double y1, double z1, double x2, double y2, double z2 ) {


    }
}
