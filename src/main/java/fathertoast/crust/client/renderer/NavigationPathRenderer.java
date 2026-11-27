package fathertoast.crust.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a render listener that keeps track of pathfinding mobs that should have their
 * current navigation path visualized and renders them in the world, right after blocks.
 * <br><br>
 * Pathfinding data does not normally exist on the client
 * in vanilla Minecraft, so Crust manually sends this data when needed.
 *
 * @see fathertoast.crust.common.core.CrustForgeEvents#onServerTick(TickEvent.ServerTickEvent)
 */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class NavigationPathRenderer {
    
    /** A thread-safe map containing numeric entity level IDs mapped to a path. */
    private static final Map<Integer, Path> PATHS_TO_RENDER = new ConcurrentHashMap<>();
    
    /** The counter for periodically removing path entries that are out of range or no longer valid. */
    private static int cleanupCounter;
    
    
    /** Called when the player respawns and a new player entity is created to replace the old one. */
    @SuppressWarnings( "resource" )
    @SubscribeEvent
    public static void onPlayerEntityCloned( ClientPlayerNetworkEvent.Clone event ) {
        // Clear all entries if the player respawns in a different dimension
        if( event.getOldPlayer().level().dimension() != event.getNewPlayer().level().dimension() ) {
            PATHS_TO_RENDER.clear();
        }
    }
    
    /** Called every tick on the logical client. */
    @SubscribeEvent
    public static void onClientTick( TickEvent.ClientTickEvent event ) {
        // Check if pathfinding visualization is enabled
        if( !ClientRegister.RENDER_SETTINGS.renderPth.get() ) return;
        
        if( event.phase == TickEvent.Phase.END ) {
            // Check that the client level exists
            if( Minecraft.getInstance().level == null ) return;
            if( cleanupCounter++ >= 60 ) {
                // Remove entries whose associated entity has despawned
                PATHS_TO_RENDER.keySet().removeIf( ( id ) -> Minecraft.getInstance().level.getEntity( id ) == null );
            }
        }
    }
    
    /** Called at various stages during level rendering. */
    @SuppressWarnings( "ConstantConditions" )
    @SubscribeEvent
    public static void onRenderLevelStage( RenderLevelStageEvent event ) {
        if( event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS ) {
            // Check if pathfinding visualization is enabled
            if( !ClientRegister.RENDER_SETTINGS.renderPth.get() ) return;
            
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
                if( entity != null && dist <= ClientRegister.RENDER_SETTINGS.renderPthDistance.get() ) {
                    renderPath( entry.getValue(), cameraPos, poseStack, bufferSource );
                }
            }
        }
    }
    
    /**
     * Renders the given path.
     *
     * @param path      The navigation path to render.
     * @param cameraPos The current position of the camera.
     */
    private static void renderPath( Path path, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource ) {
        // Draw lines between each node/path segment, if enabled
        if( ClientRegister.RENDER_SETTINGS.renderPthShowLine.get() ) {
            drawPathLine( poseStack, bufferSource.getBuffer( RenderType.debugLineStrip( 10.0D ) ), path, cameraPos );
        }
        
        // Display path target
        final BlockPos target = path.getTarget();
        final int targetColor = path.canReach() ? ClientRegister.RENDER_SETTINGS.renderPthTargetReachedColor.get() : ClientRegister.RENDER_SETTINGS.renderPthTargetNotReachedColor.get();
        drawColoredBox( target, cameraPos, poseStack, bufferSource, targetColor,
                0.25, 0.25, 0.25, 0.75, 0.75, 0.75 );
        
        // Draw nodes
        for( int i = 0; i < path.getNodeCount(); ++i ) {
            Node node = path.getNode( i );
            int color = i == path.getNextNodeIndex() ? ClientRegister.RENDER_SETTINGS.renderPthNextNodeColor.get() : ClientRegister.RENDER_SETTINGS.renderPthNodeColor.get();
            drawColoredBox( node.x, node.y, node.z, cameraPos, poseStack, bufferSource, color,
                    0.0, 0.0, 0.0, 1.0, 0.2, 1.0 );
        }
        
        // Display details, if enabled
        final DetailMode mode = ClientRegister.RENDER_SETTINGS.renderPthDetailMode.get();
        final LocalPlayer player = Minecraft.getInstance().player;
        if( mode == DetailMode.ON || (mode == DetailMode.WHEN_SNEAKING && player != null && player.isShiftKeyDown()) ) {
            for( int i = 0; i < path.getNodeCount(); ++i ) {
                Node node = path.getNode( i );
                // Path type
                DebugRenderer.renderFloatingText( poseStack, bufferSource, node.type.toString(), (double) node.x + 0.5D, (double) node.y + 0.75D, (double) node.z + 0.5D, -1, 0.02F, true, 0.0F, true );
                // Malus
                DebugRenderer.renderFloatingText( poseStack, bufferSource, String.format( Locale.ROOT, "%.2f", node.costMalus ), (double) node.x + 0.5D, (double) node.y + 0.25D, (double) node.z + 0.5D, -1, 0.02F, true, 0.0F, true );
            }
        }
    }
    
    /**
     * Draws a line between each node in a path where the
     * color gradually shifts from the starting node towards the end node.
     */
    private static void drawPathLine( PoseStack poseStack, VertexConsumer buffer, Path path, Vec3 cameraPos ) {
        for( int i = 0; i < path.getNodeCount(); ++i ) {
            Node node = path.getNode( i );
            float hue = (float) i / (float) path.getNodeCount() * 0.33F;
            int color = i == 0 ? 0 : Mth.hsvToRgb( hue, 0.9F, 0.9F );
            buffer.vertex( poseStack.last().pose(),
                            (float) (node.x - cameraPos.x + 0.5D),
                            (float) (node.y - cameraPos.y + 0.5D),
                            (float) (node.z - cameraPos.z + 0.5D) )
                    .color( CrustMath.getRed( color ), CrustMath.getGreen( color ), CrustMath.getBlue( color ), 255 ).endVertex();
        }
    }
    
    /**
     * Helper method for drawing a colored box at the given position.
     *
     * @param x         The X position to draw the box at.
     * @param y         The Y position to draw the box at.
     * @param z         The Z position to draw the box at.
     * @param cameraPos The current position of the camera.
     * @param color     The color of the box.
     */
    private static void drawColoredBox( double x, double y, double z, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource, int color,
                                        double minX, double minY, double minZ, double maxX, double maxY, double maxZ ) {
        final AABB box = new AABB(
                x + minX,
                y + minY,
                z + minZ,
                x + maxX,
                y + maxY,
                z + maxZ
        ).move( -cameraPos.x, -cameraPos.y, -cameraPos.z );
        // Get RGBA from color int
        float r = CrustMath.getRed( color );
        float g = CrustMath.getGreen( color );
        float b = CrustMath.getBlue( color );
        float a = CrustMath.getAlpha( color );
        DebugRenderer.renderFilledBox( poseStack, bufferSource, box, r, g, b, a );
    }
    
    /**
     * Helper method for drawing a colored box at the given position.
     *
     * @param pos       The block position to draw the box at.
     * @param cameraPos The current position of the camera.
     * @param color     The color of the box.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static void drawColoredBox( BlockPos pos, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource, int color,
                                        double minX, double minY, double minZ, double maxX, double maxY, double maxZ ) {
        drawColoredBox( pos.getX(), pos.getY(), pos.getZ(), cameraPos, poseStack, bufferSource, color,
                minX, minY, minZ, maxX, maxY, maxZ );
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
    
    
    /** Represents the options for when to display path details. */
    public enum DetailMode {
        /** Don't show details. */
        OFF,
        /** Always show details. */
        ON,
        /** Only show details when the player is sneaking. */
        WHEN_SNEAKING
    }
    
    
    // Utility class
    private NavigationPathRenderer() { }
}
