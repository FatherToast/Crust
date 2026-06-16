package fathertoast.crust.test.common;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.core.Crust;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrustPlugin
public class TestCrust implements ICrustPlugin {
    
    /** Logger instance for the test package. */
    public static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID + "/TEST" );
    
    /** API instance */
    public static ICrustApi API;
    
    
    /** File for testing the config api. */
    public static final TestConfigFile CONFIG = new TestConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "test_config" );
    public static final TestConfigReadme README = new TestConfigReadme(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "test_readme" );
    
    
    /** Called by Crust after {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}. */
    public void onLoad( ICrustApi apiInstance ) { API = apiInstance; }
    
    /** @return A ResourceLocation representing the ID of this plugin. */
    public ResourceLocation getId() { return Crust.rl( "test_plugin" ); }
}