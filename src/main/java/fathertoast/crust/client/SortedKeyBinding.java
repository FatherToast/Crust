package fathertoast.crust.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class SortedKeyBinding extends KeyMapping {
    
    private final int index;
    
    public SortedKeyBinding(int i, String description, KeyConflictContext conflictContext,
                            InputConstants.Type inputType, int keyCode, String category ) {
        super( description, conflictContext, inputType, keyCode, category );
        index = i;
    }
    
    public SortedKeyBinding( int i, String description, int keyCode, String category ) {
        super( description, keyCode, category );
        index = i;
    }
    
    public SortedKeyBinding( int i, String description, KeyConflictContext conflictContext,
                             KeyModifier modifier, InputConstants.Key keyCode, String category ) {
        super( description, conflictContext, modifier, keyCode, category );
        index = i;
    }
    
    @Override
    public int compareTo( KeyMapping other ) {
        if( !getCategory().equals( other.getCategory() ) ) return super.compareTo( other );
        return Integer.compare( index, ((SortedKeyBinding) other).index );
    }
}