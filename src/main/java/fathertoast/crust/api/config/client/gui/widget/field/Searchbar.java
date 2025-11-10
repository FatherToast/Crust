package fathertoast.crust.api.config.client.gui.widget.field;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.common.core.Crust;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * An extension of {@link EditBox} that can look up
 * entries in a {@link SearchableSelectionList} and provides
 * navigation buttons to jump between search candidates.
 */
public class Searchbar extends EditBox {
    
    public static final ResourceLocation SEARCH_BAR_ICONS = Crust.rl( "textures/search_bar_items.png" );
    /** The default search matcher predicate. */
    public static final BiPredicate<String, String> DEFAULT_MATCHER = StringUtils::containsIgnoreCase;
    
    /** The selection list tied to this searchbar. */
    private final SearchableSelectionList<? extends Searchable> selectionList;
    /** The search matcher predicate used by this searchbar. */
    private final BiPredicate<String, String> matchPredicate;
    /** A bidirectional map that maps search candidate indexes to selection list element indexes. */
    private final BiMap<Integer, Integer> elementByCandidateIndexes = HashBiMap.create();
    
    /** Navigation button for going to the next search candidate. */
    private final Button previousCandidate;
    /** Navigation button for going to the previous search candidate. */
    private final Button nextCandidate;
    /** The orientation of this searchbar's subcomponents. */
    private final Orientation orientation;
    
    /** The current map of search candidates and element indexes. Refreshed on search. */
    private ImmutableMap<Integer, Searchable> searchCandidates = ImmutableMap.of();
    /** The index of the currently "focused" search candidate. */
    private int focusedIndex = -1;
    /** The last String that was searched. */
    private String lastSearch = "";
    
    /**
     * Creates a new searchbar with a custom search matcher predicate
     * and adds it to the parent screen's widget list,
     * including the searchbar's child components.
     */
    public static Searchbar create( Screen parentScreen, SearchableSelectionList<? extends Searchable> selectionList, Orientation orientation,
                                    Font font, int x, int y, int width, BiPredicate<String, String> matchPredicate ) {
        Objects.requireNonNull( parentScreen );
        Searchbar searchBar = new Searchbar( selectionList, orientation, font, x, y, width, 16, matchPredicate );
        
        addWidgetToScreen( parentScreen, searchBar );
        addWidgetToScreen( parentScreen, searchBar.previousCandidate );
        addWidgetToScreen( parentScreen, searchBar.nextCandidate );
        
        return searchBar;
    }
    
    private static void addWidgetToScreen( Screen screen, AbstractWidget widget ) {
        screen.renderables.add( widget );
        screen.children.add( widget );
        screen.narratables.add( widget );
    }
    
    private Searchbar( SearchableSelectionList<? extends Searchable> selectionList, Orientation orientation, Font font, int x, int y,
                       int width, int height, BiPredicate<String, String> matchPredicate ) {
        super( font, x, y, width, height, Component.literal( "" ) );
        // Make sure these things are present
        Objects.requireNonNull( selectionList );
        Objects.requireNonNull( orientation );
        Objects.requireNonNull( matchPredicate );
        this.selectionList = selectionList;
        this.matchPredicate = matchPredicate;
        this.orientation = orientation;
        setHint( Component.translatable( "menu.crust.config.search_bar.hint" )
                .withStyle( ChatFormatting.ITALIC, ChatFormatting.GRAY ) );
        setResponder( this::search );
        
        // Create navigation buttons
        final int buttonX = orientation == Orientation.RIGHT
                ? x + width + 4
                : x - 15;
        previousCandidate = new ImageButton(
                buttonX,
                y,
                11,
                7,
                11,
                0,
                7,
                SEARCH_BAR_ICONS,
                ( button ) -> {
                    if( focusedIndex == 0 ) {
                        setFocusedIndex( searchCandidates.size() - 1 );
                    }
                    else {
                        setFocusedIndex( --focusedIndex );
                    }
                    // noinspection ConstantConditions
                    scrollToIndex( elementByCandidateIndexes.get( focusedIndex ) );
                    button.setFocused( false );
                } );
        nextCandidate = new ImageButton(
                buttonX,
                (y + height / 2) + 1,
                11,
                7,
                0,
                0,
                7,
                SEARCH_BAR_ICONS,
                ( button ) -> {
                    if( focusedIndex == searchCandidates.size() - 1 ) {
                        setFocusedIndex( 0 );
                    }
                    else {
                        setFocusedIndex( ++focusedIndex );
                    }
                    // noinspection ConstantConditions
                    scrollToIndex( elementByCandidateIndexes.get( focusedIndex ) );
                    button.setFocused( false );
                } );
        
        previousCandidate.active = false;
        previousCandidate.visible = false;
        nextCandidate.active = false;
        nextCandidate.visible = false;
    }
    
