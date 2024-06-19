package fathertoast.crust.api.config.client.gui.screen;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFileList;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigModList;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
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
public class CrustConfigSelectScreen extends Screen {
    
    /** @return The mod's display name. */
    public static String getModName( String modId ) {
        ModContainer modContainer = ModList.get().getModContainerById( modId ).orElse( null );
        return modContainer == null ? modId : modContainer.getModInfo().getDisplayName();
    }
    
    
    /** The screen open under this one. */
    private final Screen LAST_SCREEN;
    
    /** The config manager for the selected mod, or null if none is selected. */
    private final ConfigManager CFG_MANAGER;
    
    /** The text to render below the title. */
    private final Component SUBTITLE;
    
    private AbstractSelectionList<?> selectionList;
    
    /** Creates a new config selection screen, opened to the mod select page. */
    public CrustConfigSelectScreen( @Nullable Screen parent ) {
        this( parent, null, Component.translatable( "menu.crust.config.select.mod.title" ), null );
    }
    
    /** Creates a new config selection screen, opened directly to mod's file select page. */
    public CrustConfigSelectScreen( @Nullable Screen parent, ConfigManager cfgManager ) {
        this( parent, cfgManager,
                Component.translatable( "menu.crust.config.select.file.title",
                        getModName( cfgManager.MOD_ID ) ),
                Component.translatable( "menu.crust.config.select.file.subtitle",
                        ConfigUtil.toRelativePath( cfgManager.DIR ) ) );
    }
    
    /** Creates a new config selection screen, optionally opened directly to a specific mod's page. */
    private CrustConfigSelectScreen( @Nullable Screen parent, @Nullable ConfigManager cfgManager, Component title, @Nullable Component subtitle ) {
        super( title );
        LAST_SCREEN = parent;
        CFG_MANAGER = cfgManager;
        SUBTITLE = subtitle;
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() { if( minecraft != null ) minecraft.setScreen( LAST_SCREEN ); }
    
    /** Called to setup the screen before displaying it. */
    @Override
    protected void init() {
        if( minecraft == null ) return;
        
        // Header content
        // Nothing to init
        
        // Primary screen content
        if( CFG_MANAGER == null ) {
            selectionList = new CrustConfigModList( this, minecraft );
        }
        else {
            selectionList = new CrustConfigFileList( this, minecraft, CFG_MANAGER );
        }
        addRenderableWidget( selectionList );
        
        // Footer content
        addRenderableWidget( new Button( width / 2 - 155, height - 29,
                150, 20, Component.translatable( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( CFG_MANAGER == null ) Util.getPlatform().openFile( FMLPaths.CONFIGDIR.get().toFile() );
                    else Util.getPlatform().openFile( CFG_MANAGER.DIR );
                },
                Supplier::get) );
        addRenderableWidget( new Button( width / 2 - 155 + 160, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                ( button ) -> minecraft.setScreen( LAST_SCREEN ), Supplier::get ) );
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        
        selectionList.render( graphics, mouseX, mouseY, partialTicks );
        
        if( SUBTITLE != null ) {
            graphics.drawCenteredString( font, SUBTITLE, width / 2,
                    24, 0x777777 );
        }
        graphics.drawCenteredString( font, title, width / 2,
                8, 0xFFFFFF );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
}