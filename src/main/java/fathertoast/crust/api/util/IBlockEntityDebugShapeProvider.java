package fathertoast.crust.api.util;

import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface can be implemented into a BlockEntity
 * that has additional info that should be rendered in
 * debug mode (when entity hitboxes are rendered F3+B).
 */
public interface IBlockEntityDebugShapeProvider {
    
    /** @return A List of debug shapes, converted from bounding boxes. */
    @Nullable
    static List<IDebugShape> fromBBs( @Nullable AABB... boxes ) {
        if( boxes == null ) return null;
        List<IDebugShape> shapes = new ArrayList<>( boxes.length );
        for( AABB box : boxes ) shapes.add( new BoxShape( box ) );
        return shapes;
    }
    
    /** @return A List of debug shapes, converted from bounding boxes. */
    @Nullable
    static List<IDebugShape> fromBBs( @Nullable Iterable<AABB> boxes ) {
        if( boxes == null ) return null;
        List<IDebugShape> shapes = new ArrayList<>();
        boxes.forEach( ( box ) -> shapes.add( new BoxShape( box ) ) );
        return shapes;
    }
    
    /**
     * @return A List of debug shapes that should be rendered in the world.
     * The provided shapes will be rendered right before block entities. Do NOT include any null entries in the list.
     */
    @Nullable
    List<IDebugShape> getDebugShapes();
}