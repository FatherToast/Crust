package fathertoast.crust.client.screen;

import fathertoast.crust.api.config.client.gui.widget.field.TextWithSubtitle;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Screen that displays available config files. Navigation starts by selecting the mod,
 * then all that mod's configs are displayed in a list, grouped by folder location.
 * Files in the config manager's root are at the top of the list, then ordered depth-first.
 * <p>
 * The screen may also be opened directly to a specific mod's configs; for example,
 * when opened from the mod list "config" button.
 *
 * @see net.minecraft.client.gui.screens.controls.ControlsScreen
 * @see ConfigManager
 */
public class CrustConfigFetchScreen extends Screen {
    
    /** The screen open under this one. */
    private final Screen LAST_SCREEN;
    
    /** The spec of the 'opened' config file. */
    public final CrustConfigSpec SPEC;
    
    /** The subtitle to in a tooltip for the title. */
    private final Component SUBTITLE;
    
    /** True if this config is editable. */
    private final boolean EDITABLE;
    
    /** Creates a new config selection screen, opened to the mod select page. */
    public CrustConfigFetchScreen( Screen parent, CrustConfigSpec spec, boolean editable ) {
        super( Component.translatable( "menu.crust.config.file.title",
                ConfigUtil.getModName( spec.MANAGER.MOD_ID ),
                ConfigUtil.getSpecName( spec ) ) );
        LAST_SCREEN = parent;
        SPEC = spec;
        SUBTITLE = Component.translatable( "menu.crust.config.file.subtitle",
                ConfigUtil.toRelativePath( spec.getFile() ) ).withStyle( ChatFormatting.DARK_GRAY );
        EDITABLE = editable;
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() { if( minecraft != null ) minecraft.setScreen( LAST_SCREEN ); }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        if( minecraft == null ) return;
        
        // Header content
        addRenderableWidget( TextWithSubtitle.create( this, font, width / 2, 8, true, getTitle(), SUBTITLE ) );
        
        // Primary screen content
        //TODO maybe display some text that says it's waiting for the data from server
        
        // Footer content
        addRenderableWidget( new Button( width / 2 - 155, height - 29,
                150, 20, Component.translatable( "menu.crust.config.open_folder" ),
                button -> Util.getPlatform().openFile( SPEC.getFile().getParentFile() ),
                Supplier::get ) );
        addRenderableWidget( new Button( width / 2 - 155 + 160, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                button -> minecraft.setScreen( LAST_SCREEN ),
                Supplier::get ) );
        
        CrustPacketHandler.sendConfigDataRequest( SPEC );
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
    
    /** Called when the requested data packet is received. The server doesn't send it if you don't have view permission. */
    public void receivedData() {
        if( minecraft != null ) {
            minecraft.setScreen( new CrustConfigFileScreen( LAST_SCREEN, SPEC, true, EDITABLE ) );
        }
    }
}