package fathertoast.crust.api.config.client.gui.widget.field.searchbar;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.field.list.FullScreenPopupListWidget;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
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
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An extension of {@link EditBox} that can look up
 * entries in a {@link SearchableSelectionList} and provides
 * navigation buttons to jump between search candidates.
 */
public class Searchbar extends EditBox {
    
    public static final ResourceLocation SEARCH_BAR_ICONS = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/search_bar_items.png" );
    public static final int ARROW_BUTTON_WIDTH = 11;
    /** The default search matcher predicate. */
    public static final BiPredicate<String, String> DEFAULT_MATCHER = StringUtils::containsIgnoreCase;
    
    // Config accessors; populated when Crust's client configs are defined.
    public static Supplier<Searchbar.Orientation> orientation;
    public static Supplier<Boolean> showHighlights;
    public static Supplier<Integer> highlightColor;
    
    /**
     * Creates a new searchbar with a custom search matcher predicate and adds it to the parent screen's widget list,
     * including the searchbar's child components.
     */
    public static Searchbar create( Screen parentScreen, SearchableSelectionList<? extends ISearchable> selectionList, Orientation orientation,
                                    Font font, int x, int y, int width, @Nullable BiPredicate<String, String> matchPredicate ) {
        List<? extends ISearchable> children = selectionList.children();
        Searchbar searchBar = new Searchbar( orientation, font, x, y, width, 16,
                children::size, children::get, matchPredicate, selectionList::scrollToIndex );
        
        addWidgetToScreen( parentScreen, searchBar );
        addWidgetToScreen( parentScreen, searchBar.previousMatchButton );
        addWidgetToScreen( parentScreen, searchBar.nextMatchButton );
        
        selectionList.setSearchbar( searchBar );
        return searchBar;
    }
    
    private static void addWidgetToScreen( Screen screen, AbstractWidget widget ) {
        screen.renderables.add( widget );
        screen.children.add( widget );
        screen.narratables.add( widget );
    }
    
    /**
     * Creates a new searchbar with a custom search matcher predicate and adds it to the parent screen's widget list,
     * including the searchbar's child components.
     */
    public static void create( FullScreenPopupListWidget<?> popupList, Orientation orientation,
                               Font font, int x, int y, int width, @Nullable BiPredicate<String, String> matchPredicate ) {
        Searchbar searchBar = new Searchbar( orientation, font, x, y, width, 16, popupList::getItemCount,
                index -> popupList.getEntry( index ) instanceof ISearchable entry ? entry : null,
                matchPredicate, popupList::scrollToIndex );
        
        popupList.addChild( searchBar );
        popupList.addChild( searchBar.previousMatchButton );
        popupList.addChild( searchBar.nextMatchButton );
        
        popupList.setSearchbar( searchBar );
    }
    
    
    private final Supplier<Integer> SIZE;
    private final Function<Integer, ISearchable> GET_BY_INDEX;
    /** The search matcher predicate used by this searchbar. */
    private final BiPredicate<String, String> MATCH_PREDICATE;
    @Nullable
    private final Consumer<Integer> SCROLL_TO_INDEX;
    
    /** A bidirectional map that maps search match indexes to selection list element indexes. */
    private ImmutableBiMap<Integer, Integer> elementByMatchIndexes = ImmutableBiMap.of();
    /** Navigation button for going to the next search match. */
    private final Button previousMatchButton;
    /** Navigation button for going to the previous search match. */
    private final Button nextMatchButton;
    
    /** The current map of search matches and element indexes. Refreshed on search. */
    private ImmutableMap<Integer, ISearchable> searchMatches = ImmutableMap.of();
    /** The index of the currently "focused" search candidate. */
    private int focusedIndex = -1;
    /** The last String that was searched. */
    private String lastSearch = "";
    
