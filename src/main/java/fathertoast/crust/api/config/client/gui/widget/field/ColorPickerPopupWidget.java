package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.provider.ColorFieldWidgetProvider;
import fathertoast.crust.api.config.common.field.ColorIntField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.lib.CrustMath;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

import java.util.function.Supplier;

import static fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider.DEFAULT_COLOR;
import static fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider.INVALID_COLOR;

// TODO - Land on a proper design and fix the sliders lol
public class ColorPickerPopupWidget extends Screen {//TODO migrate to popup widget
    
    /** The providing field. */
    private final ColorIntField FIELD;
    /** The field component (widget "row" from previous screen's selection list). */
    private final ConfigFieldGuiEntry<?> LIST_ENTRY;
    
    /** An edit box that displays the current color as a hexadecimal color int. Can also be modified. */
    private EditBox colorHexEditBox;
    /** A color preview widget that displays the current color. */
    private ColorPreviewWidget colorPreviewWidget;
    /** The slider widget for modifying the alpha value of the color. */
    private ForgeSlider aSlider;
    /** The slider widget for modifying the red value of the color. */
    private ForgeSlider rSlider;
    /** The slider widget for modifying the green value of the color. */
    private ForgeSlider gSlider;
    /** The slider widget for modifying the blue value of the color. */
    private ForgeSlider bSlider;
    
    /** The "open file" or "discard changes" button. */
    private Button bottomLeftButton;
    /** The "done" or "save changes" button. */
    private Button bottomRightButton;
    
    
    public ColorPickerPopupWidget( ConfigFieldGuiEntry<?> listEntry, ColorIntField field ) {
        super( title( listEntry ) );
        FIELD = field;
        LIST_ENTRY = listEntry;
    }
    
    /** @return The given string field's key as a more easily readable title. */
    private static Component title( ConfigFieldGuiEntry<?> listEntry ) {
        String name = null;
        //        String name = CrustConfigFileScreen.decodeString(
        //                listEntry.getField().getKey().startsWith( listEntry.getField().getSpec().loadingCategory ) ?
        //                        listEntry.getField().getKey().substring( listEntry.getField().getSpec().loadingCategory.length() ) :
        //                        listEntry.getField().getKey() );
        
        return Component.literal( name );
    }
    
    /** Closes this screen and reopens it to hard-refresh everything. */
    public void resetScreen() {
        if( minecraft != null ) {
            ColorPickerPopupWidget newScreen = new ColorPickerPopupWidget( LIST_ENTRY, FIELD );
            minecraft.setScreen( newScreen );
        }
    }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        final int originalColor = FIELD.get();
        
        // Preview widget
        colorPreviewWidget = new ColorPreviewWidget( 230, 105, 70 );
        colorPreviewWidget.setColor( originalColor, FIELD.usesAlpha() );
        
        // Color edit box
        colorHexEditBox = new EditBox( font, 231, 80, 67, 20, Component.literal( FIELD.getKey() ) );
        colorHexEditBox.setMaxLength( FIELD.getMinDigits() );
        colorHexEditBox.setValue( TomlHelper.toLiteral( FIELD.wrap( originalColor ) ).substring( 2 ) );
        colorHexEditBox.setResponder( ( value ) -> {
            Integer newValue = TomlHelper.parseHexInt( value );
            if( newValue == null || !ColorFieldWidgetProvider.isValid( FIELD, newValue ) ) {
                updateSliders( 0 );
                colorHexEditBox.setTextColor( INVALID_COLOR );
            }
            else {
                updateSliders( newValue );
                colorHexEditBox.setTextColor( DEFAULT_COLOR );
            }
        } );
        
        // Color channel sliders
        aSlider = makeSlider( this, 40, 80, Component.literal( "Alpha " ), CrustMath.getAlphaBits( originalColor ) );
        rSlider = makeSlider( this, 40, 105, Component.literal( "Red " ), CrustMath.getRedBits( originalColor ) );
        gSlider = makeSlider( this, 40, 130, Component.literal( "Green " ), CrustMath.getGreenBits( originalColor ) );
        bSlider = makeSlider( this, 40, 155, Component.literal( "Blue " ), CrustMath.getBlueBits( originalColor ) );
        
