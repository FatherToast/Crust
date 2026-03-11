package fathertoast.crust.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Consumer;
import java.util.function.Predicate;

// TODO - Maybe utilize ResourceKeyArgument or a similar implementation
//        to show the user a suggestion list of feature IDs or tags.
public class FeatureGeneratorScreen extends Screen {
    
    /** Default GUI text color. */
    private static final int DEFAULT_TEXT_COLOR = 10526880;
    /** Default edit box text color. */
    private static final int DEFAULT_EDIT_COLOR = 14737632;
    
    /** The {@link FeatureGeneratorBlockEntity} we are editing data for. */
    private final FeatureGeneratorBlockEntity featureGenerator;
    /** The initial feature ID / tag key value. */
    private final String originalFeatureId;
    /** The initial "turns into" block state. */
    private final String originalTurnsInto;
    /** The initial Y-offset. */
    private final String originalYOffset;
    
    /** Edit box for feature ID and/or feature tag key. */
    private EditBox featureEdit;
    /** Edit box the block state the feature generator turns into. */
    private EditBox turnsIntoEdit;
    /** Edit box for the Y-offset of the generation position. */
    private EditBox yOffsetEdit;
    
    /** The done button! Wild technology. */
    private Button doneButton;
    
    
    public FeatureGeneratorScreen( FeatureGeneratorBlockEntity featureGenerator ) {
        super( GameNarrator.NO_TITLE );
        this.featureGenerator = featureGenerator;
        
        final FeatureGeneratorBlockEntity.FeatureData data = featureGenerator.getData();
        
        this.originalFeatureId = featureStringFromData( data );
        this.originalTurnsInto = stateStringFromData( data );
        this.originalYOffset = yOffsetFromData( data );
    }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        // Feature ID / tag edit
        featureEdit = new EditBox( font, width / 2 - 152, 85, 300, 20,
                Component.empty() );
        featureEdit.setMaxLength( 128 );
        featureEdit.setValue( originalFeatureId );
        featureEdit.setResponder( responderFor( featureEdit, FeatureGeneratorScreen::isFeatureValid ) );
        addRenderableWidget( featureEdit );
        
        // Result state edit
        turnsIntoEdit = new EditBox( font, width / 2 - 152, 125, 300, 20,
                Component.empty() );
        turnsIntoEdit.setMaxLength( 256 );
        turnsIntoEdit.setValue( originalTurnsInto );
        turnsIntoEdit.setResponder( responderFor( turnsIntoEdit, FeatureGeneratorScreen::isTargetStateValid ) );
        addRenderableWidget( turnsIntoEdit );
        
        // Y-offset edit
        yOffsetEdit = new EditBox( font, width / 2 - 152, 165, 100, 20,
                Component.empty() );
        yOffsetEdit.setMaxLength( 5 );
        yOffsetEdit.setValue( originalYOffset );
        yOffsetEdit.setResponder( responderFor( yOffsetEdit, FeatureGeneratorScreen::isYOffsetValid ) );
        addRenderableWidget( yOffsetEdit );
        
        // Done button
        doneButton = new Button.Builder( CommonComponents.GUI_DONE, ( button ) -> onDone() )
                .pos( width / 2 - 154, 210 )
                .size( 150, 20 )
                .build();
        doneButton.active = false;
        
        addRenderableWidget( doneButton );
        
