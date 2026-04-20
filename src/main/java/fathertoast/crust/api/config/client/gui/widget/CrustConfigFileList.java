package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.client.util.GuiUtil;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigFileScreen;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
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
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Widget that displays a sorted, scrollable list of all config files for one mod.
 * <p>
 * Note that this will ONLY show files that have been defined. Files defined, but not
 * initialized, will be visible only as inactive buttons (cannot be opened).
 */
public class CrustConfigFileList extends SearchableSelectionList<CrustConfigFileList.Entry> {
    
    private int maxNameWidth;
    
    public CrustConfigFileList( Screen parent, Minecraft game, ConfigManager cfgManager ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 20 );
        // Gather all managed config files and sort
        Path rootPath = cfgManager.DIR.toPath();
        ArrayList<SortableFile> cfgFiles = new ArrayList<>();
        for( AbstractConfigFile cfgFile : cfgManager.getConfigs() ) {
            cfgFiles.add( new SortableFile( rootPath, cfgFile ) );
        }
        Collections.sort( cfgFiles );
        
        // Populate the list contents
        String currentDir = null;
        for( SortableFile cfgFile : cfgFiles ) {
            // Directory headers
            String dir = cfgFile.DIR;
            if( !dir.equals( currentDir ) ) {
                currentDir = dir;
                addEntry( new CategoryEntry( this, Component.literal(
                        makePrettyPath( cfgManager.DIR.getName(), cfgFile.REL_FILE ) ) ) );
            }
            
            // File buttons
            Component name = Component.literal( CrustConfigFileScreen.getSpecName( cfgFile.SPEC ) );
            int nameWidth = game.font.width( name );
            if( nameWidth > maxNameWidth ) maxNameWidth = nameWidth;
            
            addEntry( new FileEntry( this, cfgFile.SPEC, name ) );
        }
    }
    
    /** The base entry for config file selection lists. */
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<CrustConfigFileList.Entry>
            implements ISearchable { }
    
    /** A file directory header for config file selection lists. */
    public static class CategoryEntry extends Entry {
        
        private final CrustConfigFileList PARENT;
        private final Component NAME;
        private final int WIDTH;
        
        public CategoryEntry( CrustConfigFileList parent, Component name ) {
            PARENT = parent;
            NAME = name;
            WIDTH = parent.minecraft.font.width( name );
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            graphics.drawString( PARENT.minecraft.font, NAME,
                    PARENT.minecraft.screen.width - WIDTH >> 1,
                    rowTop + rowHeight - 9 - 1, 0xFFFFFF );
        }
        
        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
            return null;
        }
        
        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        @Override
        public String getLookupName() {
            return NAME.getString();
        }
        
        @Override
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            int textWidth = Minecraft.getInstance().font.width( NAME.getString() );
            //noinspection ConstantConditions
            int x = (PARENT.minecraft.screen.width - WIDTH >> 1) - 8;
            int width = Math.min( x + textWidth + 15, x + scrollbarPos );
            int height = rowTop + itemHeight + 3;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x, rowTop, width, height );
        }
    }
    
    /** A file display row for config file selection lists. */
    public static class FileEntry extends Entry {
        
        private final CrustConfigFileList PARENT;
        private final CrustConfigSpec SPEC;
        private final Component NAME;
        private final Button OPEN_BUTTON;
        
        private FileEntry( CrustConfigFileList parent, CrustConfigSpec spec, Component name ) {
            PARENT = parent;
            SPEC = spec;
            NAME = name;
            
            final MutableComponent specError = Component.translatable( "menu.crust.config.select.button.spec_error" )
                    .withStyle( ChatFormatting.RED );
            
            //noinspection ConstantConditions
            OPEN_BUTTON = new Button( 0, 0, 20, 20,
                    Component.literal( ">" ),
                    ( button ) -> PARENT.minecraft.setScreen(
                            new CrustConfigFileScreen( PARENT.minecraft.screen, SPEC ) ),
                    SPEC.isInitialized() ? Supplier::get : ( supplier ) -> specError ) {
                @Override
                protected ClientTooltipPositioner createTooltipPositioner() {
                    return GuiUtil.getOrForMenu( this, GuiUtil.TooltipPositioner.CENTERED_Y );
                }
            };
            
            if( !SPEC.isInitialized() ) {
                OPEN_BUTTON.active = false;
                OPEN_BUTTON.setTooltip( Tooltip.create( specError ) );
            }
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            graphics.drawString( PARENT.minecraft.font, NAME,
                    PARENT.minecraft.screen.width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + (rowHeight - 5 >> 1), 0xFFFFFF );
            
            OPEN_BUTTON.setX( (PARENT.minecraft.screen.width + PARENT.maxNameWidth + 30 >> 1) - 20 );
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
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        @Override
        public String getLookupName() {
            return NAME.getString();
        }
        
        @Override
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            // noinspection ConstantConditions
            int openButtonX = OPEN_BUTTON.getX();
            int x = rowLeft + 14;
            int width = Math.min( scrollbarPos, openButtonX );
            int height = rowTop + itemHeight + 3;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x, rowTop, width, height );
        }
    }
    
    
    /** Wrapper for {@link AbstractConfigFile} that makes it easier to sort the way we want. */
    private static class SortableFile implements Comparable<SortableFile> {
        
        final CrustConfigSpec SPEC;
        final File REL_FILE;
        final String DIR;
        final String FILE;
        
        SortableFile( Path rootPath, AbstractConfigFile cfgFile ) {
            SPEC = cfgFile.SPEC;
            REL_FILE = relativize( rootPath, cfgFile );
            File file = REL_FILE.getParentFile();
            DIR = file == null ? "" : file.toString();
            FILE = REL_FILE.getName();
        }
        
        /**
         * The natural order of our sorted files groups by directory first, then file name (all alphabetical).
         * Root directory is first, then each directory should come in a 'depth-first' style.
         */
        @Override
        public int compareTo( SortableFile other ) {
            int dirResult = DIR.compareTo( other.DIR );
            return dirResult == 0 ? FILE.compareTo( other.FILE ) : dirResult;
        }
    }
    
    
    /** @return A pretty path string to display for the directory. */
    private static String makePrettyPath( String base, File file ) {
        file = file.getParentFile();
        if( file == null ) return base;
        
        StringBuilder builder = new StringBuilder();
        do {
            builder.insert( 0, file.getName() ).insert( 0, " > " );
            file = file.getParentFile();
        }
        while( file != null );
        return builder.insert( 0, base ).toString();
    }
    
    /** @return The config file's abstract path relative to the provided root path. */
    private static File relativize( Path rootPath, AbstractConfigFile cfgFile ) {
        return rootPath.relativize( cfgFile.SPEC.getFile().toPath() ).toFile();
    }
}