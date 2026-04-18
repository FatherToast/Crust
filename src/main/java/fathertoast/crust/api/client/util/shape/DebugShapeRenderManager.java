package fathertoast.crust.api.client.util.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.shape.NullShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Handles the registration of debug shapes and their rendering.
 * <p>
 * You can register any custom shape renderers pretty much whenever
 * you want, but it is recommended to register them during
 * {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
 */
public final class DebugShapeRenderManager {
    
    /**
     * Registers a shape's factory with its renderer. The factory must return a non-null shape with a unique
     * and consistent result from its {@link IDebugShape#getId()} method.
     * <p>
     * You may register the same renderer to multiple shape factories.
     * <p>
     * The factory may simply return a 'singleton' instance. If using a singleton, ensure the shape's
     * {@link IDebugShape#deserialize(CompoundTag)} method always sets all fields used for rendering.
     *
     * @param shapeFactory  A factory that can create new instances for deserialization on the client.
     * @param shapeRenderer The shape's renderer.
     */
    public static <S extends IDebugShape, F extends S> void register( Supplier<F> shapeFactory, IDebugShapeRenderer<S> shapeRenderer ) {
        final F shape = shapeFactory.get();
        final ResourceLocation id = shape.getId();
        if( REGISTER.containsKey( id ) ) {
            throw new IllegalStateException( "Cannot register multiple renderers to the same id! Duplicate id: " + id );
        }
        REGISTER.put( id, new Holder<>( shapeFactory, shapeRenderer ) );
    }
    
    
    // ---- Shape Rendering ---- //
    
    public static void renderShape( CompoundTag shapeTag, RenderLevelStageEvent event ) {
        renderShape( shapeTag, event.getPoseStack(), event.getProjectionMatrix() );
    }
    
    public static void renderShape( CompoundTag shapeTag, PoseStack poseStack, Matrix4f projectionMatrix ) {
        synchronized( DebugShapeRenderManager.class ) {
            renderShape( fromNBT( shapeTag ), null, poseStack, projectionMatrix );
        }
    }
    
    public static void renderShape( IDebugShape shape, @Nullable Vec3 pos, RenderLevelStageEvent event ) {
        renderShape( shape, pos, event.getPoseStack(), event.getProjectionMatrix() );
    }
    
    public static void renderShape( IDebugShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
        final Holder<?, ?> holder = REGISTER.get( shape.getId() );
        if( holder == null ) {
            LOG.warn( "Tried rendering unregistered shape with id \"{}\"! Shape: {}", shape.getId(), shape );
            return;
        }
        holder.renderSuppliedShape( shape, pos, poseStack, projectionMatrix );
    }
    
    /** @return True if the client has hit box rendering mode active (F3+B). */
    public static boolean shouldRenderHitBoxes() {
        return Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() &&
                !Minecraft.getInstance().showOnlyReducedInfo();
    }
    
    /** @return The vertex consumer for line rendering. */
    public static VertexConsumer getLinesBuffer() { return getBuffer( RenderType.lines() ); }
    
    /** @return The vertex consumer for line strip rendering. */
    public static VertexConsumer getLineStripBuffer() { return getBuffer( RenderType.lineStrip() ); }
    
    /** @return The vertex consumer for a particular render type. */
    public static VertexConsumer getBuffer( RenderType renderType ) {
        return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer( renderType );
    }
    
    
    // ---- Shape Serialization ---- //
    
    /** The NBT tag that the shape id is saved to. */
    public static String TAG_ID = "id";
    
    /** Loads a shape from nbt. Called to deserialize shapes received from the server. */
    public static IDebugShape fromNBT( CompoundTag shapeTag ) {
        if( !NBTHelper.containsString( shapeTag, TAG_ID ) ) {
            LOG.warn( "Failed to deserialize shape! Missing id" );
        }
        else {
            final ResourceLocation id = ResourceLocation.tryParse( shapeTag.getString( TAG_ID ) );
            if( id == null ) {
                LOG.warn( "Failed to deserialize shape! Invalid id: \"{}\"", shapeTag.getString( TAG_ID ) );
            }
            else {
                final Holder<?, ?> holder = REGISTER.get( id );
                if( holder == null ) {
                    LOG.warn( "Failed to deserialize shape! Unregistered id: \"{}\"", id );
                }
                else {
                    final IDebugShape shape = holder.shapeFactory.get();
                    shape.deserialize( shapeTag );
                    return shape;
                }
            }
        }
        return NullShape.getInstance();
    }
    
    /** Writes a shape to nbt. Called to serialize shapes to send to clients. */
    public static CompoundTag toNBT( IDebugShape shape ) {
        final CompoundTag shapeTag = new CompoundTag();
        shape.serialize( shapeTag );
        shapeTag.putString( TAG_ID, shape.getId().toString() );
        return shapeTag;
    }
    
    
    // ---- Internal Methods ---- //
    
    /** Logger instance for the Crust debug shape renderer. */
    private static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID + "/shapes" );
    
    /** Holds all registered factories and renderers. */
    private static final Map<ResourceLocation, Holder<?, ?>> REGISTER = new ConcurrentHashMap<>();
    
    static {
        register( NullShape::getInstance, NullShapeRenderer.getInstance() );
    }
    
    private record Holder<S extends IDebugShape, F extends S>
            (Supplier<F> shapeFactory, IDebugShapeRenderer<S> shapeRenderer) {
        
        void renderSuppliedShape( IDebugShape shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
            //noinspection unchecked
            renderShape( (S) shape, pos, poseStack, projectionMatrix );
        }
        
        void renderShape( @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
            final F shape = shapeFactory.get();
            renderShape( shape, pos, poseStack, projectionMatrix );
        }
        
        void renderShape( S shape, @Nullable Vec3 pos, PoseStack poseStack, Matrix4f projectionMatrix ) {
            shapeRenderer.renderShape( shape, pos, poseStack, projectionMatrix );
        }
    }
    
    // Utility class
    private DebugShapeRenderManager() { }
}