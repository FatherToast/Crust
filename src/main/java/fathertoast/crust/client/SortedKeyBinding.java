package fathertoast.crust.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
//import net.minecraftforge.client.settings.KeyConflictContext;
//import net.minecraftforge.client.settings.KeyModifier;

/**
 * A key binding with specified sorting order.
 */
public class SortedKeyBinding extends KeyBinding {
    
    /** The index to sort by. */
    private final int index;
    
    /**
     * Sorted key binding that is unbound by default.
     *
     * @param i           Sort index, lower puts it higher in the key bind list.
     * @param description Localization code for the key bind name/description.
     * @param category    Localization code for the key bind category.
     */
    public SortedKeyBinding( int i, String description, String category ) {
        super( description, InputMappings.UNKNOWN.getValue(), category );
        index = i;
    }
    
    //    /**
    //     * Sorted key binding with a default key.
    //     *
    //     * @param i           Sort index, lower puts it higher in the key bind list.
    //     * @param description Localization code for the key bind name/description.
    //     * @param keyCode     Default key.
    //     * @param category    Localization code for the key bind category.
    //     */
    //    public SortedKeyBinding( int i, String description, int keyCode, String category ) {
    //        super( description, keyCode, category );
    //        index = i;
    //    }
    
    //    /**
    //     * Sorted key binding with a default key + modifier.
    //     *
    //     * @param i               Sort index, lower puts it higher in the key bind list.
    //     * @param description     Localization code for the key bind name/description.
    //     * @param conflictContext The context for identifying key conflicts.
    //     * @param modifier        Default modifier key.
    //     * @param keyCode         Default key.
    //     * @param category        Localization code for the key bind category.
    //     */
    //    public SortedKeyBinding( int i, String description, KeyConflictContext conflictContext,
    //                             KeyModifier modifier, InputMappings.Input keyCode, String category ) {
    //        super( description, conflictContext, modifier, keyCode, category );
    //        index = i;
    //    }
    
    @Override
    public int compareTo( KeyBinding other ) {
        if( !getCategory().equals( other.getCategory() ) ) return super.compareTo( other );
        return Integer.compare( index, ((SortedKeyBinding) other).index ); // Sort by index, lowest to highest
    }
}