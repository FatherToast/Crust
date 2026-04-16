package fathertoast.crust.client.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Screen that informs the user of any broken Crust-based configs that failed to load.
 * The user is prompted to either proceed with world loading or return to the title screen.
 *
 * @see fathertoast.crust.common.mixin_work.ClientMixinHooks
 * @see fathertoast.crust.common.mixin.WorldOpenFlowsMixin
 */
public class BrokenConfigsScreen extends Screen {
    
    /** A multi-line message explaining what the hell is happening. */
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    
    /** The callback to run when the "proceed" button is pressed. */
    private final Runnable callback;
    /** The list of config file paths to display as "broken". */
    private final List<String> brokenConfigs;
    
    
    public BrokenConfigsScreen( Runnable callback, List<String> brokenConfigs ) {
        super( Component.translatable( "menu.crust.broken_configs.title" ).withStyle( ChatFormatting.RED ) );
        this.callback = callback;
        this.brokenConfigs = brokenConfigs;
    }
    
    @Override
    protected void init() {
        super.init();
        
        message = MultiLineLabel.create( font, Component.translatable( "menu.crust.broken_configs.message" ), width - 50 );
        
        Button loadLevelButton = Button.builder( Component.translatable( "menu.crust.broken_configs.proceed" ),
                        button -> callback.run() )
                .bounds( width / 2 - 155, height - 35, 150, 20 )
                .build();
        
        // noinspection ConstantConditions
        Button titleScreenButton = Button.builder( CommonComponents.GUI_TO_TITLE,
                        button -> minecraft.setScreen( null ) )
                .bounds( width / 2 - 155 + 160, height - 35, 150, 20 )
                .build();
        
        addRenderableWidget( createDisplayList() );
        addRenderableWidget( loadLevelButton );
        addRenderableWidget( titleScreenButton );
    }
    
    /** Creates the scrollable list widget that shows all broken configs. */
    private AbstractSelectionList<?> createDisplayList() {
        final int topY = height / 8 + 55;
        final int bottomY = height - 50;
        // noinspection ConstantConditions
        StringList list = new StringList( minecraft, width, 60, topY, bottomY, 18 );
        brokenConfigs.forEach( list::add );
        return list;
    }
    
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        graphics.drawCenteredString( font, title, width / 2, height / 10, 0xFFFFFF );
        message.renderCentered( graphics, width / 2, height / 10 + 25 );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
    
    /** Overridden to deny closing the screen by pressing "esc". */
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    /** A simple selection list implementation that displays string entries. */
    public static class StringList extends ContainerObjectSelectionList<StringList.Entry> {
        
        public StringList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight ) {
            super( minecraft, width, height, topY, bottomY, itemHeight );
            centerListVertically = true;
        }
        
        /** Adds the given string as an entry. */
        public void add( String entry ) {
            addEntry( new Entry( this, entry ) );
        }
        
        protected static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            
            /**
             * Only here for convenience so we don't have to return a new empty list
             * object every time {@link Entry#children()} or {@link Entry#narratables()} is called.
             */
            private static final List<AbstractWidget> emptyList = ImmutableList.of();
            
            /** The {@link StringList} this entry belongs to. */
            final StringList parent;
            /** The component to display. */
            final Component value;
            /** The width of the value String, as determined by the used font. */
            final int width;
            
            
            private Entry( StringList parent, String value ) {
                this.parent = parent;
                this.value = Component.literal( value ).withStyle( ChatFormatting.GRAY );
                this.width = parent.minecraft.font.width( value );
            }
            
            @Override
            public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                                int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
                // noinspection ConstantConditions
                graphics.drawString( parent.minecraft.font, value,
                        parent.minecraft.screen.width - width >> 1,
                        rowTop + rowHeight - 9 - 1, 0xFFFFFF );
            }
            
            @Override
            public List<? extends GuiEventListener> children() {
                // No children.
                return emptyList;
            }
            
            @Override
            public List<? extends NarratableEntry> narratables() {
                // No narratables.
                return emptyList;
            }
        }
    }
}
