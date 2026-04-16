package fathertoast.crust.client.screen;

import fathertoast.crust.api.config.common.AbstractConfigFile;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

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
    /** The list of config files that are considered "broken". */
    private final List<AbstractConfigFile> brokenConfigs;
    
    
    public BrokenConfigsScreen( Runnable callback, List<AbstractConfigFile> brokenConfigs ) {
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
        ConfigList list = new ConfigList( minecraft, width + 45, height, topY, bottomY, 20 );
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
    
    /**
     * A simple selection list implementation that contains entries
     * that display a config path and a button to open said config on the system.
     */
    public static class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {
        
        private int maxNameWidth;
        
        public ConfigList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight ) {
            super( minecraft, width, height, topY, bottomY, itemHeight );
        }
        
        /** Adds the given config as an entry. */
        public void add( AbstractConfigFile config ) {
            addEntry( new Entry( this, config ) );
        }
        
        protected static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            
            /** The {@link ConfigList} this entry belongs to. */
            final ConfigList PARENT;
            /** The component to display. */
            final Component VALUE;
            /** The button that opens the config file on the system. */
            final AbstractWidget OPEN_BUTTON;
            /** The width of the value String, as determined by the used font. */
            final int WIDTH;
            
            /** List of subcomponents in this entry. */
            final List<AbstractWidget> CHILDREN;
            
            
            private Entry( ConfigList parent, AbstractConfigFile config ) {
                PARENT = parent;
                VALUE = Component.literal( config.SPEC.getFilePath() ).withStyle( ChatFormatting.GRAY );
                WIDTH = parent.minecraft.font.width( VALUE );
                OPEN_BUTTON = new Button( 0, 0, 20, 20,
                        Component.literal( ">" ),
                        ( button ) -> Util.getPlatform().openFile( config.SPEC.getFile() ), Supplier::get );
                
                CHILDREN = List.of( OPEN_BUTTON );
                
                // Update the list's max name width
                if( WIDTH > PARENT.maxNameWidth ) PARENT.maxNameWidth = WIDTH;
            }
            
            @Override
            public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                                int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
                // noinspection ConstantConditions
                graphics.drawString( PARENT.minecraft.font, VALUE,
                        PARENT.minecraft.screen.width - WIDTH >> 1,
                        rowTop + rowHeight - 9 - 1, 0xFFFFFF );
                
                OPEN_BUTTON.setX( (PARENT.minecraft.screen.width + PARENT.maxNameWidth + 60 >> 1) - 20 );
                OPEN_BUTTON.setY( rowTop );
                OPEN_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
            }
            
            @Override
            public List<? extends GuiEventListener> children() {
                return CHILDREN;
            }
            
            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of();
            }
        }
    }
}
