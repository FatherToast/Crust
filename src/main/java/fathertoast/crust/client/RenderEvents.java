package fathertoast.crust.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.client.util.shape.DebugShapeRenderManager;
import fathertoast.crust.api.util.IBlockEntityBBProvider;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public final class RenderEvents {
    
    /**
     * Attempts to draw any shapes in the list at the provided position.
     */
    public static void renderShapes( @Nullable List<IDebugShape> shapes, @Nullable Vec3 pos, RenderLevelStageEvent event ) {
        if( shapes != null && !shapes.isEmpty() ) {
            final PoseStack poseStack = event.getPoseStack();
            final Vec3 cameraPos = event.getCamera().getPosition();
            poseStack.pushPose();
            // Only move relative to camera position
            poseStack.translate( -cameraPos.x(), -cameraPos.y(), -cameraPos.z() );
            
            shapes.forEach( ( shape ) -> DebugShapeRenderManager.renderShape( shape, pos, event ) );
            
            poseStack.popPose();
        }
    }
    
    @SubscribeEvent
    @SuppressWarnings( "ConstantConditions" )
    static void onRenderLevelStage( RenderLevelStageEvent event ) {
        if( DebugShapeRenderManager.shouldRenderHitBoxes() ) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if( player != null ) {
                if( event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS ) {
                    // Render entity debug shapes right before entities
                    if( ClientRegister.RENDER_SETTINGS.entityShapes.get() ) {
                        renderEntityShapes( player, event );
                    }
                }
                else if( event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES ) {
                    // Render block entity debug shapes right before block entities
                    if( ClientRegister.RENDER_SETTINGS.blockEntityShapes.get() ) {
                        renderBlockEntityShapes( player, event );
                    }
                }
            }
        }
    }
    
    /**
     * Searches for all entities in range that implement {@link IDebugShapeProvider}
     * and attempts to draw any shapes they provide.
     */
    private static void renderEntityShapes( LocalPlayer player, RenderLevelStageEvent event ) {
        final ClientLevel level = (ClientLevel) player.level();
        
        // Radius value from config, effectively capped by how far away the client tracks entities
        final double radiusSqr = ClientRegister.RENDER_SETTINGS.entityShapesDistanceSqr.get();
        
        // Loop through all entities to check for shape providers
        for( Entity entity : level.entitiesForRendering() ) {
            if( player.distanceToSqr( entity ) < radiusSqr && entity instanceof IDebugShapeProvider shapeProvider ) {
                renderShapes( shapeProvider.getDebugShapes(), shapeProvider.useWorldPosition() ? entity.position() : null, event );
            }
        }
    }
    
    /**
     * Searches for all block entities in range that implement {@link IDebugShapeProvider}
     * or {@link IBlockEntityBBProvider} and attempts to draw any shapes they provide,
     * centered on their block positions.
     */
    private static void renderBlockEntityShapes( LocalPlayer player, RenderLevelStageEvent event ) {
        final Level level = player.level();
        final BlockPos playerPos = player.blockPosition();
        final int chunkX = playerPos.getX() >> 4;
        final int chunkZ = playerPos.getZ() >> 4;
        
        // Radius value from config, capped at current effective render distance
        final int radius = Math.min( ClientRegister.RENDER_SETTINGS.blockEntityShapesDistance.get(),
                Minecraft.getInstance().options.getEffectiveRenderDistance() );
        
        // Don't bother with the crazy loop if we only render for one chunk
        if( radius <= 1 ) {
            renderTileEntityShapes( level, chunkX, chunkZ, event );
        }
        else {
            // Loop through all chunks in range to check for tile entities
            for( int x = chunkX - (radius - 1); x < chunkX + radius; x++ ) {
                for( int z = chunkZ - (radius - 1); z < chunkZ + radius; z++ ) {
                    renderTileEntityShapes( level, x, z, event );
                }
            }
        }
    }
    
    /**
     * Searches for all block entities in the chunk that implement {@link IDebugShapeProvider}
     * or {@link IBlockEntityBBProvider} and attempts to draw any shapes they provide.
     */
    private static void renderTileEntityShapes( Level level, int chunkX, int chunkZ, RenderLevelStageEvent event ) {
        if( level.hasChunk( chunkX, chunkZ ) ) {
            for( BlockEntity blockEntity : level.getChunk( chunkX, chunkZ ).getBlockEntities().values() ) {
                if( blockEntity instanceof IDebugShapeProvider shapeProvider ) {
                    renderShapes( shapeProvider.getDebugShapes(), shapeProvider.useWorldPosition() ? blockEntity.getBlockPos().getCenter() : null, event );
                }
                
                // TODO Remove when updating beyond 1.20.1
                else {
                    legacyRenderBlockEntityBBs( blockEntity, event );
                }
            }
        }
    }
    
    /**
     * Checks if the given block entity is an instance of {@link IDebugShapeProvider}
     * or {@link IBlockEntityBBProvider} and attempts to draw any shapes it provides.
     */
    @Deprecated( forRemoval = true ) // TODO Remove when updating beyond 1.20.1
    private static void legacyRenderBlockEntityBBs( BlockEntity blockEntity, RenderLevelStageEvent event ) {
        //noinspection removal
        if( blockEntity instanceof IBlockEntityBBProvider bbProvider ) {
            //noinspection removal
            final List<AABB> boxes = bbProvider.getBoundingBoxes();
            if( boxes == null || boxes.isEmpty() ) return;
            
            final PoseStack poseStack = event.getPoseStack();
            final Vec3 cameraPos = event.getCamera().getPosition();
            final VertexConsumer linesBuffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer( RenderType.lines() );
            poseStack.pushPose();
            poseStack.translate( -cameraPos.x, -cameraPos.y, -cameraPos.z ); // Only move relative to camera position
            
            for( AABB box : boxes ) {
                if( box != null ) {
                    LevelRenderer.renderLineBox( poseStack, linesBuffer, box,
                            0.0F, 1.0F, 0.0F, 1.0F );
                }
            }
            poseStack.popPose();
        }
    }
    
    // Static listener, no instantiation
    private RenderEvents() { }
}