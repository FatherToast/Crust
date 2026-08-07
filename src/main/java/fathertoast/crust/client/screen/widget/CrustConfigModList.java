package fathertoast.crust.client.screen.widget;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.client.screen.CrustConfigSelectScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
public class CrustConfigModList extends SearchableSelectionList<CrustConfigModList.Entry> {
    
    private int maxNameWidth;
    
    public CrustConfigModList( Screen parent, Minecraft game ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 20 );
        // Gather all mod config managers and sort
        ArrayList<ConfigManager> cfgManagers = new ArrayList<>( ConfigManager.getAll() );
        cfgManagers.sort( Comparator.comparing( ( cfgManager ) -> cfgManager.MOD_ID ) );
        
        // Populate the list contents
        for( ConfigManager cfgManager : cfgManagers ) {
            Component name = Component.literal( ConfigUtil.getModName( cfgManager.MOD_ID ) +
                    ChatFormatting.DARK_GRAY + " (modid:" + cfgManager.MOD_ID + ")" );
            int nameWidth = game.font.width( name );
            if( nameWidth > maxNameWidth ) maxNameWidth = nameWidth;
            
            addEntry( new Entry( this, cfgManager, name ) );
        }
    }
    
    /** A mod display row for mod selection lists. */
    public static class Entry extends ContainerObjectSelectionList.Entry<CrustConfigModList.Entry> implements ISearchable {
        
        private final CrustConfigModList PARENT;
        private final ConfigManager CFG_MANAGER;
        private final Component NAME;
        private final Button MOD_BUTTON;
        
        private Entry( CrustConfigModList parent, ConfigManager cfgManager, Component name ) {
            PARENT = parent;
            CFG_MANAGER = cfgManager;
            NAME = name;
            MOD_BUTTON = new Button( 0, 0, 20, 20,
                    Component.literal( ">" ),
                    ( button ) -> PARENT.minecraft.setScreen(
                            new CrustConfigSelectScreen( PARENT.minecraft.screen, CFG_MANAGER ) ), Supplier::get );
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            graphics.drawString( PARENT.minecraft.font, NAME,
                    PARENT.minecraft.screen.width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + 6, 0xFFFFFF );
            
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
        
        /** @return Narrations for this entry. */
        @Override
        public List<? extends NarratableEntry> narratables() { return List.of(); }
        
        /** @return An identifying String to be looked up by a {@link Searchbar} */
        @Override // ISearchable
        public String getLookupName() { return NAME.getString(); }
    }
}