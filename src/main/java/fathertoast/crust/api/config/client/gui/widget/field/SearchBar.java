package fathertoast.crust.api.config.client.gui.widget.field;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * An extension of {@link EditBox} that can look up
 * entries in a selection list and provides
 * navigation buttons to jump between search candidates.
 */
public class SearchBar extends EditBox {
    
    /** The default search matcher predicate. */
    private static final BiPredicate<String, String> DEFAULT_MATCHER = StringUtils::containsIgnoreCase;
    
    /** The selection list tied to this search bar. */
    private final SearchableSelectionList<? extends Searchable> selectionList;
    /** The search matcher predicate used by this search bar. */
    private final BiPredicate<String, String> matchPredicate;
    
    /** The current list of search candidates. Refreshed on search. */
    private ImmutableList<Searchable> searchCandidates = ImmutableList.of();
    
    
    /** Creates a new search bar with the default search matcher predicate ({@link SearchBar#DEFAULT_MATCHER}). */
    public SearchBar( SearchableSelectionList<? extends Searchable> selectionList, Font font, int x, int y, int width, int height ) {
        this( selectionList, font, x, y, width, height, DEFAULT_MATCHER );
    }
    
    /** Creates a new search bar with a custom search matcher predicate. */
    public SearchBar( SearchableSelectionList<? extends Searchable> selectionList, Font font, int x, int y, int width, int height, BiPredicate<String, String> matchPredicate ) {
        super( font, x, y, width, height, Component.literal( "" ) );
        this.selectionList = selectionList;
        this.matchPredicate = matchPredicate;
        setHint( Component.translatable( "menu.crust.config.search_bar.hint" ).withStyle( ChatFormatting.ITALIC, ChatFormatting.GRAY ) );
        setResponder( this::search );
    }
    
    /**
     * Performs a name-comparison search on all the elements
     * in the search bar's underlying selection list.<br>
     * Valid candidates are added to {@link SearchBar#searchCandidates}
     * for easy scroll navigation later.<br><br>
     * Scrolls to the first found candidate, if any.
     */
    public void search( String value ) {
        if( value.isEmpty() ) {
            searchCandidates = ImmutableList.of();
        }
        else {
            List<Searchable> candidates = new ArrayList<>();
            boolean foundFirst = false;
            
            // Loop through the elements in the selection list
            for( int i = 0; i < selectionList.children().size(); i++ ) {
                Searchable searchable = selectionList.children().get( i );
                String lookupName = searchable.getLookupName();
                
                // Check if the element's name contains the search String (not case-sensitive)
                if( matchPredicate.test( lookupName, value ) ) {
                    // Scroll to the first candidate, if any
                    if( !foundFirst ) {
                        foundFirst = true;
                        final int index = selectionList.children().indexOf( searchable );
                        final int bottom = selectionList.getBottom();
                        final int top = selectionList.getTop();
                        final int itemHeight = selectionList.itemHeight;
                        selectionList.setScrollAmount( index * itemHeight + (double) (itemHeight / 2) - (double) ((bottom - top) / 2) );
                    }
                    candidates.add( searchable );
                }
            }
            searchCandidates = ImmutableList.copyOf( candidates );
        }
        selectionList.setSearchCandidates( searchCandidates );
    }
    
    /** @return An unmodifiable view of the search bar's current search candidates. */
    public ImmutableList<Searchable> getSearchCandidates() {
        return searchCandidates;
    }
    
    /** Represents an element that can be looked up by name. */
    public interface Searchable {
        
        /** @return An identifying String to be looked up by a {@link SearchBar} */
        @Nullable
        String getLookupName();
    }
}
