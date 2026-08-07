package fathertoast.crust.api.config.client.gui.widget.entry;

import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.action.ICrustConfigGuiSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents one entry in a {@link fathertoast.crust.client.screen.widget.CrustConfigFieldList}.
 * These entries are used for both field widget display and formatting.
 * <p>
 * This class is analogous to {@link fathertoast.crust.api.config.common.file.action.ISpecAction} in
 * {@link CrustConfigSpec}, but for the in-game editor, and is what the spec actions ultimately
 * generate during {@link CrustConfigSpec#initGui(ICrustConfigGuiSpec)} to build the GUI.
 */
public abstract class ConfigGuiEntry extends ContainerObjectSelectionList.Entry<ConfigGuiEntry> implements ISearchable {
    
    /** The maximum width for text lines in tooltips. */
    public static final int TOOLTIP_WIDTH = 150;
    
    /** @return The client singleton instance. Just a slightly more readable version of the usual method. */
    public final Minecraft client() { return Minecraft.getInstance(); }
    
    /** @return The currently open screen. We assume it must be non-null because this is only used by screens. */
    public final Screen screen() { return Objects.requireNonNull( client().screen ); }
    
    /** Wraps the formatted text based on the desired width and appends all those lines to the given tooltip. */
    public void split( List<FormattedCharSequence> tooltip, FormattedText text, int width ) {
        tooltip.addAll( client().font.split( text, width ) );
    }
    
    /** @return The tooltip to render when the mouse is over this entry. Null if no tooltip should render. */
    @Nullable
    public List<FormattedCharSequence> getTooltip() { return null; }
    
    /** @return Narrations for this entry. */
    @Override
    public List<? extends NarratableEntry> narratables() { return List.of(); }
    
    /** @return An identifying String to be looked up by a {@link Searchbar} */
    @Override // ISearchable
    @Nullable
    public String getLookupName() { return null; }
}