package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

public class SphereMeshShape extends ColoredShape {

    private double radius;

    public SphereMeshShape( double radius, int color ) {
        super( color );
    }

    @Override
    public void renderShape( PoseStack poseStack, Vec3 cameraPos, VertexConsumer buffer ) {

    }

    public void setRadius( double radius ) {
        this.radius = radius;
    }

    private void drawCircle() {

    }
}
