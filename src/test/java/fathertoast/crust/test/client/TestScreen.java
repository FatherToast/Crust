package fathertoast.crust.test.client;

import fathertoast.crust.api.client.util.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/** A test screen for messing around with widgets and other GUI components. */
public class TestScreen extends Screen {
    
    /** The screen open under this one. */
    private final Screen LAST_SCREEN;
    
    
    /** Creates a new test screen, with the provided last screen. */
    protected TestScreen( Screen lastScreen ) {
        super( Component.literal( "Test Screen" ) );
        LAST_SCREEN = lastScreen;
    }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        super.init();
        
        // Upper left-corner button
        Button button1 = simpleTestButton( 20, 20, 20, 60, "1",
                "Tooltip #1! GuiUtil#CENTERED_X type, " +
                        "should stay inside the screen with ATTEMPTED centered X position, either below or above the cursor!",
                GuiUtil.TooltipPositioner.CENTERED_X );
        
        // Middle button
        Button button2 = simpleTestButton( (width / 2) - 10, (height / 2) - 10, 20, 20, "2",
                "Tooltip #2! GuiUtil#CENTERED_X type, " +
                        "should stay inside the screen WITH centered X position, above the cursor!",
                GuiUtil.TooltipPositioner.CENTERED_X );
        
        // Middle-right button
        Button button3 = simpleTestButton( width - 40, (height / 2) - 10, 20, 20, "3",
                "Tooltip #3! GuiUtil#CENTERED_Y type, " +
                        "should stay inside the screen WITH centered Y position, to the LEFT of the cursor!",
                GuiUtil.TooltipPositioner.CENTERED_Y );
        
        // Lower left-corner button
        Button button4 = simpleTestButton( 20, height - 40, 20, 30, "4",
                "Tooltip #4! GuiUtil#CENTERED_Y type, " +
                        "should stay inside the screen with ATTEMPTED centered Y position, to the RIGHT of the cursor!",
                GuiUtil.TooltipPositioner.CENTERED_Y );
        
        addRenderableWidget( button1 );
        addRenderableWidget( button2 );
        addRenderableWidget( button3 );
        addRenderableWidget( button4 );
        
        // noinspection ConstantConditions
        addRenderableWidget( new Button( width / 2 - 150 / 2, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                ( button ) -> minecraft.setScreen( LAST_SCREEN ), Supplier::get ) );
    }
    
    /** Creates a simple 20x20 button that does nothing when clicked, but displays a tooltip. */
    @SuppressWarnings( "SameParameterValue" )
    private static Button simpleTestButton( int x, int y, int width, int height, String label, String tooltip, ClientTooltipPositioner positioner ) {
        Button button = new Button( x, y, width, height, Component.literal( label ), b -> { }, Supplier::get ) {
            @Override
            protected ClientTooltipPositioner createTooltipPositioner() {
                return GuiUtil.getOrForMenu( this, positioner );
            }
        };
        button.setTooltip( Tooltip.create( Component.literal( tooltip ) ) );
        return button;
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        
        graphics.drawCenteredString( font, getTitle(), width / 2, height / 10, 0xFFFFFF );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
}
