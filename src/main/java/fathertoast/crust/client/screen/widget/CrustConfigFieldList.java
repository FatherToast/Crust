package fathertoast.crust.client.screen.widget;

import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.entry.ConfigGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.entry.FormatGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.action.ICrustConfigGuiSpec;
import fathertoast.crust.client.screen.CrustConfigFileScreen;
import fathertoast.crust.client.screen.widget.entry.FieldGuiEntry;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Widget that displays a list of all fields defined in one config spec.
 * <p>
 * The layout of this screen is largely driven by the spec itself, while
 * each field decides how it displays its own info.
 */
public class CrustConfigFieldList extends SearchableSelectionList<ConfigGuiEntry> implements ICrustConfigGuiSpec {
    
    /** The config spec this list is displaying contents for. */
    public final CrustConfigFileScreen PARENT;
    /** The config spec this list is displaying contents for. */
    public final CrustConfigSpec SPEC;
    
    /** True if this config is a remotely-hosted file. */
    private final boolean REMOTE;
    /** True if this config is editable. */
    private final boolean EDITABLE;
    
    /** True if any fields have been changed since opening. */
    private boolean changed;
    
    public CrustConfigFieldList( CrustConfigFileScreen parent, Minecraft game, CrustConfigSpec spec,
                                 boolean remote, boolean editable ) {
        super( game, parent.width, parent.height,
                43, parent.height - 32, IConfigFieldWidgetProvider.VALUE_HEIGHT + 1 );
        PARENT = parent;
        SPEC = spec;
        REMOTE = remote;
        EDITABLE = editable;
        
        // Simply pass all responsibility on to the config spec
        spec.initGui( this );
    }
    
    /** @return The width for each row in the list. */
    @Override
    public int getRowWidth() { return OVERALL_WIDTH; }
    
    /** @return The scrollbar position, when visible. */
    @Override
    protected int getScrollbarPosition() { return getRowRight() + 1 - SCROLL_WIDTH; }
    
    /** Called each frame to draw this component. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        super.render( graphics, mouseX, mouseY, partialTicks );
        
        if( isMouseOver( mouseX, mouseY ) ) {
            ConfigGuiEntry entryMouseOver = getEntryAtPosition( mouseX, mouseY );
            if( entryMouseOver != null ) {
                PARENT.setTooltip( entryMouseOver.getTooltip() );
            }
        }
    }
    
    /** @return True if any fields have been changed. */
    public boolean isChanged() { return changed; }
    
    /** Called by fields to verify changed state. */
    public void updateChangedState() {
        update:
        {
            for( ConfigGuiEntry child : children() ) {
                if( child instanceof FieldGuiEntry<?> fieldEntry && fieldEntry.isChanged() ) {
                    changed = true;
                    break update;
                }
            }
            changed = false;
        }
        PARENT.updateFooterButtonText();
    }
    
    /** Called when the "save changes" button is pressed to apply all pending changes. */
    public void saveChanges() {
        if( EDITABLE && changed ) {
            for( ConfigGuiEntry child : children() ) {
                if( child instanceof FieldGuiEntry<?> fieldEntry && fieldEntry.isChanged() ) fieldEntry.pushValue();
            }
            if( REMOTE ) CrustPacketHandler.sendConfigChangeRequest( SPEC );
            else SPEC.onLoad();
        }
    }
    
    /** Centers the list entry on-screen by scrolling the list up or down. */
    @Override // Change access to public
    public void centerScrollOn( ConfigGuiEntry entry ) { super.centerScrollOn( entry ); }
    
    /** Ensures the list entry is on-screen by scrolling the list up or down. */
    @Override // Change access to public
    public void ensureVisible( ConfigGuiEntry entry ) { super.ensureVisible( entry ); }
    
    
    // ---- GUI Builder Methods ---- //
    
    /** Appends a specific number of empty lines. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec newLine( int count ) {
        for( int i = 0; i < count; i++ ) add( new FormatGuiEntry.NewLine() );
        return this;
    }
    
    /** Appends a single-line comment. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec comment( String str, int color ) {
        List<FormattedCharSequence> lines = minecraft.font.split(
                Component.literal( str ), MAX_WIDTH );
        for( FormattedCharSequence line : lines ) add( new FormatGuiEntry.LeftAlignedString( line, color ) );
        return this;
    }
    
    /** Appends a tooltip comment. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec titledComment( String title, List<String> comment, int color ) {
        return add( new FormatGuiEntry.TitledComment( title, comment, color ) );
    }
    
    /** Appends a single-line centered header with a tooltip comment. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec header( String str, @Nullable List<String> comment, int color ) {
        return add( new FormatGuiEntry.Header( ConfigUtil.decodeBareKeyString( str ), comment, color ) );
    }
    
    /** Appends a field widget. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec field( IConfigField<?> field, @Nullable RestartNote restartNote, List<String> addedComment ) {
        String name = ConfigUtil.decodeBareKeyString( field.getKey().startsWith( SPEC.loadingCategory ) ?
                field.getKey().substring( SPEC.loadingCategory.length() ) : field.getKey() );
        
        return add( new FieldGuiEntry<>( this, field, name, restartNote, addedComment, REMOTE, EDITABLE ) );
    }
    
    /** Appends a line entry. */
    @Override // ICrustConfigGuiSpec
    public ICrustConfigGuiSpec add( ConfigGuiEntry entry ) {
        addEntry( entry );
        return this;
    }
}