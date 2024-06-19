package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Widget that displays a sorted, scrollable list of all mods that use Crust configs.
 */
public class CrustConfigModList extends ContainerObjectSelectionList<CrustConfigModList.Entry> {

    private int maxNameWidth;
    
    public CrustConfigModList(Screen parent, Minecraft game ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 20 );
        // Gather all mod config managers and sort
        ArrayList<ConfigManager> cfgManagers = new ArrayList<>( ConfigManager.getAll() );
        cfgManagers.sort( Comparator.comparing( ( cfgManager ) -> cfgManager.MOD_ID ) );
        
        // Populate the list contents
        for( ConfigManager cfgManager : cfgManagers ) {
            Component name = Component.literal( CrustConfigSelectScreen.getModName( cfgManager.MOD_ID ) +
                    " (modid:" + cfgManager.MOD_ID + ")" );
            int nameWidth = game.font.width( name );
            if( nameWidth > maxNameWidth ) maxNameWidth = nameWidth;
            
            addEntry( new Entry( this, cfgManager, name ) );
        }
    }
    
    /** A mod display row for mod selection lists. */
    public static class Entry extends ContainerObjectSelectionList.Entry<CrustConfigModList.Entry> {
        
        private final CrustConfigModList PARENT;
        private final ConfigManager CFG_MANAGER;
        private final Component NAME;
        private final Button MOD_BUTTON;
        
        private Entry( CrustConfigModList parent, ConfigManager cfgManager, Component name ) {
            PARENT = parent;
            CFG_MANAGER = cfgManager;
            NAME = name;
            //noinspection ConstantConditions
            MOD_BUTTON = new Button( 0, 0, 20, 20,
                    Component.literal( ">" ),
                    ( button ) -> PARENT.minecraft.setScreen(
                            new CrustConfigSelectScreen( PARENT.minecraft.screen, CFG_MANAGER ) ), Supplier::get );
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            graphics.drawString( PARENT.minecraft.font, NAME,
                    PARENT.minecraft.screen.width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + (rowHeight - 9 >> 1), 0xFFFFFF );
            
            MOD_BUTTON.setX( (PARENT.minecraft.screen.width + PARENT.maxNameWidth + 30 >> 1) - 20 );
            MOD_BUTTON.setY( rowTop );
            MOD_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends GuiEventListener> children() { return ImmutableList.of( MOD_BUTTON ); }
        
        @Override
        public boolean mouseClicked( double x, double y, int mouseKey ) {
            return MOD_BUTTON.mouseClicked( x, y, mouseKey );
        }

        @Override
        public boolean mouseReleased( double x, double y, int mouseKey ) {
            return MOD_BUTTON.mouseReleased( x, y, mouseKey );
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }
}