        addRenderableWidget( colorHexEditBox );
        addRenderableWidget( colorPreviewWidget );
        addRenderableWidget( aSlider );
        addRenderableWidget( rSlider );
        addRenderableWidget( gSlider );
        addRenderableWidget( bSlider );
        
        aSlider.active = FIELD.usesAlpha();
        
        // Footer content
        addRenderableWidget( bottomLeftButton = new Button( width / 2 - 155, height - 29,
                150, 20, Component.translatable( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( isChanged() ) resetScreen();
                    else Util.getPlatform().openFile( LIST_ENTRY.getField().getSpec().getFile().getParentFile() );
                },
                Supplier::get ) );
        
        addRenderableWidget( bottomRightButton = new Button( width / 2 - 155 + 160, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                ( button ) -> {
                    if( isChanged() ) {
                        resetScreen();
                    }
                    else {
                        //                        minecraft.setScreen( LAST_SCREEN );
                        //                        LAST_SCREEN.setScrollAmount( LAST_SCROLL );
                    }
                },
                Supplier::get ) );
    }
    
    /** Helper method for making slider widgets for each color channel. */
    @SuppressWarnings( "SameParameterValue" )
    private static ForgeSlider makeSlider( ColorPickerPopupWidget screen, int x, int y, Component prefix, int currentValue ) {
        return new ForgeSlider( x, y, 150, 20, prefix, Component.literal( "" ),
                0, 255, currentValue, true ) {
            @Override
            protected void applyValue() {
                super.applyValue();
                screen.colorHexEditBox.setValue( TomlHelper.toLiteral( screen.FIELD.wrap( screen.getCurrentValue() ) ).substring( 2 ) );
            }
        };
    }
    
    /** @return The current color int that is the result of each slider's value. */
    private int getCurrentValue() {
        return CrustMath.bitsToARGB( aSlider.getValueInt(), rSlider.getValueInt(), gSlider.getValueInt(), bSlider.getValueInt() );
    }
    
    /**
     * @return True if the current color value of
     * the ARGB sliders are different from the initial ARGB value.
     */
    private boolean isChanged() {
        return getCurrentValue() != FIELD.get();
    }
    
    /** Modifies the slider's values using the current color value of {@link ColorPickerPopupWidget#colorHexEditBox}. */
    public void updateSliders( int color ) {
        aSlider.setValue( CrustMath.getAlphaBits( color ) );
        rSlider.setValue( CrustMath.getRedBits( color ) );
        gSlider.setValue( CrustMath.getGreenBits( color ) );
        bSlider.setValue( CrustMath.getBlueBits( color ) );
    }
    
    /** Called when the footer text might need to be changed. */
    public void updateFooterButtonText() {
        if( isChanged() ) {
            bottomLeftButton.setMessage( Component.translatable( "menu.crust.config.discard_changes" )
                    .withStyle( ChatFormatting.RED ) );
            //            bottomRightButton.setMessage( Component.translatable( "menu.crust.config.save_changes" )
            //                    .withStyle( ChatFormatting.GREEN ) );
            bottomRightButton.setMessage( Component.translatable( "menu.crust.config.confirm_changes" )
                    .withStyle( ChatFormatting.AQUA ) );
        }
        else {
            bottomLeftButton.setMessage( Component.translatable( "menu.crust.config.open_folder" ) );
            bottomRightButton.setMessage( CommonComponents.GUI_DONE );
        }
    }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        colorHexEditBox.tick();
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        // Update color preview
        colorPreviewWidget.setColor(
                CrustMath.toARGB( aSlider.getValueInt(), rSlider.getValueInt(), gSlider.getValueInt(), bSlider.getValueInt() ),
                FIELD.usesAlpha()
        );
        renderMain( graphics, mouseX, mouseY, partialTicks );
    }
    
    /** Called to render the primary screen content. */
    protected void renderMain( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        
        graphics.drawCenteredString( font, title, width / 2, height / 12, 0xFFFFFF );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
}