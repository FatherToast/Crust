package fathertoast.crust.api.config.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigFileList;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigModList;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;

/**
 * Screen that displays available config files. Navigation starts by selecting the mod,
 * then all that mod's configs are displayed in a list, grouped by folder location.
 * Files in the config manager's root are at the top of the list, then ordered depth-first.
 * <p>
 * The screen may also be opened directly to a specific mod's configs; for example,
 * when opened from the mod list "config" button.
 *
 * @see net.minecraft.client.gui.screen.ControlsScreen
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
    private final ITextComponent SUBTITLE;
    
    private AbstractOptionList<?> selectionList;
    
    /** Creates a new config selection screen, opened to the mod select page. */
    public CrustConfigSelectScreen( Screen parent ) {
        this( parent, null, new TranslationTextComponent( "menu.crust.config.select.mod.title" ), null );
    }
    
    /** Creates a new config selection screen, opened directly to mod's file select page. */
    public CrustConfigSelectScreen( Screen parent, ConfigManager cfgManager ) {
        this( parent, cfgManager,
                new TranslationTextComponent( "menu.crust.config.select.file.title",
                        getModName( cfgManager.MOD_ID ) ),
                new TranslationTextComponent( "menu.crust.config.select.file.subtitle",
                        ConfigUtil.toRelativePath( cfgManager.DIR ) ) );
    }
    
    /** Creates a new config selection screen, optionally opened directly to a specific mod's page. */
    private CrustConfigSelectScreen( Screen parent, @Nullable ConfigManager cfgManager, ITextComponent title, @Nullable ITextComponent subtitle ) {
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
        children.add( selectionList );
        
        // Footer content
        addButton( new Button( width / 2 - 155, height - 29,
                150, 20, new TranslationTextComponent( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( CFG_MANAGER == null ) Util.getPlatform().openFile( FMLPaths.CONFIGDIR.get().toFile() );
                    else Util.getPlatform().openFile( CFG_MANAGER.DIR );
                } ) );
        addButton( new Button( width / 2 - 155 + 160, height - 29,
                150, 20, DialogTexts.GUI_DONE,
                ( button ) -> minecraft.setScreen( LAST_SCREEN ) ) );
    }
    
    /** Called to render the screen. */
    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( matrixStack );
        
        selectionList.render( matrixStack, mouseX, mouseY, partialTicks );
        
        if( SUBTITLE != null ) {
            drawCenteredString( matrixStack, font, SUBTITLE, width / 2,
                    24, 0x777777 );
        }
        drawCenteredString( matrixStack, font, title, width / 2,
                8, 0xFFFFFF );
        
        super.render( matrixStack, mouseX, mouseY, partialTicks );
    }
}