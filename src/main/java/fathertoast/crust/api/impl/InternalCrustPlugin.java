package fathertoast.crust.api.impl;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;

@CrustPlugin
public class InternalCrustPlugin implements ICrustPlugin {
    
    private static final ResourceLocation ID = Crust.resLoc( "builtin_plugin" );
    
    public static PortalBuilder NETHER_PORTAL;
    public static PortalBuilder END_PORTAL;
    
    
    @Override
    public void onLoad( ICrustApi apiInstance ) {
        apiInstance.getRegistryHelper();
    }
    
    @Override
    public ResourceLocation getId() {
        return ID;
    }
}