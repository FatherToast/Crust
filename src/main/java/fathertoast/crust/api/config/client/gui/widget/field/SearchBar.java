package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/** WIP */
public class SearchBar extends EditBox {
    
    public SearchBar( Font font, int x, int y, int width, int height ) {
        super( font, x, y, width, height, Component.literal( "" ) );
        setHint( Component.translatable( "menu.crust.config.search_bar.hint" ).withStyle( ChatFormatting.ITALIC, ChatFormatting.GRAY ) );
    }
}
