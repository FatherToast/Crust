package fathertoast.crust.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.util.IBlockEntityBBProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class RenderEvents {

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onRenderLevelStage( RenderLevelStageEvent event ) {
        // Iterate through all block entities in proximity to the player
        // and check if they should render special bounding boxes
        if ( ClientRegister.RENDER_SETTINGS.BLOCK_ENTITY_BB_RENDERING.enabled.get() && event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES ) {
            ClientLevel level = Minecraft.getInstance().level;
            BlockPos playerPos = Minecraft.getInstance().player.blockPosition();
            VertexConsumer linesBuffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer( RenderType.lines() );
            int chunkX = playerPos.getX() >> 4;
            int chunkZ = playerPos.getZ() >> 4;
            // Radius value from config, capped at current effective render distance
            int radius = Math.min(
                    ClientRegister.RENDER_SETTINGS.BLOCK_ENTITY_BB_RENDERING.distance.get(),
                    Minecraft.getInstance().options.getEffectiveRenderDistance() );

            // Don't bother with a loop if we only render for one chunk
            if ( radius <= 1 ) {
                for ( BlockEntity blockEntity : level.getChunk( chunkX, chunkZ ).getBlockEntities().values() ) {
                    renderBoundingBoxes( blockEntity, event.getPoseStack(), event.getCamera().getPosition(), linesBuffer );
                }
            }
            else {
                for ( int x = chunkX - (radius - 1); x < chunkX + radius; x++ ) {
                    for ( int z = chunkZ - (radius - 1); z < chunkZ + radius; z++ ) {
                        if ( level.hasChunk( x, z ) ) {
                            for ( BlockEntity blockEntity : level.getChunk( x, z ).getBlockEntities().values() ) {
                                renderBoundingBoxes( blockEntity, event.getPoseStack(), event.getCamera().getPosition(), linesBuffer );
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Checks if the given BlockEntity is an instance of {@link IBlockEntityBBProvider}
     * and attempts to draw the bounding boxes it provides.
     */
    private void renderBoundingBoxes( BlockEntity blockEntity, PoseStack poseStack, Vec3 cameraPos, VertexConsumer buffer ) {
        if ( blockEntity instanceof IBlockEntityBBProvider bbProvider ) {
            List<AABB> boxes = bbProvider.getBoundingBoxes();

            if ( boxes == null || boxes.isEmpty() )
                return;

            // Don't render unless entity hitbox rendering is enabled
            if ( Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() &&
                    !Minecraft.getInstance().showOnlyReducedInfo() ) {

                poseStack.pushPose();
                poseStack.translate( -cameraPos.x, -cameraPos.y, -cameraPos.z ); // Only move relative to camera position

                for ( AABB box : boxes ) {
                    LevelRenderer.renderLineBox( poseStack, buffer, box, 0.0F, 1.0F, 0.0F, 1.0F );
                }
                poseStack.popPose();
            }
        }
    }
}
