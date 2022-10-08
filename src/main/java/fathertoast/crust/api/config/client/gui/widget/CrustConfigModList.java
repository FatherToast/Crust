package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Widget that displays a sorted, scrollable list of all mods that use Crust configs.
 */
public class CrustConfigModList extends AbstractOptionList<CrustConfigModList.Entry> {
    
    private int maxNameWidth;
    
    public CrustConfigModList( Screen parent, Minecraft game ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 20 );
        // Gather all mod config managers and sort
        ArrayList<ConfigManager> cfgManagers = new ArrayList<>( ConfigManager.getAll() );
        cfgManagers.sort( Comparator.comparing( ( cfgManager ) -> cfgManager.MOD_ID ) );
        
        // Populate the list contents
        for( ConfigManager cfgManager : cfgManagers ) {
            ITextComponent name = new StringTextComponent( CrustConfigSelectScreen.getModName( cfgManager.MOD_ID ) +
                    " (modid:" + cfgManager.MOD_ID + ")" );
            int nameWidth = game.font.width( name );
            if( nameWidth > maxNameWidth ) maxNameWidth = nameWidth;
            
            addEntry( new Entry( this, cfgManager, name ) );
        }
    }
    
    /** A mod display row for mod selection lists. */
    public static class Entry extends AbstractOptionList.Entry<CrustConfigModList.Entry> {
        
        private final CrustConfigModList PARENT;
        private final ConfigManager CFG_MANAGER;
        private final ITextComponent NAME;
        private final Button MOD_BUTTON;
        
        private Entry( CrustConfigModList parent, ConfigManager cfgManager, ITextComponent name ) {
            PARENT = parent;
            CFG_MANAGER = cfgManager;
            NAME = name;
            //noinspection ConstantConditions
            MOD_BUTTON = new Button( 0, 0, 20, 20,
                    new StringTextComponent( ">" ),
                    ( button ) -> PARENT.minecraft.setScreen(
                            new CrustConfigSelectScreen( PARENT.minecraft.screen, CFG_MANAGER ) ) );
        }
        
        @Override
        public void render( MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            PARENT.minecraft.font.draw( matrixStack, NAME,
                    PARENT.minecraft.screen.width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + (rowHeight - 9 >> 1), 0xFFFFFF );
            
            MOD_BUTTON.x = (PARENT.minecraft.screen.width + PARENT.maxNameWidth + 30 >> 1) - 20;
            MOD_BUTTON.y = rowTop;
            MOD_BUTTON.render( matrixStack, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends IGuiEventListener> children() { return ImmutableList.of( MOD_BUTTON ); }
        
        @Override
        public boolean mouseClicked( double x, double y, int mouseKey ) {
            return MOD_BUTTON.mouseClicked( x, y, mouseKey );
        }
        
        @Override
        public boolean mouseReleased( double x, double y, int mouseKey ) {
            return MOD_BUTTON.mouseReleased( x, y, mouseKey );
        }
    }
}