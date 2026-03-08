package fathertoast.crust.api.event;

import fathertoast.crust.api.advancement.IModifiableAdvancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when advancements are loaded from JSON.
 * This event provides an {@link IModifiableAdvancement} instance
 * containing the original advancement data, making it easier to modify
 * most properties of the advancement before it is built.
 * <br><br>
 * This event is fired only on the server, posted on the {@link MinecraftForge#EVENT_BUS} bus, does not
 * have a Result and is not cancelable.
 */
public final class AdvancementLoadEvent extends Event {
    
    private final ResourceLocation advancementId;
    private final IModifiableAdvancement modifiableAdvancement;
    
    public AdvancementLoadEvent( ResourceLocation advancementId, IModifiableAdvancement modifiableAdvancement ) {
        this.advancementId = advancementId;
        this.modifiableAdvancement = modifiableAdvancement;
    }
    
    /**
     * @return A modifiable view of the advancement data.
     */
    public IModifiableAdvancement getAdvancement() {
        return modifiableAdvancement;
    }
    
    /** @return The ID of the advancement being loaded. */
    public ResourceLocation getId() {
        return advancementId;
    }
}
