package fathertoast.crust.api.config.client.gui;

/**
 * Contains x, y, width and height offsets to be applied
 * when creating or drawing widgets or other graphical components on a screen.
 */
public record ElementOffset(int xOffset, int yOffset, int widthOffset, int heightOffset) {
    
    /** Default highlight offsets instance with no offsets. */
    public static final ElementOffset NONE = new ElementOffset( 0, 0, 0, 0 );
    
    
    public int getX() { return xOffset; }
    
    public int getY() { return yOffset; }
    
    public int getWidth() { return widthOffset; }
    
    public int getHeight() { return heightOffset; }
}
