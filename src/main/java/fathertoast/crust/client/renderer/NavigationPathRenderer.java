package fathertoast.crust.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a render listener that keeps track of pathfinding mobs that should have their
 * current navigation path visualized and renders them in the world, right after blocks.
 * <br><br>
 * Pathfinding data does not exist on the client
 * in vanilla Minecraft, so Crust manually sends this data when needed.
 */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class NavigationPathRenderer {
    
    private static int cleanupCounter;
    
    
    /** A thread-safe map containing numeric entity level IDs mapped to a path. */
    private static final Map<Integer, Path> PATHS_TO_RENDER = new ConcurrentHashMap<>();
    
    
    /** Called when the player respawns and a new player entity is created to replace the old one. */
    @SuppressWarnings( "resource" )
    @SubscribeEvent
    public static void onPlayerEntityCloned( ClientPlayerNetworkEvent.Clone event ) {
        // Clear all entries if the player respawns in a different dimension
        if( event.getOldPlayer().level().dimension() != event.getNewPlayer().level().dimension() ) {
            PATHS_TO_RENDER.clear();
        }
    }
    
    /** Called at various stages during level rendering. */
    @SuppressWarnings( "ConstantConditions" )
    @SubscribeEvent
    public static void onRenderLevelStage( RenderLevelStageEvent event ) {
        if( event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS ) {
            // Check if pathfinding visualization is enabled
            if( !ClientRegister.RENDER_SETTINGS.renderPathfinding.get() ) return;
            
            final MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            final ClientLevel level = Minecraft.getInstance().level;
            final PoseStack poseStack = event.getPoseStack();
            final Vec3 cameraPos = event.getCamera().getPosition();
            
            for( Map.Entry<Integer, Path> entry : PATHS_TO_RENDER.entrySet() ) {
                Entity entity = level.getEntity( entry.getKey() );
                // Entity might be null if entity is removed before
                // we complete a cleanup cycle
                if( entity == null ) continue;
                
                double dist = Math.sqrt( entity.distanceToSqr( cameraPos ) );
                // Check that entity exists and is within range
                if( entity != null && dist <= ClientRegister.RENDER_SETTINGS.renderPathfindingDistance.get() ) {
                    renderEntry( level, entry.getValue(), cameraPos, poseStack, bufferSource );
                }
            }
        }
    }
    
    /** Called every tick on the logical client. */
    @SubscribeEvent
    public static void onClientTick( TickEvent.ClientTickEvent event ) {
        // Check if pathfinding visualization is enabled
        if( !ClientRegister.RENDER_SETTINGS.renderPathfinding.get() ) return;
        
        if( event.phase == TickEvent.Phase.END ) {
            // Check that the client level exists
            if( Minecraft.getInstance().level == null ) return;
            if( cleanupCounter++ >= 60 ) {
                // Remove entries whose associated entity has despawned
                PATHS_TO_RENDER.keySet().removeIf( ( id ) -> Minecraft.getInstance().level.getEntity( id ) == null );
            }
        }
    }
    
    /**
     * Renders the given path.
     *
     * @param level The client level object.
     * @param path  The navigation path to render.
     */
    // TODO - Some neater looking things, maybe
    private static void renderEntry( ClientLevel level, Path path, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource ) {
        // Display each node in the path
        for( int i = 0; i < path.nodes.size(); i++ ) {
            BlockPos pos = path.nodes.get( i ).asBlockPos();
            // Special block for the first node
            if( i == 0 ) {
                renderCenteredBlock( Blocks.BLUE_CONCRETE, pos, 0.6F, cameraPos, poseStack, bufferSource );
            }
            else {
                renderCenteredBlock( i >= path.getNextNodeIndex() ? Blocks.WHITE_CONCRETE : Blocks.YELLOW_CONCRETE, pos, 0.3F, cameraPos, poseStack, bufferSource );
            }
        }
        // Display the target position of the path
        renderCenteredBlock( path.canReach() ? Blocks.LIME_CONCRETE : Blocks.ORANGE_CONCRETE, path.getTarget(), 0.6F,
                cameraPos, poseStack, bufferSource );
    }
    
    /**
     * Helper method for rendering a scaled full-bright block centered at the given position.
     *
     * @param block        The block to render (its default block state).
     * @param pos          The block position to render the block at.
     * @param scale        The render scale to use when rendering the block.
     * @param cameraPos    The current position of the camera.
     * @param poseStack    The current pose stack provided by the level renderer.
     * @param bufferSource The buffer source.
     */
    @SuppressWarnings( "ConstantConditions" )
    private static void renderCenteredBlock( Block block, BlockPos pos, float scale, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource ) {
        poseStack.pushPose();
        poseStack.translate(
                pos.getX() - (cameraPos.x + ((scale - 1) / 2)),
                pos.getY() - (cameraPos.y + ((scale - 1) / 2)),
                pos.getZ() - (cameraPos.z + ((scale - 1) / 2))
        );
        poseStack.scale( scale, scale, scale );
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                block.defaultBlockState(), poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null
        );
        poseStack.popPose();
    }
    
    /**
     * Adds the given entity ID and path to the map of paths to visualize if the path is not null.
     * If the given path is null, the entry associated with the given entity ID is removed from the map,
     * if it exists.
     */
    public static void putEntry( int entityId, @Nullable Path path ) {
        if( path == null ) PATHS_TO_RENDER.remove( entityId );
        else PATHS_TO_RENDER.put( entityId, path );
    }
    
    
    // Utility class
    private NavigationPathRenderer() { }
}
