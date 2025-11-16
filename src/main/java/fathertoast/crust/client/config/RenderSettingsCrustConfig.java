package fathertoast.crust.client.config;

import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.client.ClientRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * This config is instantiated and then initialized (first-time loaded) during the
 * {@link FMLClientSetupEvent} in {@link ClientRegister#onClientSetup(FMLClientSetupEvent)}.
 * <p>
 * Any time after that event, the config values are simply accessed through the static field
 * {@link ClientRegister#RENDER_SETTINGS}.
 */
public class RenderSettingsCrustConfig extends AbstractConfigFile.Simple {
    
    public final BooleanField fancyFishing;
    
    public final BooleanField blockEntityShapes;
    public final IntField blockEntityShapesDistance;
    
    public final BooleanField entityShapes;
    public final DoubleField entityShapesDistanceSqr;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    public RenderSettingsCrustConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "Settings for features related to in-world rendering." );
        
        fancyFishing = SPEC.define( new BooleanField( "fancy_fishing", true,
                "Overrides the default fishing rod item animation so that it is compatible with fishing " +
                        "mobs that use Crust's base fishing hook. Set to false if it causes problems with another mod."
        ), RestartNote.GAME );
        
        SPEC.category( "block_entity_debug_shapes" );
        blockEntityShapes = SPEC.define( new BooleanField( "enabled", true,
                "If true, block entities close to the player that support Crust's debug bounding box " +
                        "rendering will draw their boxes while 'show entity hitboxes' (F3+B) is active." ) );
        blockEntityShapesDistance = SPEC.define( new IntField( "distance", 3, IntField.Range.POSITIVE,
                "If block entity bounding box rendering is enabled, this value determines the " +
                        "'radius' in chunks around the player in which Crust will look for block entities to " +
                        "render bounding boxes for. A value of 1 means only the chunk the player is standing in. " +
                        "This value is also capped by the effective render distance." ) );
        
        SPEC.category( "entity_debug_shapes" );
        entityShapes = SPEC.define( new BooleanField( "enabled", true,
                "If true, entities close to the player that support Crust's debug bounding box " +
                        "rendering will draw their boxes while 'show entity hitboxes' (F3+B) is active." ) );
        entityShapesDistanceSqr = SPEC.define( new SqrDoubleField( "distance", 48.0, DoubleField.Range.NON_NEGATIVE,
                "If entity bounding box rendering is enabled, this value determines the maximum " +
                        "distance from the player in which Crust will look for entities to render bounding " +
                        "boxes for. This value is also capped by the effective render distance." ) );
    }
}