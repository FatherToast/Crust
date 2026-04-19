package fathertoast.crust.api.config.client.gui.widget.field;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
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
    
    /** The screen this search bar belongs to. */
    private final Screen PARENT_SCREEN;
    /** The selection list tied to this searchbar. */
    private final SearchableSelectionList<? extends Searchable> SEARCHABLE_LIST;
    /** The search matcher predicate used by this searchbar. */
    private final BiPredicate<String, String> MATCH_PREDICATE;
    
    /** A bidirectional map that maps search match indexes to selection list element indexes. */
    private ImmutableBiMap<Integer, Integer> elementByMatchIndexes = ImmutableBiMap.of();
    /** Navigation button for going to the next search match. */
    private final Button previousMatchButton;
    /** Navigation button for going to the previous search match. */
    private final Button nextMatchButton;
    
    /** The current map of search matches and element indexes. Refreshed on search. */
    private ImmutableMap<Integer, Searchable> searchMatches = ImmutableMap.of();
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
        Searchbar searchBar = new Searchbar( parentScreen, selectionList, orientation, font, x, y, width, 16, matchPredicate );
        
        addWidgetToScreen( parentScreen, searchBar );
        addWidgetToScreen( parentScreen, searchBar.previousMatchButton );
        addWidgetToScreen( parentScreen, searchBar.nextMatchButton );
        
        return searchBar;
    }
    
    private static void addWidgetToScreen( Screen screen, AbstractWidget widget ) {
        screen.renderables.add( widget );
        screen.children.add( widget );
        screen.narratables.add( widget );
    }
    
    private Searchbar( Screen parentScreen, SearchableSelectionList<? extends Searchable> searchableList, Orientation orientation, Font font, int x, int y,
                       int width, int height, BiPredicate<String, String> matcher ) {
        super( font, x, y, width, height, Component.literal( "" ) );
        // Make sure these things are present
        Objects.requireNonNull( parentScreen );
        Objects.requireNonNull( searchableList );
        Objects.requireNonNull( orientation );
        Objects.requireNonNull( matcher );
        PARENT_SCREEN = parentScreen;
        SEARCHABLE_LIST = searchableList;
        MATCH_PREDICATE = matcher;
        setHint( Component.translatable( "menu.crust.config.search_bar.hint" ).withStyle( ChatFormatting.ITALIC, ChatFormatting.GRAY ) );
        setResponder( ( value ) -> search( value, false ) );
        
        // Create navigation buttons
        final int buttonX = orientation == Orientation.RIGHT
                ? x + width + 4
                : x - 15;
        
        previousMatchButton = new ImageButton( buttonX, y, 11, 7, 11, 0, 7,
                SEARCH_BAR_ICONS,
                ( button ) -> {
                    if( focusedIndex == 0 ) {
                        setFocusedIndex( searchMatches.size() - 1 );
                    }
                    else {
                        setFocusedIndex( --focusedIndex );
                    }
                    // noinspection ConstantConditions
                    scrollToIndex( elementByMatchIndexes.get( focusedIndex ) );
                    button.setFocused( false );
                } );
        nextMatchButton = new ImageButton( buttonX, (y + height / 2) + 1, 11, 7, 0, 0, 7,
                SEARCH_BAR_ICONS,
                ( button ) -> {
                    if( focusedIndex == searchMatches.size() - 1 ) {
                        setFocusedIndex( 0 );
                    }
                    else {
                        setFocusedIndex( ++focusedIndex );
                    }
                    // noinspection ConstantConditions
                    scrollToIndex( elementByMatchIndexes.get( focusedIndex ) );
                    button.setFocused( false );
                } );
        setNavButtonsState( false );
    }
    
    /**
     * Performs a name-comparison search on all the elements
     * in the searchbar's underlying selection list.<br>
     * Valid candidates are added to {@link Searchbar#searchMatches}
     * for easy scroll navigation later.<br><br>
     * Scrolls to the first found candidate, if any.
     */
    public void search( String value, boolean forceSearch ) {
        // No point in doing anything if the
        // search value didn't change, unless we are forcing a search.
        if( value.equals( lastSearch ) && !forceSearch )
            return;
        
        // Update last search.
        lastSearch = value;
        
        setNavButtonsState( false );
        
        // Clear indexes and search candidates.
        searchMatches = ImmutableMap.of();
        elementByMatchIndexes = ImmutableBiMap.of();
        
        // Reset scroll if empty.
        if( value.isEmpty() ) {
            scrollToIndex( -1 );
        }
        else {
            final Map<Integer, Searchable> candidates = new HashMap<>();
            final Map<Integer, Integer> elementByCandidates = new HashMap<>();
            boolean foundFirst = false;
            int key = 0;
            
            // Loop through the elements in the selection list.
            for( int elementIndex = 0; elementIndex < SEARCHABLE_LIST.children().size(); elementIndex++ ) {
                Searchable searchable = SEARCHABLE_LIST.children().get( elementIndex );
                String lookupName = searchable.getLookupName();
                
                // Check if the element is a valid candidate.
                if( MATCH_PREDICATE.test( lookupName, value ) ) {
                    // Scroll to the first candidate, if any.
                    if( !foundFirst ) {
                        foundFirst = true;
                        setFocusedIndex( key );
                        scrollToIndex( elementIndex );
                    }
                    candidates.put( key, searchable );
                    elementByCandidates.put( key, elementIndex );
                    ++key;
                }
            }
            searchMatches = ImmutableMap.copyOf( candidates );
            elementByMatchIndexes = ImmutableBiMap.copyOf( elementByCandidates );
            
            // Update navigation buttons.
            if( !searchMatches.isEmpty() ) {
                setNavButtonsState( searchMatches.size() > 1 );
                setTextColor( IConfigFieldWidgetProvider.DEFAULT_COLOR );
            }
            else {
                setTextColor( IConfigFieldWidgetProvider.INVALID_COLOR );
            }
        }
    }
    
    /** Updates the visibility and "active" state of the match navigation buttons. */
    private void setNavButtonsState( boolean active ) {
        nextMatchButton.active = active;
        nextMatchButton.visible = active;
        previousMatchButton.active = active;
        previousMatchButton.visible = active;
    }
    
    /** Tells this searchbar's selection list to scroll to the element at the given index. */
    private void scrollToIndex( int index ) {
        final int listSize = SEARCHABLE_LIST.children().size();
        
        // Negative index, assume it is intentional
        // for defocusing the focused search candidate.
        if( index < 0 ) {
            SEARCHABLE_LIST.setScrollAmount( 0.0 );
        }
        // Out of bounds
        else if( index > listSize - 1 ) {
            throw new IndexOutOfBoundsException( "Attempted to scroll to an out-of-bounds index in a selection list!" );
        }
        final int bottom = SEARCHABLE_LIST.getBottom();
        final int top = SEARCHABLE_LIST.getTop();
        final int itemHeight = SEARCHABLE_LIST.itemHeight;
        SEARCHABLE_LIST.setScrollAmount( index * itemHeight + (double) (itemHeight / 2) - (double) ((bottom - top) / 2) );
    }
    
    /** Sets focused search match index for self and the underlying selection list. */
    private void setFocusedIndex( int index ) {
        focusedIndex = index;
    }
    
    /** @return The index of the currently focused search match. */
    public int getFocusedIndex() {
        return focusedIndex;
    }
    
    /** @return An unmodifiable view of the searchbar's current search candidates. */
    public ImmutableMap<Integer, Searchable> getSearchMatches() {
        return searchMatches;
    }
    
    /** @return An unmodifiable view of the searchbar's element-by-candidate indexes. */
    public ImmutableBiMap<Integer, Integer> getElementByMatchIndexes() { return elementByMatchIndexes; }
    
    /** Called each frame to draw this component. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        super.render( graphics, mouseX, mouseY, partialTick );
        
        // Draws a string above the searchbar displaying
        // current index over total matches
        if( !searchMatches.isEmpty() ) {
            Component indexOverMatches = Component.literal( (focusedIndex + 1) + " / " + searchMatches.size() )
                    .withStyle( ChatFormatting.GRAY );
            graphics.drawCenteredString( font, indexOverMatches, getX() + width / 2, getY() - getHeight() + 5, 0xFFFFFF );
        }
    }
    
    /**
     * Called when a keyboard key is pressed.
     *
     * @param key      The keyboard key that was pressed (see {@link InputConstants.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputConstants.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        // If any matches exist, allow using the up & down arrow keys
        // to navigate matches without the search bar losing focus.
        if( !getSearchMatches().isEmpty() ) {
            if( key == InputConstants.getKey( "key.keyboard.up" ).getValue() ) {
                previousMatchButton.onPress();
                return true;
            }
            else if( key == InputConstants.getKey( "key.keyboard.down" ).getValue() ) {
                nextMatchButton.onPress();
                return true;
            }
        }
        return super.keyPressed( key, scancode, mods );
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