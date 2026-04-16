package fathertoast.crust.common.config;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.ConfigManager;

/**
 * The config manager that determines the mod's config folder is created on the main thread during the mod construct loading stage.
 * ({@link fathertoast.crust.common.core.Crust#Crust(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext)}). You may create this wherever you want, as long as it exists
 * before you initialize any of your config files.
 * <p>
 * The initial loading for this is done by the work queue in the Crust mod's common setup event
 * ({@link fathertoast.crust.common.core.Crust#onCommonSetup(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent)}).
 * <p>
 * We also add functionality to the Forge "Config" button and load client configs in the client setup event
 * ({@link fathertoast.crust.client.ClientRegister#onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent)}).
 */
public final class CrustConfig {
    
    /** File for configuring default game rules. */
    public static final GameRulesCrustConfigFile DEFAULT_GAME_RULES = new GameRulesCrustConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "default_game_rules" );
    /** File for configuring modes. */
    public static final CrustModesConfigFile MODES = new CrustModesConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "modes" );
    /** File for configuring misc utilities. */
    public static final CrustUtilitiesConfigFile UTILITIES = new CrustUtilitiesConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "utilities" );
    
    /**
     * Called to load all the common config files.
     */
    public static void initialize() {
        CrustConfig.DEFAULT_GAME_RULES.SPEC.initialize();
        CrustConfig.MODES.SPEC.initialize();
        CrustConfig.UTILITIES.SPEC.initialize();
    }
}