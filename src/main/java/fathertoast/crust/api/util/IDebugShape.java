package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

public interface IDebugShape {
    
    void renderShape( PoseStack poseStack, Vec3 cameraPos, VertexConsumer buffer );
}