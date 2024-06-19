package fathertoast.crust.api.config.client.gui.widget;

import fathertoast.crust.api.config.client.gui.screen.CrustConfigFileScreen;
import fathertoast.crust.api.config.client.gui.widget.field.ResetButton;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jline.reader.Widget;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Widget that displays a list of all fields defined in one config spec.
 * <p>
 * The layout of this screen is largely driven by the spec itself, while
 * each field decides how it displays its own info.
 */
public class CrustConfigFieldList extends ContainerObjectSelectionList<CrustConfigFieldList.Entry> {
    
    /** The maximum width for text lines in tooltips. */
    public static final int TOOLTIP_WIDTH = 150;
    
    /** The total amount of space available for field widgets. */
    public static final int OVERALL_WIDTH = 310;
    /** The width of the 'reset to default' button, including padding. */
    public static final int RESET_BUTTON_WIDTH = 12;
    /** The width of the scroll bar, including padding. */
    public static final int SCROLL_WIDTH = 10;
    /** The total amount of space available for left-aligned rows. */
    public static final int MAX_WIDTH = OVERALL_WIDTH - SCROLL_WIDTH;
    
    
    /** The config spec this list is displaying contents for. */
    public final CrustConfigFileScreen PARENT;
    /** The config spec this list is displaying contents for. */
    public final CrustConfigSpec SPEC;
    
    /** True if any fields have been changed since opening. */
    private boolean changed;
    
    public CrustConfigFieldList( CrustConfigFileScreen parent, Minecraft game, CrustConfigSpec spec ) {
        super( game, parent.width, parent.height,
                43, parent.height - 32, IConfigFieldWidgetProvider.VALUE_HEIGHT + 1 );
        PARENT = parent;
        SPEC = spec;
        
        // Simply pass all responsibility on to the config spec
        spec.initGui( this, this::addEntry );
    }
    
    /** Inserts a single new line. */
    public void newLine() { newLine( 1 ); }
    
    /** @param count The number of new lines to insert. */
    public void newLine( int count ) {
        for( int i = 0; i < count; i++ ) addEntry( new NewLineEntry() );
    }
    
    /** Adds a comment. Each string in the list is printed on a separate line, in the order returned by iteration. */
    public void comment( List<String> comment ) { comment( comment, 0x777777 ); }
    
    /** Adds a comment. Each string in the list is printed on a separate line, in the order returned by iteration. */
    public void comment( List<String> comment, int color ) {
        for( String line : comment ) comment( line, color );
    }
    
    /** Adds a single-line comment. */
    public void comment( String str ) { comment( str, 0x777777 ); }
    
    /** Adds a single-line comment. */
    public void comment( String str, int color ) {
        List<FormattedCharSequence> lines = minecraft.font.split(
                Component.literal( str ), MAX_WIDTH );
        for( FormattedCharSequence line : lines ) addEntry( new LeftAlignedStringEntry( line, color ) );
    }
    
    /** Adds a tooltip comment. */
    public void titledComment( String title, List<String> comment ) { titledComment( title, comment, 0x777777 ); }
    
    /** Adds a tooltip comment. */
    public void titledComment( String title, List<String> comment, int color ) {
        addEntry( new TitledCommentEntry( title, comment, color ) );
    }
    
    /** Adds a single-line centered header with a tooltip comment. */
    public void header( String str, @Nullable List<String> comment ) { header( str, comment, 0xFFFF55 ); }
    
    /** Adds a single-line centered header with a tooltip comment. */
    public void header( String str, @Nullable List<String> comment, int color ) {
        addEntry( new HeaderEntry( str, comment, color ) );
    }
    
