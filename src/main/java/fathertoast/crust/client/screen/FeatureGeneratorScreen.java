package fathertoast.crust.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.client.util.GuiUtil;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.api.util.ResourceLocationUtils;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO - Maybe utilize ResourceKeyArgument or a similar implementation
//        to show the user a suggestion list of feature IDs or tags.

/** Screen that allows the player to interact with a Feature Generator block entity's settings. */
public class FeatureGeneratorScreen extends Screen {
    
    /** Default GUI text color. */
    private static final int DEFAULT_TEXT_COLOR = 10526880;
    /** Default edit box text color. */
    private static final int DEFAULT_EDIT_COLOR = 14737632;
    
    /** The {@link FeatureGeneratorBlockEntity} we are editing data for. */
    private final FeatureGeneratorBlockEntity featureGenerator;
    /** The initial feature ID / tag key value. */
    private final String originalFeatureId;
    /** The initial fallback feature ID. */
    private final String originalFallbackId;
    /** The initial "turns into" block state. */
    private final String originalTurnsInto;
    /** The initial Y-offset. */
    private final String originalYOffset;
    /** The initial generation chance. */
    private final String originalChance;
    /** The initial "force generation" flag value. */
    private final String originalForceGen;
    
    /** Edit box for feature ID and/or feature tag key. */
    private EditBox featureEdit;
    /** Edit box for fallback feature ID. */
    private EditBox fallbackEdit;
    /** Edit box the block state the feature generator turns into. */
    private EditBox turnsIntoEdit;
    /** Edit box for the Y-offset of the generation position. */
    private EditBox yOffsetEdit;
    /** Edit box for the generation chance. */
    private EditBox chanceEdit;
    
    /** The done button! Wild technology. */
    private Button doneButton;
    /** Toggles the "force gen" flag. */
    private Button toggleForceGenButton;
    
    /** True if the "force generation" flag should be enabled. */
    boolean forceGeneration;
    
    
    public FeatureGeneratorScreen( FeatureGeneratorBlockEntity featureGen ) {
        super( GameNarrator.NO_TITLE );
        featureGenerator = featureGen;
        
        final FeatureGeneratorBlockEntity.FeatureData data = featureGen.getData();
        
        originalFeatureId = featureStringFromData( data );
        originalFallbackId = fallbackStringFromData( data );
        originalTurnsInto = stateStringFromData( data );
        originalYOffset = yOffsetFromData( data );
        originalChance = chanceFromData( data );
        originalForceGen = forceGenFromData( data );
        
        forceGeneration = data.forceGeneration();
    }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        // Feature ID / tag edit
        featureEdit = new EditBox( font, width / 2 - 152, 45, 300, 20,
                Component.empty() );
        featureEdit.setMaxLength( 128 );
        featureEdit.setValue( originalFeatureId );
        featureEdit.setResponder( responderFor( featureEdit, FeatureGeneratorScreen::isFeatureValid ) );
        addRenderableWidget( featureEdit );
        
        // Fallback feature ID
        fallbackEdit = new EditBox( font, width / 2 - 152, 85, 300, 20,
                Component.empty() );
        fallbackEdit.setMaxLength( 128 );
        fallbackEdit.setValue( originalFallbackId );
        fallbackEdit.setResponder( responderFor( fallbackEdit, FeatureGeneratorScreen::isFallbackValid ) );
        addRenderableWidget( fallbackEdit );
        
        // Result state edit
        turnsIntoEdit = new EditBox( font, width / 2 - 152, 125, 300, 20,
                Component.empty() );
        turnsIntoEdit.setMaxLength( 256 );
        turnsIntoEdit.setValue( originalTurnsInto );
        turnsIntoEdit.setResponder( responderFor( turnsIntoEdit, FeatureGeneratorScreen::isTargetStateValid ) );
        addRenderableWidget( turnsIntoEdit );
        
        // Y-offset edit
        yOffsetEdit = new EditBox( font, width / 2 - 152, 165, 95, 20,
                Component.empty() );
        yOffsetEdit.setMaxLength( 5 );
        yOffsetEdit.setValue( originalYOffset );
        yOffsetEdit.setResponder( responderFor( yOffsetEdit, FeatureGeneratorScreen::isYOffsetValid ) );
        addRenderableWidget( yOffsetEdit );
        
        // Y-offset edit
        chanceEdit = new EditBox( font, width / 2 - 48, 165, 95, 20,
                Component.empty() );
        chanceEdit.setMaxLength( 10 );
        chanceEdit.setValue( originalChance );
        chanceEdit.setResponder( responderFor( chanceEdit, FeatureGeneratorScreen::isChanceValid ) );
        addRenderableWidget( chanceEdit );
        
        // Toggle "force generation" button
        toggleForceGenButton = new Button( width / 2 + 54, 164, 95, 22,
                Component.translatable( "menu.crust.feature_generator.button.force_generation", forceGeneration ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF ),
                ( button ) -> onToggleForceGen(), Supplier::get ) {
            @Override
            protected ClientTooltipPositioner createTooltipPositioner() {
                return GuiUtil.getOrForMenu( this, GuiUtil.TooltipPositioner.CENTERED_X );
            }
        };
        toggleForceGenButton.setTooltip( Tooltip.create( Component.translatable( "menu.crust.feature_generator.button.force_generation.tooltip" ) ) );
        toggleForceGenButton.setTooltipDelay( 750 );
        addRenderableWidget( toggleForceGenButton );
        
