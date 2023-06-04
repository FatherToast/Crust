package fathertoast.crust.api.impl;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;

@CrustPlugin
public class InternalCrustPlugin implements ICrustPlugin {
    
    private static final ResourceLocation ID = Crust.resLoc( "builtin_plugin" );
    
    /** Called by Crust after {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}. */
    @Override
    public void onLoad( ICrustApi apiInstance ) {
        // Don't need to do anything yet
    }
    
    /** @return A ResourceLocation representing the ID of this plugin. */
    @Override
    public ResourceLocation getId() { return ID; }
}