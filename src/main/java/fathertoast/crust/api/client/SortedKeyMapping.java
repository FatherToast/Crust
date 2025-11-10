package fathertoast.crust.api.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

/**
 * A key binding with specified sorting order. Also has handy constructing methods.
 */
@SuppressWarnings( "unused" )
public class SortedKeyMapping extends KeyMapping {
    /**
     * Sorted key binding that is unbound by default, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     */
    public SortedKeyMapping( int i, String description, String category ) {
        this( i, description, category, InputConstants.UNKNOWN.getValue() );
    }
    
    /**
     * Sorted key binding with a default key, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param keyCode     Default key.
     */
    public SortedKeyMapping( int i, String description, String category, int keyCode ) {
        this( i, description, category, InputConstants.Type.KEYSYM, keyCode );
    }
    
    /**
     * Sorted key binding with a default key, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param type        Default key input type.
     * @param keyCode     Default key.
     */
    public SortedKeyMapping( int i, String description, String category,
                             InputConstants.Type type, int keyCode ) {
        this( i, description, category, KeyModifier.NONE, type, keyCode );
    }
    
    /**
     * Sorted key binding with a default key, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param key         Default key.
     */
    public SortedKeyMapping( int i, String description, String category,
                             InputConstants.Key key ) {
        this( i, description, category, KeyModifier.NONE, key );
    }
    
    /**
     * Sorted key binding with a default key + modifier, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param modifier    Default modifier key.
     * @param keyCode     Default key.
     */
    public SortedKeyMapping( int i, String description, String category,
                             KeyModifier modifier, int keyCode ) {
        this( i, description, category, modifier, InputConstants.Type.KEYSYM, keyCode );
    }
    
    /**
     * Sorted key binding with a default key + modifier, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param modifier    Default modifier key.
     * @param type        Default key input type.
     * @param keyCode     Default key.
     */
    public SortedKeyMapping( int i, String description, String category,
                             KeyModifier modifier, InputConstants.Type type, int keyCode ) {
        this( i, description, category, modifier, type.getOrCreate( keyCode ) );
    }
    
    /**
     * Sorted key binding with a default key + modifier, with universal conflict context.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     * @param modifier    Default modifier key.
     * @param key         Default key.
     */
    public SortedKeyMapping( int i, String description, String category,
                             KeyModifier modifier, InputConstants.Key key ) {
        super( description, KeyConflictContext.UNIVERSAL, modifier, key, category );
        index = i;
    }
    
    
    /** Sets the conflict context for this key mapping and returns itself for ease of constructing. */
    public SortedKeyMapping guiOnly() { return withConflictContext( KeyConflictContext.GUI ); }
    
    /** Sets the conflict context for this key mapping and returns itself for ease of constructing. */
    public SortedKeyMapping inGameOnly() { return withConflictContext( KeyConflictContext.IN_GAME ); }
    
    /** Sets the conflict context for this key mapping and returns itself for ease of constructing. */
    public SortedKeyMapping withConflictContext( IKeyConflictContext conflictContext ) {
        setKeyConflictContext( conflictContext );
        return this;
    }
    
    
    // ---- Implementation ---- //
    
    /** The index to sort by. */
    private final int index;
    
    @Override
    public int compareTo( KeyMapping other ) {
        // Let the super sort categories
        if( !getCategory().equals( other.getCategory() ) ) return super.compareTo( other );
        // Sort by index (lowest to highest), then alphabetically
        int compare = Integer.compare( index, ((SortedKeyMapping) other).index );
        return compare == 0 ? I18n.get( getName() ).compareTo( I18n.get( other.getName() ) ) : compare;
    }
}