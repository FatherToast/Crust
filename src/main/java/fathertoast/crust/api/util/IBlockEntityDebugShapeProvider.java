package fathertoast.crust.api.util;

/**
 * This interface can be implemented into a block entity
 * that has bounding boxes that should be rendered in
 * debug mode (when entity hitboxes are rendered).
 * <p>
 * Will be removed beyond MC 1.20, use {@link IDebugShapeProvider} instead.
 */
@Deprecated( forRemoval = true )
public interface IBlockEntityDebugShapeProvider extends IDebugShapeProvider { }