    /**
     * @param x             Distance from the left side of the screen to the left side of the searchbar, in GUI pixels.
     * @param y             Distance from the top side of the screen to the top side of the searchbar, in GUI pixels.
     * @param width         Width of the searchbar, in GUI pixels, including its navigation buttons. Typical is 115.
     * @param height        Height of the searchbar, in GUI pixels. Typical is 16.
     * @param size          Returns the number of elements we can search.
     * @param getByIndex    Returns the searchable element when given an index (0 <= index < size), or null if the
     *                      element at that index is not searchable.
     * @param matcher       Optional matching logic. If null, the search does a case-insensitive contains check.
     * @param scrollToIndex Optional function that scrolls the display to show an element by index, or to the top if
     *                      index is -1. If provided, the search bar will include up and down buttons which take up
     *                      15 pixels of the provided width.
     */
    public Searchbar( Orientation orientation, Font font, int x, int y, int width, int height,
                      Supplier<Integer> size, Function<Integer, ISearchable> getByIndex,
                      @Nullable BiPredicate<String, String> matcher, @Nullable Consumer<Integer> scrollToIndex ) {
        super( font, orientation == Orientation.RIGHT || scrollToIndex == null ? x : x + 15, y,
                scrollToIndex == null ? width : width - 15, height, Component.literal( "" ) );
        SIZE = size;
        GET_BY_INDEX = getByIndex;
        MATCH_PREDICATE = matcher == null ? DEFAULT_MATCHER : matcher;
        SCROLL_TO_INDEX = scrollToIndex;
        
        setHint( Component.translatable( "menu.crust.config.search_bar.hint" ).withStyle( ChatFormatting.ITALIC, ChatFormatting.GRAY ) );
        setResponder( value -> search( value, false ) );
        
        // Create navigation buttons
        int buttonX = orientation == Orientation.RIGHT ? x + width - ARROW_BUTTON_WIDTH : x;
        previousMatchButton = new ImageButton( buttonX, y, ARROW_BUTTON_WIDTH, 7, 11, 0, 7,
                SEARCH_BAR_ICONS, button -> {
            if( getFocusedIndex() == 0 ) setFocusedIndex( searchMatches.size() - 1 );
            else setFocusedIndex( getFocusedIndex() - 1 );
            // noinspection ConstantConditions
            scrollToIndex( elementByMatchIndexes.get( getFocusedIndex() ) );
            button.setFocused( false );
        } );
        nextMatchButton = new ImageButton( buttonX, y + height / 2 + 1, ARROW_BUTTON_WIDTH, 7, 0, 0, 7,
                SEARCH_BAR_ICONS, button -> {
            if( getFocusedIndex() == searchMatches.size() - 1 ) setFocusedIndex( 0 );
            else setFocusedIndex( getFocusedIndex() + 1 );
            // noinspection ConstantConditions
            scrollToIndex( elementByMatchIndexes.get( getFocusedIndex() ) );
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
        // No point in doing anything if the search value didn't change, unless we are forcing a search.
        if( value.equals( lastSearch ) && !forceSearch ) return;
        
        // Update last search.
        lastSearch = value;
        
        // Clear indexes and search candidates.
        setNavButtonsState( false );
        searchMatches = ImmutableMap.of();
        elementByMatchIndexes = ImmutableBiMap.of();
        
        // Reset scroll if empty.
        if( value.isEmpty() ) {
            scrollToIndex( -1 );
            return;
        }
        
        final Map<Integer, ISearchable> candidates = new HashMap<>();
        final Map<Integer, Integer> elementByCandidates = new HashMap<>();
        boolean foundFirst = false;
        int key = 0;
        
        // Loop through the elements in the selection list.
        int size = SIZE.get();
        for( int elementIndex = 0; elementIndex < size; elementIndex++ ) {
            ISearchable searchable = GET_BY_INDEX.apply( elementIndex );
            if( searchable == null ) continue;
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
                key++;
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
    
    /** Updates the visibility and "active" state of the match navigation buttons. */
    private void setNavButtonsState( boolean active ) {
        if( SCROLL_TO_INDEX == null ) active = false;
        nextMatchButton.active = active;
        nextMatchButton.visible = active;
        previousMatchButton.active = active;
        previousMatchButton.visible = active;
    }
    
    /** Tells this searchbar's selection list to scroll to the element at the given index. */
    private void scrollToIndex( int index ) {
        if( SCROLL_TO_INDEX != null ) SCROLL_TO_INDEX.accept( index );
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
    public ImmutableMap<Integer, ISearchable> getSearchMatches() {
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
            Component indexOverMatches = Component.literal( (getFocusedIndex() + 1) + " / " + searchMatches.size() )
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
        // If any matches exist, allow using the up & down arrow keys or return and shift+return
        // to navigate matches without the search bar losing focus.
        if( !getSearchMatches().isEmpty() ) {
            if( key == InputConstants.KEY_UP ) {
                previousMatchButton.onPress();
                return true;
            }
            else if( key == InputConstants.KEY_DOWN ) {
                nextMatchButton.onPress();
                return true;
            }
            else if( key == InputConstants.KEY_RETURN ) {
                if( KeyModifier.SHIFT.isActive( null ) ) previousMatchButton.onPress();
                else nextMatchButton.onPress();
                return true;
            }
        }
        return super.keyPressed( key, scancode, mods );
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
        
        public int getX( int padding, int searchbarWidth, int guiWidth ) {
            return this == LEFT ? padding : guiWidth - padding - searchbarWidth;
        }
    }
}