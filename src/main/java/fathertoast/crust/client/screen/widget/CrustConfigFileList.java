package fathertoast.crust.client.screen.widget;

import fathertoast.crust.api.client.util.GuiUtil;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.lib.CrustCmdHelper;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.screen.widget.entry.FileGuiEntry;
import fathertoast.crust.common.config.CrustConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Widget that displays a sorted, scrollable list of all config files for one mod.
 * <p>
 * Note that files defined, but not initialized, will be visible only as inactive buttons (cannot be opened).
 */
public class CrustConfigFileList extends SearchableSelectionList<FileGuiEntry> {
    
    public final int maxNameWidth;
    
    public CrustConfigFileList( Screen parent, Minecraft game, ConfigManager cfgManager ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 20 );
        // Check permissions
        boolean canEdit = CrustCmdHelper.hasPermissions( game.player, CrustCmdHelper.PERMISSION_SERVER_OP );
        boolean canRead = canEdit || CrustCmdHelper.hasPermissions( game.player, CrustConfig.UTILITIES.CONFIGS.viewConfigsOpLevel.get() );
        Visibility visibility = ClientRegister.CONFIG_EDITOR.EDIT_SCREEN.fileVisibility.get();
        
        // Gather all managed config files and sort
        Path rootPath = cfgManager.DIR.toPath();
        ArrayList<SortableFile> cfgFiles = new ArrayList<>();
        for( AbstractConfigFile cfgFile : cfgManager.getConfigs() ) {
            // Check file visibility
            FileState state = cfgFile.SPEC.CLIENT_ONLY || canEdit ? FileState.FULL_ACCESS :
                    canRead ? FileState.READ_ONLY : FileState.NO_ACCESS;
            if( visibility.shouldShow( state ) ) {
                cfgFiles.add( new SortableFile( rootPath, cfgFile, state ) );
            }
        }
        Collections.sort( cfgFiles );
        
        // Populate the list contents
        String currentDir = null;
        int currentMaxWidth = 0;
        for( SortableFile cfgFile : cfgFiles ) {
            // Directory headers
            String dir = cfgFile.DIR;
            if( !dir.equals( currentDir ) ) {
                currentDir = dir;
                addEntry( new FileGuiEntry.Directory( this, Component.literal(
                        makePrettyPath( cfgManager.DIR.getName(), cfgFile.REL_FILE ) ) ) );
            }
            
            // File buttons
            MutableComponent name = Component.literal( ConfigUtil.getSpecName( cfgFile.SPEC ) );
            if( !cfgFile.SPEC.CLIENT_ONLY && !GuiUtil.isServerLocal() ) {
                // Format name for remote file
                if( cfgFile.STATE == FileState.FULL_ACCESS ) name.withStyle( ChatFormatting.AQUA );
                else name.withStyle( ChatFormatting.DARK_GRAY );
            }
            int nameWidth = game.font.width( name );
            if( nameWidth > currentMaxWidth ) currentMaxWidth = nameWidth;
            
            addEntry( new FileGuiEntry.File( this, name, cfgFile.SPEC, cfgFile.STATE ) );
        }
        maxNameWidth = currentMaxWidth;
    }
    
    
    /** A user preference, indicating which files they want to see in the editor menu. */
    public enum Visibility {
        /** Show all files, regardless of permissions. */
        SHOW_ALL,
        /** Only show files you have permission to view. */
        HIDE_UNREADABLE,
        /** Only show files you have permission to view and edit. */
        HIDE_UNEDITABLE;
        
        /** @return True if the file state should be shown under this visibility setting. */
        public boolean shouldShow( FileState state ) {
            return switch( this ) {
                case SHOW_ALL -> true;
                case HIDE_UNREADABLE -> state != FileState.NO_ACCESS;
                case HIDE_UNEDITABLE -> state == FileState.FULL_ACCESS;
            };
        }
    }
    
    /** What level of access the player has to a particular file. */
    public enum FileState { FULL_ACCESS, READ_ONLY, NO_ACCESS }
    
    /** Wrapper for {@link AbstractConfigFile} that makes it easier to sort the way we want. */
    private static class SortableFile implements Comparable<SortableFile> {
        
        final CrustConfigSpec SPEC;
        final File REL_FILE;
        final String DIR;
        final String FILE;
        final FileState STATE;
        
        SortableFile( Path rootPath, AbstractConfigFile cfgFile, FileState state ) {
            SPEC = cfgFile.SPEC;
            REL_FILE = relativize( rootPath, cfgFile );
            File file = REL_FILE.getParentFile();
            DIR = file == null ? "" : file.toString();
            FILE = REL_FILE.getName();
            STATE = state;
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