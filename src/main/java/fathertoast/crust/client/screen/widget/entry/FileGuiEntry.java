package fathertoast.crust.client.screen.widget.entry;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.client.util.GuiUtil;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.client.screen.CrustConfigFetchScreen;
import fathertoast.crust.client.screen.CrustConfigFileScreen;
import fathertoast.crust.client.screen.widget.CrustConfigFileList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** The base entry for config file selection lists. */
public abstract class FileGuiEntry extends ContainerObjectSelectionList.Entry<FileGuiEntry> implements ISearchable {
    
    protected final CrustConfigFileList PARENT;
    protected final Component NAME;
    
    public FileGuiEntry( CrustConfigFileList parent, Component name ) {
        PARENT = parent;
        NAME = name;
    }
    
    /** @return The client singleton instance. Just a slightly more readable version of the usual method. */
    public final Minecraft client() { return Minecraft.getInstance(); }
    
    /** @return The currently open screen. We assume it must be non-null because this is only used by screens. */
    public final Screen screen() { return Objects.requireNonNull( client().screen ); }
    
    /** @return Narrations for this entry. */
    @Override
    public List<? extends NarratableEntry> narratables() { return List.of(); }
    
    /** @return An identifying String to be looked up by a {@link Searchbar} */
    @Override // ISearchable
    public String getLookupName() { return NAME.getString(); }
    
    
    // ---- Implementations ---- //
    
    /** A file directory header for config file selection lists. */
    public static class Directory extends FileGuiEntry {
        
        protected final int WIDTH;
        
        public Directory( CrustConfigFileList parent, Component name ) {
            super( parent, name );
            WIDTH = client().font.width( name );
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            graphics.drawString( client().font, NAME,
                    screen().width - WIDTH >> 1, rowTop + 6, 0xFFFFFF );
        }
        
        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) { return null; }
        
        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
        
        /**
         * This is used by {@link SearchableSelectionList} to render
         * a highlight behind this searchable if it is said list's
         * currently focused search match.
         *
         * @param isFocused    True if this searchable is the currently focused entry in the underlying searchable list.
         * @param scrollbarPos The position of the underlying searchable list's scrollbar.
         * @see SearchableSelectionList#renderItem(GuiGraphics, int, int, float, int, int, int, int, int)
         */
        @Override // ISearchable
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            int textWidth = client().font.width( NAME.getString() );
            int x = (screen().width - WIDTH >> 1) - 8;
            int width = Math.min( x + textWidth + 15, x + scrollbarPos );
            int height = rowTop + itemHeight + 3;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x, rowTop, width, height );
        }
    }
    
    /** A file display row for config file selection lists. */
    public static class File extends FileGuiEntry {
        
        protected final CrustConfigSpec SPEC;
        protected final Button OPEN_BUTTON;
        
        public File( CrustConfigFileList parent, Component name, CrustConfigSpec spec, CrustConfigFileList.FileState state ) {
            super( parent, name );
            SPEC = spec;
            
            boolean local = spec.CLIENT_ONLY || GuiUtil.isServerLocal();
            boolean editable = state == CrustConfigFileList.FileState.FULL_ACCESS;
            MutableComponent specError = Component.translatable( "menu.crust.config.select.button.spec_error" )
                    .withStyle( ChatFormatting.RED );
            
            OPEN_BUTTON = new Button( 0, 0, 20, 20,
                    Component.literal( ">" ),
                    button -> client().setScreen( local ?
                            new CrustConfigFileScreen( screen(), SPEC, false, editable ) :
                            new CrustConfigFetchScreen( screen(), SPEC, editable ) ),
                    SPEC.isInitialized() ? Supplier::get : supplier -> specError ) {
                @Override
                protected ClientTooltipPositioner createTooltipPositioner() {
                    return GuiUtil.getOrForMenu( this, GuiUtil.TooltipPositioner.CENTERED_Y );
                }
            };
            
            if( !SPEC.isInitialized() ) {
                OPEN_BUTTON.active = false;
                OPEN_BUTTON.setTooltip( Tooltip.create( specError ) );
            }
            else if( state == CrustConfigFileList.FileState.NO_ACCESS ) {
                OPEN_BUTTON.active = false;
                OPEN_BUTTON.setTooltip( Tooltip.create( Component.translatable( "menu.crust.config.select.button.no_access" ) ) );
            }
            else if( state == CrustConfigFileList.FileState.READ_ONLY ) {
                OPEN_BUTTON.setTooltip( Tooltip.create( Component.translatable( "menu.crust.config.select.button.read_only" ) ) );
            }
            else if( !local ) {
                OPEN_BUTTON.setTooltip( Tooltip.create( Component.translatable( "menu.crust.config.select.button.remote" ) ) );
            }
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            graphics.drawString( client().font, NAME,
                    screen().width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + 6, 0xFFFFFF );
            
            OPEN_BUTTON.setX( (screen().width + PARENT.maxNameWidth + 30 >> 1) - 20 );
            OPEN_BUTTON.setY( rowTop );
            OPEN_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends GuiEventListener> children() { return ImmutableList.of( OPEN_BUTTON ); }
        
        @Override
        public boolean mouseClicked( double x, double y, int mouseKey ) {
            return OPEN_BUTTON.mouseClicked( x, y, mouseKey );
        }
        
        @Override
        public boolean mouseReleased( double x, double y, int mouseKey ) {
            return OPEN_BUTTON.mouseReleased( x, y, mouseKey );
        }
        
        /**
         * This is used by {@link SearchableSelectionList} to render
         * a highlight behind this searchable if it is said list's
         * currently focused search match.
         *
         * @param isFocused    True if this searchable is the currently focused entry in the underlying searchable list.
         * @param scrollbarPos The position of the underlying searchable list's scrollbar.
         * @see SearchableSelectionList#renderItem(GuiGraphics, int, int, float, int, int, int, int, int)
         */
        @Override // ISearchable
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            int x = screen().width - PARENT.maxNameWidth - 30 >> 1;
            int width = OPEN_BUTTON.getX() + 3;
            int height = rowTop + itemHeight + 3;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x - 3, rowTop, width, height );
        }
    }
}