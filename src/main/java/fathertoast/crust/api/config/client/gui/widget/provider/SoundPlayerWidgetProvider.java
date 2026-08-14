package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.client.gui.widget.field.SoundPlayerWidget;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.value.SoundData;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Displays a text box with a button to the right of it that plays
 * a sound event depending on the value of the text box. This
 * is primarily used
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class SoundPlayerWidgetProvider<T> implements IConfigFieldWidgetProvider<T> {
    
    /** A supplier that provides the sound player widget with sound data. */
    protected final Function<T, SoundData> SOUND_DATA_MAPPER;
    
    /** An optional line validator. */
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    /**
     * Constructs a new instance of this widget provider
     * with the specified value supplier, and optionally a line validator.
     *
     * @param soundDataMapper Maps a field value to the sound we need to play.
     * @param lineValidator   An optional line validator for the text box provided by this provider.
     */
    public SoundPlayerWidgetProvider( Function<T, SoundData> soundDataMapper, @Nullable Predicate<String> lineValidator ) {
        SOUND_DATA_MAPPER = soundDataMapper;
        VALIDATOR = lineValidator;
    }
    
    /**
     * Constructs a new instance of this widget provider
     * with the specified value supplier, and optionally a line validator.
     *
     * @param soundMapper   Maps a field value to the sound we need to play.
     * @param lineValidator An optional line validator for the text box provided by this provider.
     */
    public SoundPlayerWidgetProvider( Function<T, SoundEvent> soundMapper, float volume, float pitch, @Nullable Predicate<String> lineValidator ) {
        this( value -> SoundData.of( soundMapper.apply( value ), volume, pitch ), lineValidator );
    }
    
    /**
     * Called to initialize the field's gui components.
     * <p>
     * Positions of the widgets provided (x, y) are relative to the top-left corner of the "field value widget" space.
     * The space available for field value widgets is a {@link #VALUE_WIDTH} by {@link #VALUE_HEIGHT} rectangle
     * (in GUI pixels) that is right-aligned in the parent list widget.
     *
     * @param components   The list to populate with widgets.
     * @param listEntry    The field component (widget "row" within a scrollable list).
     * @param displayValue The current raw value to display in the GUI.
     */
    @Override
    public void apply( List<AbstractWidget> components, ConfigFieldGuiEntry<T> listEntry, T displayValue ) {
        SoundPlayerWidget soundPlayerWidget = new SoundPlayerWidget( SOUND_DATA_MAPPER.apply( displayValue ),
                VALUE_WIDTH - EntryViewWidget.DEFAULT_SIZE, 0, 20, 20 );
        
        EditBox editBox = new EditBox( listEntry.client().font, 1, 1,
                VALUE_WIDTH - 3 - EntryViewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( "" ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        editBox.setValue( TomlHelper.toTomlString( displayValue ) );
        editBox.setResponder( VALIDATOR == null ?
                text -> {
                    listEntry.updateInput( text );
                    soundPlayerWidget.setSoundData( SOUND_DATA_MAPPER.apply( listEntry.getValue() ) );
                } :
                text -> {
                    editBox.setTextColor( VALIDATOR.test( text ) ? DEFAULT_COLOR : INVALID_COLOR );
                    listEntry.updateInput( text );
                    soundPlayerWidget.setSoundData( SOUND_DATA_MAPPER.apply( listEntry.getValue() ) );
                } );
        editBox.active = listEntry.isEditable();
        
        components.add( soundPlayerWidget );
        components.add( editBox );
    }
}