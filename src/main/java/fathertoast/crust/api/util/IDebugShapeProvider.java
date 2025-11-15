package fathertoast.crust.api.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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