        // Done button
        doneButton = new Button.Builder( CommonComponents.GUI_DONE, ( button ) -> onDone() )
                .pos( width / 2 - 154, 210 )
                .size( 150, 20 )
                .build();
        doneButton.active = false;
        addRenderableWidget( doneButton );
        
        // Cancel button
        addRenderableWidget( new Button.Builder( CommonComponents.GUI_CANCEL, ( button ) -> onClose() )
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
    
    /** Called when the "force generation" button is pressed. */
    private void onToggleForceGen() {
        forceGeneration = !forceGeneration;
        toggleForceGenButton.setMessage(
                Component.translatable( "menu.crust.feature_generator.button.force_generation", forceGeneration
                        ? CommonComponents.OPTION_ON
                        : CommonComponents.OPTION_OFF ) );
        checkCanSend();
    }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        featureEdit.tick();
        fallbackEdit.tick();
        turnsIntoEdit.tick();
        yOffsetEdit.tick();
        chanceEdit.tick();
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() {
        // noinspection ConstantConditions
        minecraft.setScreen( null );
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
        
        // Draw field names
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.feature" ), width / 2 - 152, 33, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.fallback" ), width / 2 - 152, 73, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.turns_into" ), width / 2 - 152, 113, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.y_offset" ), width / 2 - 152, 153, DEFAULT_TEXT_COLOR );
        guiGraphics.drawString( font, Component.translatable( "menu.crust.feature_generator.edit_box.chance" ), width / 2 - 48, 153, DEFAULT_TEXT_COLOR );
        
        // Render edit boxes
        featureEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        fallbackEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        turnsIntoEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        yOffsetEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        chanceEdit.render( guiGraphics, mouseX, mouseY, partialTicks );
        
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
        else if( data.getTagKey() != null )
            value = "#" + data.getTagKey().location();
        return value;
    }
    
    /**
     * @return A String representing the "fallback" feature ID of the given FeatureData instance.
     * If null, an empty String is returned instead.
     */
    private static String fallbackStringFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        if( !ResourceLocationUtils.isEmpty( data.getFallbackId() ) )
            return data.getFallbackId().toString();
        return "";
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
    
    /** @return A String representing the "generation chance" of the given FeatureData instance. */
    private static String chanceFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        return String.valueOf( data.getChance() );
    }
    
    /** @return A String representing the "force generation" flag of the given FeatureData instance. */
    private static String forceGenFromData( FeatureGeneratorBlockEntity.FeatureData data ) {
        return String.valueOf( data.forceGeneration() );
    }
    
    /**
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#featureEdit} edit box.
     */
    private static boolean isFeatureValid( String value ) {
        if( value.isEmpty() ) return false;
        // Allow '#' as starting character, indicates a tag key.
        value = value.startsWith( "#" ) ? value.substring( 1 ) : value;
        return ResourceLocationUtils.strictIsValid( value );
    }
    
    /**
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#fallbackEdit} edit box.
     */
    private static boolean isFallbackValid( String value ) {
        // Allow empty
        if( value.isEmpty() ) return true;
        return ResourceLocationUtils.strictIsValid( value );
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
     * @return True if the given String is considered a valid value
     * for the {@link FeatureGeneratorScreen#chanceEdit} edit box.
     */
    private static boolean isChanceValid( String value ) {
        try {
            double chance = Double.parseDouble( value );
            return chance >= 0.0 && chance <= 1.0;
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
                        || !fallbackEdit.getValue().equals( originalFallbackId )
                        || !turnsIntoEdit.getValue().equals( originalTurnsInto )
                        || !yOffsetEdit.getValue().equals( originalYOffset )
                        || !chanceEdit.getValue().equals( originalChance )
                        || !String.valueOf( forceGeneration ).equals( originalForceGen )
        );
    }
    
    /** Parses current data and sends it to the server. */
    @SuppressWarnings( "ConstantConditions" )
    private void sendToServer() {
        if( minecraft.player != null ) {
            final String featureOrTagId = featureEdit.getValue();
            final String fallbackId = fallbackEdit.getValue();
            final String turnsInto = turnsIntoEdit.getValue();
            final String yOffset = yOffsetEdit.getValue();
            final String chance = chanceEdit.getValue();
            
            FeatureGeneratorBlockEntity.FeatureData newData = new FeatureGeneratorBlockEntity.FeatureData(
                    featureOrTagId.startsWith( "#" )
                            ? null
                            : ResourceLocation.parse( featureOrTagId ),
                    featureOrTagId.startsWith( "#" )
                            ? TagKey.create( Registries.CONFIGURED_FEATURE, ResourceLocation.parse( featureOrTagId.substring( 1 ) ) )
                            : null,
                    ResourceLocationUtils.parseOrDefault( fallbackId, ResourceLocationUtils.EMPTY ),
                    BlockStatePropertyMap.strictStateFrom( turnsInto ),
                    Integer.parseInt( yOffset ),
                    Double.parseDouble( chance ),
                    forceGeneration
            );
            featureGenerator.setData( newData );
            CrustPacketHandler.sendFeatureGeneratorData( featureGenerator );
        }
    }
}
