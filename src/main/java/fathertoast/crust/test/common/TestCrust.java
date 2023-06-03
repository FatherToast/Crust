package fathertoast.crust.test.common;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.impl.CrustApi;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrustPlugin
public class TestCrust implements ICrustPlugin {
    
    /** Logger instance for the test package. */
    public static final Logger LOG = LogManager.getLogger( "crust/TEST" );
    
    /** API instance */
    public static ICrustApi API;
    
    /** File for testing the config api. */
    public static final TestConfigFile CONFIG = new TestConfigFile(
            ConfigManager.getRequired( Crust.MOD_ID ), "test_config" );
    
    
    /** Called by Crust after {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}. */
    public void onLoad( ICrustApi apiInstance ) { API = apiInstance; }
    
    /** @return A ResourceLocation representing the ID of this plugin. */
    public ResourceLocation getId() { return Crust.resLoc( "test_plugin" ); }
}