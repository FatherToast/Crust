package fathertoast.crust.api.config.common.value;

/**
 * A simple implementation of an 'anchor point' for use in configs.
 */
@SuppressWarnings( "unused" )
public enum CrustAnchor {
    CENTER, SCREEN_LEFT, LEFT, SCREEN_RIGHT, RIGHT, SCREEN_TOP, TOP, SCREEN_BOTTOM, BOTTOM;
    
    // General-purpose anchors
    public static final CrustAnchor[] VERTICAL = { TOP, CENTER, BOTTOM };
    public static final CrustAnchor[] HORIZONTAL = { LEFT, CENTER, RIGHT };
    
    // GUI screen anchors
    public static final CrustAnchor[] VERTICAL_GUI = { SCREEN_TOP, TOP, CENTER, BOTTOM, SCREEN_BOTTOM };
    public static final CrustAnchor[] HORIZONTAL_GUI = { SCREEN_LEFT, LEFT, CENTER, RIGHT, SCREEN_RIGHT };
    
    /**
     * Anchor to a region. This snaps the anchored object to the inside edge of the region.
     *
     * @param regionSize Size of the anchor region.
     * @param size       Size of the anchored object.
     * @return The anchored position of the object's top-left corner.
     */
    public int pos( int regionSize, int size ) {
        switch( this ) {
            case TOP: case LEFT: case SCREEN_TOP: case SCREEN_LEFT:
                return 0;
            case BOTTOM: case RIGHT: case SCREEN_BOTTOM: case SCREEN_RIGHT:
                return regionSize - size;
            default:
                return (regionSize - size) / 2;
        }
    }
    
    /**
     * Anchor to a centered GUI window.
     * This snaps the anchored element to the inside edge of the screen or the outside edge of the window.
     *
     * @param screenSize X- or Y-size of the entire game screen.
     * @param guiSize    X- or Y-size of the active GUI window.
     * @param size       X- or Y-size of the anchored GUI element.
     * @return The anchored position of the object's top-left corner.
     * @see #pos(int, int, int, int) Anchoring to an off-center GUI window.
     */
    public int pos( int screenSize, int guiSize, int size ) {
        switch( this ) {
            case SCREEN_TOP: case SCREEN_LEFT:
                return 0;
            case SCREEN_BOTTOM: case SCREEN_RIGHT:
                return screenSize - size;
            case TOP: case LEFT:
                return (screenSize - guiSize) / 2 - size;
            case BOTTOM: case RIGHT:
                return (screenSize + guiSize) / 2;
            default:
                return (screenSize - size) / 2;
        }
    }
    
    /**
     * Anchor to an off-center GUI window.
     * This snaps the anchored element to the inside edge of the screen or the outside edge of the window.
     *
     * @param screenSize X- or Y-size of the entire game screen.
     * @param guiSize    X- or Y-size of the active GUI window.
     * @param guiPos     X- or Y-position of the active GUI window's top-left corner.
     * @param size       X- or Y-size of the anchored GUI element.
     * @return The anchored position of the object's top-left corner.
     * @see #pos(int, int, int) Anchoring to a centered GUI window.
     */
    public int pos( int screenSize, int guiSize, int guiPos, int size ) {
        switch( this ) {
            case SCREEN_TOP: case SCREEN_LEFT:
                return 0;
            case SCREEN_BOTTOM: case SCREEN_RIGHT:
                return screenSize - size;
            case TOP: case LEFT:
                return guiPos - size;
            case BOTTOM: case RIGHT:
                return guiPos + guiSize;
            default:
                return guiPos + (guiSize - size) / 2;
        }
    }
}