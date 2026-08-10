package fathertoast.crust.api.config.common.value;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Optional interface you may implement on an enum to provide a tooltip description for
 * each value in the in-game config editor GUI.
 */
public interface ITooltipEnum {
    /** @return The component to display in the tooltip, or null if none should display. */
    @Nullable
    Component getTooltip();
}