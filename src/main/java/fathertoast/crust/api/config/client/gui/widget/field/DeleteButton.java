package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

/**
 * The 'delete button' displayed to the right of each entry in a popup list editor widget in the in-game
 * config editor. Actual delete logic is handled in the list widget that creates these buttons.
 */
public class DeleteButton extends SimpleTextureButton {
    
    private static final ResourceLocation DELETE_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/delete_button.png" );
    
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    
    
    public DeleteButton( int x, int y, Button.OnPress onPress ) {
        super( x, y, WIDTH, HEIGHT, null, DELETE_BUTTON_TEXTURE, onPress );
    }
}