    /** Adds a field widget. */
    public void field( AbstractConfigField field, @Nullable RestartNote restartNote, List<String> addedComment ) {
        String name = CrustConfigFileScreen.decodeString( field.getKey().startsWith( SPEC.loadingCategory ) ?
                field.getKey().substring( SPEC.loadingCategory.length() ) : field.getKey() );
        
        addEntry( new FieldEntry( this, field, name, restartNote, addedComment ) );
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
            Entry entryMouseOver = getEntryAtPosition( mouseX, mouseY );
            if( entryMouseOver != null ) {
                PARENT.setTooltip( entryMouseOver.getTooltip() );
            }
        }
    }
    
    /** @return True if any fields have been changed. */
    public boolean isChanged() { return changed; }
    
    /** Called by fields to verify changed state. */
    private void updateChangedState() {
        update:
        {
            for( Entry child : children() ) {
                if( child instanceof FieldEntry && ((FieldEntry) child).changed ) {
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
        if( changed ) {
            for( Entry child : children() ) {
                if(child instanceof FieldEntry fieldEntry) {
                    if( fieldEntry.changed ) {
                        SPEC.getNightConfig().set( fieldEntry.FIELD.getKey(), fieldEntry.pendingValue );
                    }
                }
            }
            SPEC.onLoad();
        }
    }
    
    
    public static abstract class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        
        public Minecraft minecraft() { return Minecraft.getInstance(); }

        @Nullable
        public List<FormattedCharSequence> getTooltip() { return null; }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }
    
    public static class NewLineEntry extends Entry {
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) { }

        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
            return super.nextFocusPath( event );
        }

        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
    }
    
    public static class LeftAlignedStringEntry extends Entry {
        
        private final FormattedCharSequence TEXT;
        private final int COLOR;
        
        public LeftAlignedStringEntry( FormattedCharSequence text, int color ) {
            TEXT = text;
            COLOR = color;
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {

            graphics.drawString( minecraft().font, TEXT, rowLeft, rowTop + 5, COLOR );
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
            return super.nextFocusPath( event );
        }

        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
    }
    
    public static class TitledCommentEntry extends Entry {
        
        private final Component TEXT;
        private final List<FormattedCharSequence> TOOLTIP;
        private final int COLOR;
        
        public TitledCommentEntry( String text, List<String> comment, int color ) {
            TEXT = Component.literal( text );
            COLOR = color;
            
            if( comment.isEmpty() ) TOOLTIP = null;
            else {
                TOOLTIP = new ArrayList<>();
                TOOLTIP.addAll( minecraft().font.split( Component.literal(
                        text ).withStyle( ChatFormatting.YELLOW ), TOOLTIP_WIDTH ) );
                for( String line : comment ) {
                    TOOLTIP.addAll( minecraft().font.split( Component.literal(
                            line ), TOOLTIP_WIDTH ) );
                }
            }
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {

            graphics.drawString(minecraft().font, TEXT,
                    rowLeft, rowTop + 5, COLOR );
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
            return super.nextFocusPath( event );
        }

        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
        
        @Override
        @Nullable
        public List<FormattedCharSequence> getTooltip() { return TOOLTIP; }
    }
    
    public static class CenteredStringEntry extends Entry {
        
        private final Component TEXT;
        private final int COLOR;
        
        public final int WIDTH;
        
        public CenteredStringEntry( Component text, int color ) {
            TEXT = text;
            COLOR = color;
            WIDTH = Minecraft.getInstance().font.width( TEXT );
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            //noinspection ConstantConditions
            graphics.drawString( minecraft().font, TEXT,
                    minecraft().screen.width - WIDTH - 5 >> 1, rowTop + 5, COLOR );
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
            return super.nextFocusPath( event );
        }

        @Override
        public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
    }
    
    public static class HeaderEntry extends CenteredStringEntry {
        
        private final List<FormattedCharSequence> TOOLTIP;
        
        public HeaderEntry( String text, @Nullable List<String> comment, int color ) {
            super( Component.literal( ConfigUtil.properCase( CrustConfigFileScreen.decodeString( text ) ) ), color );
            
            if( comment == null || comment.isEmpty() ) TOOLTIP = null;
            else {
                TOOLTIP = new ArrayList<>();
                TOOLTIP.addAll( minecraft().font.split( Component.literal(
                        text ).withStyle( ChatFormatting.YELLOW ), TOOLTIP_WIDTH ) );
                for( String line : comment ) {
                    TOOLTIP.addAll( minecraft().font.split( Component.literal(
                            line ), TOOLTIP_WIDTH ) );
                }
            }
        }
        
        @Override
        @Nullable
        public List<FormattedCharSequence> getTooltip() { return TOOLTIP; }
    }
    
    public static class FieldEntry extends Entry {
        
        public final CrustConfigFieldList PARENT;
        public final AbstractConfigField FIELD;
        
        private final List<FormattedCharSequence> NAME;
        private final List<FormattedCharSequence> TOOLTIP;
        
        private final List<AbstractWidget> COMPONENTS = new ArrayList<>();
        private final List<OffsetWidget> RENDER_COMPONENTS = new ArrayList<>();
        
        private final Button RESET_BUTTON;
        private final Object CURRENT_VALUE;
        
        private Object pendingValue;
        private boolean changed;
        
        public FieldEntry( CrustConfigFieldList parent, AbstractConfigField field, String name,
                           @Nullable RestartNote restartNote, List<String> addedComment ) {
            PARENT = parent;
            FIELD = field;
            NAME = minecraft().font.split( Component.literal( name ),
                    MAX_WIDTH - 2 - IConfigFieldWidgetProvider.VALUE_WIDTH - RESET_BUTTON_WIDTH );
            
            TOOLTIP = new ArrayList<>();
            buildTooltip( TOOLTIP_WIDTH, restartNote, addedComment );
            if( TOOLTIP.size() > 20 ) { // Just brute force it a little
                TOOLTIP.clear();
                buildTooltip( (int) (TOOLTIP_WIDTH * 1.5), restartNote, addedComment );
                if( TOOLTIP.size() > 20 ) {
                    TOOLTIP.clear();
                    buildTooltip( TOOLTIP_WIDTH * 2, restartNote, addedComment );
                }
            }
            
            CURRENT_VALUE = pendingValue = field.getValue();

            RESET_BUTTON = new ResetButton( ( button ) -> {
                updateValue( FIELD.getDefaultValue() );
                populateComponents();
            } );
            RESET_BUTTON.active = !TomlHelper.equals( FIELD.getDefaultValue(), pendingValue );
            
            populateComponents();
        }
        
        private void populateComponents() {
            COMPONENTS.clear();
            RENDER_COMPONENTS.clear();
            
            RESET_BUTTON.setX( IConfigFieldWidgetProvider.VALUE_WIDTH + 1 );
            RESET_BUTTON.setY( 0 );
            COMPONENTS.add( RESET_BUTTON );
            FIELD.getWidgetProvider().apply( COMPONENTS, this, pendingValue );
            for( AbstractWidget component : COMPONENTS ) RENDER_COMPONENTS.add( new OffsetWidget( component ) );
        }
        
        /** @return The field's pending "new" value. */
        public Object getValue() { return pendingValue; }
        
        /** Call this to change the field's pending "new" value. */
        public void updateValue( Object value ) {
            pendingValue = value;
            RESET_BUTTON.active = !TomlHelper.equals( FIELD.getDefaultValue(), value );
            changed = !TomlHelper.equals( CURRENT_VALUE, value );
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Call this to delete the field's pending "new" value. */
        public void clearValue() {
            pendingValue = CURRENT_VALUE;
            RESET_BUTTON.active = !TomlHelper.equals( FIELD.getDefaultValue(), CURRENT_VALUE );
            changed = false;
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Ensures this list entry is on-screen by scrolling the list up or down. */
        public void ensureVisible() { PARENT.ensureVisible( this ); }
        
        /** Builds this field entry's tooltip. */
        private void buildTooltip( int width, @Nullable RestartNote restartNote, List<String> addedComment ) {
            TOOLTIP.addAll( minecraft().font.split( Component.literal(
                    FIELD.getKey() ).withStyle( ChatFormatting.YELLOW ), width ) );
            if( FIELD.getComment() != null && !FIELD.getComment().isEmpty() ) {
                for( String line : FIELD.getComment() ) {
                    TOOLTIP.addAll( minecraft().font.split( Component.literal(
                            line ), width ) );
                }
            }
            if( restartNote != null ) {
                TOOLTIP.addAll( minecraft().font.split( Component.literal(
                        restartNote.COMMENT ).withStyle( ChatFormatting.RED ), width ) );
            }
            if( !addedComment.isEmpty() ) {
                for( String line : addedComment ) {
                    TOOLTIP.addAll( minecraft().font.split( Component.literal(
                            line ).withStyle( ChatFormatting.GRAY ), width ) );
                }
            }
        }
        
        /** Opens a popup widget over the screen. Setting to null closes any open popup. */
        public void setPopupWidget( @Nullable AbstractWidget popup ) { PARENT.PARENT.setPopupWidget( popup ); }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            int color = changed ? 0x55FFFF : 0xFFFFFF;
            if( NAME.size() == 1 ) {
                graphics.drawString( minecraft().font, NAME.get( 0 ),
                        rowLeft, rowTop + 5, color );
            }
            else if( NAME.size() >= 2 ) {
                graphics.drawString( minecraft().font, NAME.get( NAME.size() - 2 ),
                        rowLeft, rowTop, color );
                graphics.drawString( minecraft().font, NAME.get( NAME.size() - 1 ),
                        rowLeft, rowTop + 10, color );
            }
            
            for( OffsetWidget component : RENDER_COMPONENTS ) {
                component.WIDGET.setX( component.X_OFFSET + rowLeft + MAX_WIDTH - IConfigFieldWidgetProvider.VALUE_WIDTH - RESET_BUTTON_WIDTH );
                component.WIDGET.setY( component.Y_OFFSET + rowTop );
                component.WIDGET.render( graphics, mouseX, mouseY, partialTicks );
            }
        }
        
        @Override
        @Nullable
        public List<FormattedCharSequence> getTooltip() { return TOOLTIP; }
        
        @Override
        public List<? extends GuiEventListener> children() { return COMPONENTS; }
        
        @Override
        public void setFocused( @Nullable GuiEventListener component ) {
            if( component instanceof EditBox editBox) PARENT.PARENT.setFocusedTextBox( editBox );
            super.setFocused( component );
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        /** Simple wrapper used to save the offsets of provided field gui components. */
        private static class OffsetWidget {
            
            final AbstractWidget WIDGET;
            final int X_OFFSET;
            final int Y_OFFSET;
            
            OffsetWidget( AbstractWidget widget ) {
                WIDGET = widget;
                X_OFFSET = widget.getX();
                Y_OFFSET = widget.getY();
            }
        }
    }
}