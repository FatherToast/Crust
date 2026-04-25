package fathertoast.crust.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This interface can be implemented into objects that have additional info that should
 * be rendered in debug mode (when entity hitboxes are rendered F3+B).
 * <p>
 * Crust automatically handles debug shape rendering for any block entities that
 * implement this interface.
 */
public interface IDebugShapeProvider {
    
    /**
     * @return A List of debug shapes that should be rendered in the world.
     * The list may be null, but do NOT include any null entries in the list.
     */
    @Nullable
    List<IDebugShape> getDebugShapes();
    
    /**
     * By default, the shapes returned by a shape provider will be centered relative to the implementing block or entity's
     * in-world position before rendering. For entities, the position used is {@link Entity#position()}, and for block entities it is
     * the center of {@link BlockEntity#getBlockPos()}.
     * <br><br>
     * If this is overridden to return false instead, each shape's {@link fathertoast.crust.api.client.util.shape.IDebugShapeRenderer}
     * will call their render method with a null vec3 position.
     *
     * @see fathertoast.crust.api.client.util.shape.IDebugShapeRenderer#renderShape(IDebugShape, Vec3, PoseStack, Matrix4f)
     */
    default boolean useWorldPosition() {
        return true;
    }
    
    // ---- Convenience Methods/Objects ---- //
    
    /** An empty list of shapes you can return if you don't have any shapes to draw at the moment. */
    List<IDebugShape> NO_SHAPES = Collections.emptyList();
    
    /** @return A List of debug shapes with the default color (green), converted from bounding boxes. */
    static List<IDebugShape> fromBBs( @Nullable AABB... boxes ) {
        return fromBBs( 0x00FF00, boxes );
    }
    
    /** @return A List of debug shapes, converted from bounding boxes. */
    static List<IDebugShape> fromBBs( int color, @Nullable AABB... boxes ) {
        if( boxes == null ) return new ArrayList<>(); // Just make a new list so more shapes can be added afterward
        List<IDebugShape> shapes = new ArrayList<>( boxes.length );
        Vec3 offset = null;
        for( AABB box : boxes ) {
            if( offset == null ) offset = box.getCenter().scale( -1 );
            shapes.add( new BoxShape( box.move( offset ) ).withColor( color ) );
        }
        return shapes;
    }
    
    /** @return A List of debug shapes with the default color (green), converted from bounding boxes. */
    static List<IDebugShape> fromBBs( @Nullable Iterable<AABB> boxes ) {
        return fromBBs( 0x00FF00, boxes );
    }
    
    /** @return A List of debug shapes, converted from bounding boxes. */
    static List<IDebugShape> fromBBs( int color, @Nullable Iterable<AABB> boxes ) {
        if( boxes == null ) return new ArrayList<>(); // Just make a new list so more shapes can be added afterward
        List<IDebugShape> shapes = new ArrayList<>();
        Vec3 offset = null;
        for( AABB box : boxes ) {
            if( offset == null ) offset = box.getCenter().scale( -1 );
            shapes.add( new BoxShape( box.move( offset ) ).withColor( color ) );
        }
        return shapes;
    }
}