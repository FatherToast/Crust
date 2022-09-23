package fathertoast.crust.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class SortedKeyBinding extends KeyBinding {
    
    private final int index;
    
    public SortedKeyBinding( int i, String description, KeyConflictContext conflictContext,
                             InputMappings.Type inputType, int keyCode, String category ) {
        super( description, conflictContext, inputType, keyCode, category );
        index = i;
    }
    
    public SortedKeyBinding( int i, String description, int keyCode, String category ) {
        super( description, keyCode, category );
        index = i;
    }
    
    public SortedKeyBinding( int i, String description, KeyConflictContext conflictContext,
                             KeyModifier modifier, InputMappings.Input keyCode, String category ) {
        super( description, conflictContext, modifier, keyCode, category );
        index = i;
    }
    
    @Override
    public int compareTo( KeyBinding other ) {
        if( !getCategory().equals( other.getCategory() ) ) return super.compareTo( other );
        return Integer.compare( index, ((SortedKeyBinding) other).index );
    }
}