        // Cancel button
        addRenderableWidget( new Button.Builder( CommonComponents.GUI_CANCEL, ( button ) -> onCancel() )
                .pos( width / 2 + 4, 210 )
                .size( 150, 20 )
                .build() );
    }
    
    /** Called when the "Done" button is pressed. */
    private void onDone() {
        sendToServer();
        // noinspection ConstantConditions
        minecraft.setScreen( null );
    }
    
    /** Called when the "Cancel" button is pressed. */
    private void onCancel() {
        // noinspection ConstantConditions
        minecraft.setScreen( null );
    }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        featureEdit.tick();
        turnsIntoEdit.tick();
        yOffsetEdit.tick();
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() {
        this.onCancel();
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
        if( super.keyPressed( key, scancode, mods ) ) {
            return true;
        }
        else if( !doneButton.active
                || key != InputConstants.getKey( "key.keyboard.enter" ).getValue()
                && key != InputConstants.getKey( "key.keyboard.keypad.enter" ).getValue() ) {
            return false;
        }
        else {
            onDone();
            return true;
        }
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( guiGraphics );
        
        // Draw "title"
        guiGraphics.drawCenteredString( font, Component.translatable( CrustObjects.Blocks.FEATURE_GENERATOR.get().getDescriptionId() ),
                width / 2, (height / 2) - 90, DEFAULT_TEXT_COLOR );
        
        // Draw field names
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.feature" ), width / 2 - 152, (height / 2) - 53, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.turns_into" ), width / 2 - 152, (height / 2) - 13, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.y_offset" ), width / 2 - 152, (height / 2) + 27, DEFAULT_TEXT_COLOR );
        
        featureEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        turnsIntoEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        yOffsetEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        
        super.render( guiGraphics, mouseX, mouseY, partialTicks );
    }
    
    
    //
    // --------------------- Parsing and validation ---------------------
    //
    
    /**
     * @return A String representing either the feature ID or feature tag
     * of the given FeatureData instance, depending on which value is present.
     * <br><br>
     * If both feature ID and tag key are present, feature ID takes priority.
     */
    private static String featureStringFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        String value = "";
        
        if( data.getConfiguredFeatureId() != null )
            value = data.getConfiguredFeatureId().toString();
        else if( data.getTag() != null )
            value = "#" + data.getTag().location();
        return value;
    }
    
    /**
     * @return A String representing the "turns into" block state of the
     * given FeatureData instance.
     */
    private static String stateStringFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        return BlockStateKey.of( data.getTurnsInto(), false ).toString();
    }
    
    /** @return A String representing the "y-offset" of the given FeatureData instance. */
    private static String yOffsetFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        return String.valueOf( data.getYOffset() );
    }
    
    /**
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#featureEdit} edit box.
     */
    private static boolean isFeatureValid( String value ) {
        if( value.isEmpty() ) return false;
        // Allow '#' as starting character, indicates a tag key.
        value = value.startsWith( "#" ) ? value.substring( 1 ) : value;
        return ResourceLocation.isValidResourceLocation( value );
    }
    
    /**
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#turnsIntoEdit} edit box.
     */
    private static boolean isTargetStateValid( String value ) {
        return BlockStatePropertyMap.strictStateFrom( value ) != null;
    }
    
    /**
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#yOffsetEdit} edit box.
     */
    private static boolean isYOffsetValid( String value ) {
        try {
            int offset = Integer.parseInt( value );
            return offset >= -999 && offset <= 999;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
    
    /**
     * Convenience method for creating an edit box responder
     * that updates text color and the {@link FeatureGeneratorScreen#doneButton} depending on validity.
     */
    private Consumer<String> responderFor( EditBox editBox, Predicate<String> validator ) {
        return ( value ) -> {
            // noinspection DataFlowIssue
            editBox.setTextColor( validator.test( value ) ? DEFAULT_EDIT_COLOR : ChatFormatting.RED.getColor() );
            checkCanSend();
        };
    }
    
    /**
     * Checks if current data is valid AND is different from the initial data to
     * set the state of the "Done" button. If the checks don't pass, there is
     * no point allowing the user to send a packet to the server.
     */
    private void checkCanSend() {
        boolean areValuesValid =
                isFeatureValid( featureEdit.getValue() )
                        && isTargetStateValid( turnsIntoEdit.getValue() )
                        && isYOffsetValid( yOffsetEdit.getValue() );
        
        doneButton.active = areValuesValid && (
                !featureEdit.getValue().equals( originalFeatureId )
                        || !turnsIntoEdit.getValue().equals( originalTurnsInto )
                        || !yOffsetEdit.getValue().equals( originalYOffset )
        );
    }
    
    /** Parses current data and sends it to the server. */
    @SuppressWarnings( "ConstantConditions" )
    private void sendToServer() {
        if( minecraft.player != null ) {
            final String featureOrTagId = featureEdit.getValue();
            final String turnsInto = turnsIntoEdit.getValue();
            final String yOffset = yOffsetEdit.getValue();
            
            FeatureGeneratorBlockEntity.FeatureData newData = new FeatureGeneratorBlockEntity.FeatureData(
                    featureOrTagId.startsWith( "#" )
                            ? null
                            : ResourceLocation.parse( featureOrTagId ),
                    featureOrTagId.startsWith( "#" )
                            ? TagKey.create( Registries.CONFIGURED_FEATURE, ResourceLocation.parse( featureOrTagId.substring( 1 ) ) )
                            : null,
                    BlockStatePropertyMap.strictStateFrom( turnsInto ),
                    Integer.parseInt( yOffset )
            );
            featureGenerator.setData( newData );
            CrustPacketHandler.sendFeatureGeneratorData( featureGenerator );
        }
    }
}