    /**
     * Performs a name-comparison search on all the elements
     * in the searchbar's underlying selection list.<br>
     * Valid candidates are added to {@link Searchbar#searchCandidates}
     * for easy scroll navigation later.<br><br>
     * Scrolls to the first found candidate, if any.
     */
    public void search( String value ) {
        // No point in doing anything if the
        // search value didn't change.
        if( value.equals( lastSearch ) )
            return;
        
        // Update last search.
        lastSearch = value;
        
        
        previousCandidate.active = false;
        previousCandidate.visible = false;
        nextCandidate.active = false;
        nextCandidate.visible = false;
        
        // Clear indexes and search candidates.
        searchCandidates = ImmutableMap.of();
        elementByCandidateIndexes.clear();
        
        // Reset scroll if empty.
        if( value.isEmpty() ) {
            scrollToIndex( -1 );
        }
        else {
            final Map<Integer, Searchable> candidates = new HashMap<>();
            boolean foundFirst = false;
            int mapKey = 0;
            
            // Loop through the elements in the selection list.
            for( int elementIndex = 0; elementIndex < selectionList.children().size(); elementIndex++ ) {
                Searchable searchable = selectionList.children().get( elementIndex );
                String lookupName = searchable.getLookupName();
                
                // Check if the element is a valid candidate.
                if( matchPredicate.test( lookupName, value ) ) {
                    // Scroll to the first candidate, if any.
                    if( !foundFirst ) {
                        foundFirst = true;
                        setFocusedIndex( mapKey );
                        scrollToIndex( elementIndex );
                    }
                    candidates.put( mapKey, searchable );
                    elementByCandidateIndexes.put( mapKey, elementIndex );
                    ++mapKey;
                }
            }
            searchCandidates = ImmutableMap.copyOf( candidates );
            
            // Update navigation buttons.
            if( !searchCandidates.isEmpty() ) {
                nextCandidate.active = searchCandidates.size() > 1;
                nextCandidate.visible = nextCandidate.active;
                previousCandidate.active = nextCandidate.active;
                previousCandidate.visible = nextCandidate.active;
                setTextColor( IConfigFieldWidgetProvider.DEFAULT_COLOR );
            }
            else {
                setTextColor( IConfigFieldWidgetProvider.INVALID_COLOR );
            }
        }
        // Notify selection list of current search candidates and index mappings.
        selectionList.setIndexes( elementByCandidateIndexes );
    }
    
    /** Tells this searchbar's selection list to scroll to the element at the given index. */
    private void scrollToIndex( int index ) {
        final int listSize = selectionList.children().size();
        
        // Negative index, assume it is intentional
        // for defocusing the focused search candidate.
        if( index < 0 ) {
            selectionList.setScrollAmount( 0.0 );
        }
        // Out of bounds
        else if( index > listSize - 1 ) {
            throw new IndexOutOfBoundsException( "Attempted to scroll to an out-of-bounds index in a selection list!" );
        }
        final int bottom = selectionList.getBottom();
        final int top = selectionList.getTop();
        final int itemHeight = selectionList.itemHeight;
        selectionList.setScrollAmount( index * itemHeight + (double) (itemHeight / 2) - (double) ((bottom - top) / 2) );
    }
    
    /** Sets focused search match index for self and the underlying selection list. */
    private void setFocusedIndex( int index ) {
        focusedIndex = index;
        selectionList.setFocusedIndex( index );
    }
    
    /** @return An unmodifiable view of the searchbar's current search candidates. */
    public ImmutableMap<Integer, Searchable> getSearchCandidates() {
        return searchCandidates;
    }
    
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        super.render( graphics, mouseX, mouseY, partialTick );
        
        // Draws a string above the searchbar displaying
        // current index over total matches
        if( !searchCandidates.isEmpty() ) {
            Component indexOverMatches = Component.literal( (focusedIndex + 1) + " / " + searchCandidates.size() )
                    .withStyle( ChatFormatting.GRAY );
            graphics.drawCenteredString( font, indexOverMatches, getX() + width / 2, getY() - getHeight() + 5, 0xFFFFFF );
        }
    }
    
    /** Represents an element that can be looked up by name. */
    public interface Searchable {
        
        /** @return An identifying String to be looked up by a {@link Searchbar} */
        @Nullable
        String getLookupName();
    }
    
    /**
     * Used to determine where a searchbar's subcomponents should be placed
     * relative to the searchbar itself; either to the right or to the left
     * of the search bar.
     */
    public enum Orientation {
        RIGHT,
        LEFT;
        
        public Orientation getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }
}