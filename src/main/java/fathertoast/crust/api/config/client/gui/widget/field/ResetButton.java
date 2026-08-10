package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.ICrustApi;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

/**
 * The 'reset button' displayed to the right of each config field in the in-game config editor.
 * Actual reset logic is handled in the config field list that creates these buttons.
 */
public class ResetButton extends SimpleTextureButton {
    
    private static final ResourceLocation RESET_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/reset_button.png" );
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    
    
    public ResetButton( int x, int y, Button.OnPress onPress ) {
        super( x, y, WIDTH, HEIGHT, null, RESET_BUTTON_TEXTURE, onPress );
    }
}