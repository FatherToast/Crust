package fathertoast.crust.common.compat.jei;

import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.ScreenEvents;
import fathertoast.crust.common.core.Crust;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class CrustJeiPlugin implements IModPlugin {
    
    public static final ResourceLocation PLUGIN_ID = Crust.rl( "crust_jei" );
    
    
    /** The unique ID for this mod plugin. */
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
    
    /**
     * Register various GUI-related things for your mod.
     * This includes adding clickable areas in your GUIs to open JEI,
     * and adding areas on the screen that JEI should avoid drawing.
     */
    @Override
    public void registerGuiHandlers( IGuiHandlerRegistration registration ) {
        registration.addGlobalGuiHandler( new InventoryGuiHandler() );
    }
    
    
    /** A handler for telling JEI where Crust's extra inventory buttons are located. */
    static class InventoryGuiHandler implements IGlobalGuiHandler {
        
        /**
         * Give JEI information about extra space that your mod takes up.
         * Used for moving JEI out of the way of extra things like gui buttons.
         *
         * @return the space that the gui takes up besides the normal rectangle defined by {@link AbstractContainerScreen}.
         */
        @Override
        public Collection<Rect2i> getGuiExtraAreas() {
            if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.enabled.get() &&
                    Minecraft.getInstance().screen instanceof AbstractContainerScreen ) {
                final Rect2i buttonArea = ScreenEvents.INV_BUTTONS_AREA;
                if( buttonArea != null ) return List.of( buttonArea );
            }
            return Collections.emptyList();
        }
    }
}
