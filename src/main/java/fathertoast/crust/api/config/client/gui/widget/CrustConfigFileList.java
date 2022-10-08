package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigFileScreen;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Widget that displays a sorted, scrollable list of all config files for one mod.
 * <p>
 * Note that this will ONLY show files that have been defined. Files defined, but not
 * initialized, will be visible only as inactive buttons (cannot be opened).
 */
public class CrustConfigFileList extends AbstractOptionList<CrustConfigFileList.Entry> {
    
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
                addEntry( new CategoryEntry( this, new StringTextComponent(
                        makePrettyPath( cfgManager.DIR.getName(), cfgFile.REL_FILE ) ) ) );
            }
            
            // File buttons
            ITextComponent name = new StringTextComponent( CrustConfigFileScreen.getSpecName( cfgFile.SPEC ) );
            int nameWidth = game.font.width( name );
            if( nameWidth > maxNameWidth ) maxNameWidth = nameWidth;
            
            addEntry( new FileEntry( this, cfgFile.SPEC, name ) );
        }
    }
    
    /** The base entry for config file selection lists. */
    public abstract static class Entry extends AbstractOptionList.Entry<CrustConfigFileList.Entry> { }
    
    /** A file directory header for config file selection lists. */
    public static class CategoryEntry extends Entry {
        
        private final CrustConfigFileList PARENT;
        private final ITextComponent NAME;
        private final int WIDTH;
        
        public CategoryEntry( CrustConfigFileList parent, ITextComponent name ) {
            PARENT = parent;
            NAME = name;
            WIDTH = parent.minecraft.font.width( name );
        }
        
        @Override
        public void render( MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            PARENT.minecraft.font.draw( matrixStack, NAME,
                    PARENT.minecraft.screen.width - WIDTH >> 1,
                    rowTop + rowHeight - 9 - 1, 0xFFFFFF );
        }
        
        @Override
        public boolean changeFocus( boolean forward ) { return false; }
        
        @Override
        public List<? extends IGuiEventListener> children() { return Collections.emptyList(); }
    }
    
    /** A file display row for config file selection lists. */
    public static class FileEntry extends Entry {
        
        private final CrustConfigFileList PARENT;
        private final CrustConfigSpec SPEC;
        private final ITextComponent NAME;
        private final Button OPEN_BUTTON;
        
        private FileEntry( CrustConfigFileList parent, CrustConfigSpec spec, ITextComponent name ) {
            PARENT = parent;
            SPEC = spec;
            NAME = name;
            //noinspection ConstantConditions
            OPEN_BUTTON = new Button( 0, 0, 20, 20,
                    new StringTextComponent( ">" ),
                    ( button ) -> PARENT.minecraft.setScreen(
                            new CrustConfigFileScreen( PARENT.minecraft.screen, SPEC ) ) );
            OPEN_BUTTON.active = SPEC.isInitialized();
        }
        
        @Override
        public void render( MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            PARENT.minecraft.font.draw( matrixStack, NAME,
                    PARENT.minecraft.screen.width - PARENT.maxNameWidth - 30 >> 1,
                    rowTop + (rowHeight - 9 >> 1), 0xFFFFFF );
            
            OPEN_BUTTON.x = (PARENT.minecraft.screen.width + PARENT.maxNameWidth + 30 >> 1) - 20;
            OPEN_BUTTON.y = rowTop;
            OPEN_BUTTON.render( matrixStack, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends IGuiEventListener> children() { return ImmutableList.of( OPEN_BUTTON ); }
        
        @Override
        public boolean mouseClicked( double x, double y, int mouseKey ) {
            return OPEN_BUTTON.mouseClicked( x, y, mouseKey );
        }
        
        @Override
        public boolean mouseReleased( double x, double y, int mouseKey ) {
            return OPEN_BUTTON.mouseReleased( x, y, mouseKey );
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