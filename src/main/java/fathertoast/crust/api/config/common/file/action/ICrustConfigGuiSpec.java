package fathertoast.crust.api.config.common.file.action;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigGuiEntry;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.util.OnClient;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Contains builder-style methods to build the in-game config editor GUI,
 * similarly to building a config spec.
 * <p>
 * Each time a config file is opened in the in-game editor, its spec calls
 * {@link ISpecAction#initGui(ICrustConfigGuiSpec)} on each {@link ISpecAction},
 * in the order they were added, to build that config file's GUI.
 */
@OnClient
public interface ICrustConfigGuiSpec {
    
    /** The total amount of space available for field widgets. */
    int OVERALL_WIDTH = 310;
    /** The width of the 'reset to default' button, including padding. */
    int RESET_BUTTON_WIDTH = 12;
    /** The width of the scroll bar, including padding. */
    int SCROLL_WIDTH = 10;
    /** The total amount of space available for left-aligned rows. */
    int MAX_WIDTH = OVERALL_WIDTH - SCROLL_WIDTH;
    
    
    /** Appends an empty line. */
    default ICrustConfigGuiSpec newLine() { return newLine( 1 ); }
    
    /** Appends a specific number of empty lines. */
    ICrustConfigGuiSpec newLine( int count );
    
    /** Appends a comment. Each string in the list is printed on a separate line, in the order returned by iteration. */
    default ICrustConfigGuiSpec comment( List<String> comment ) { return comment( comment, 0x777777 ); }
    
    /** Appends a comment. Each string in the list is printed on a separate line, in the order returned by iteration. */
    default ICrustConfigGuiSpec comment( List<String> comment, int color ) {
        for( String line : comment ) comment( line, color );
        return this;
    }
    
    /** Appends a single-line comment. */
    default ICrustConfigGuiSpec comment( String str ) { return comment( str, 0x777777 ); }
    
    /** Appends a single-line comment. */
    ICrustConfigGuiSpec comment( String str, int color );
    
    /** Appends a tooltip comment. */
    default ICrustConfigGuiSpec titledComment( String title, List<String> comment ) { return titledComment( title, comment, 0x777777 ); }
    
    /** Appends a tooltip comment. */
    ICrustConfigGuiSpec titledComment( String title, List<String> comment, int color );
    
    /** Appends a single-line centered header with a tooltip comment. */
    default ICrustConfigGuiSpec header( String str, @Nullable List<String> comment ) { return header( str, comment, 0xFFFF55 ); }
    
    /** Appends a single-line centered header with a tooltip comment. */
    ICrustConfigGuiSpec header( String str, @Nullable List<String> comment, int color );
    
    /** Appends a line entry. */ // If none of the built-in functions do what you need, make your own
    ICrustConfigGuiSpec add( ConfigGuiEntry entry );
    
    /** INTERNAL METHOD. Appends a field widget. */
    @ApiStatus.Internal
    ICrustConfigGuiSpec field( IConfigField<?> field, @Nullable RestartNote restartNote, List<String> addedComment );
}