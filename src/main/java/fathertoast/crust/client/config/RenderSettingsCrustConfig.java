package fathertoast.crust.client.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;

public class RenderSettingsCrustConfig extends AbstractConfigFile {
    
    public final Misc MISC;
    public final BlockEntityShapes BLOCK_ENTITY_SHAPES;
    public final EntityShapes ENTITY_SHAPES;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    public RenderSettingsCrustConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "Misc. settings for in-world rendering related features." );
        
        MISC = new Misc( this, "misc" );
        BLOCK_ENTITY_SHAPES = new BlockEntityShapes( this, "block_entity_debug_shapes" );
        ENTITY_SHAPES = new EntityShapes( this, "entity_debug_shapes" );
    }
    
    public static class Misc extends AbstractConfigCategory<RenderSettingsCrustConfig> {
        
        public final BooleanField fancyFishing;
        
        Misc( RenderSettingsCrustConfig parent, String category ) {
            super( parent, category, "Miscellaneous Crust render settings." );
            
            fancyFishing = SPEC.define( new BooleanField( "fancy_fishing", true,
                    "Overrides the default fishing rod item animation so that it is compatible with fishing " +
                            "mobs that use Crust's base fishing hook. Set to false if it causes problems with another mod."
            ), RestartNote.GAME );
        }
    }
    
    public static class BlockEntityShapes extends AbstractConfigCategory<RenderSettingsCrustConfig> {
        
        public final BooleanField enabled;
        public final IntField distance;
        
        BlockEntityShapes( RenderSettingsCrustConfig parent, String category ) {
            super( parent, category, "Options for Crust's block entity debug bounding box renderer." );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "If true, block entities close to the player that support Crust's debug bounding box " +
                            "rendering will draw their boxes while 'show entity hitboxes' (F3+B) is active." ) );
            
            SPEC.newLine();
            
            distance = SPEC.define( new IntField( "distance", 3, IntField.Range.POSITIVE,
                    "If block entity bounding box rendering is enabled, this value determines the " +
                            "'radius' in chunks around the player in which Crust will look for block entities to " +
                            "render bounding boxes for. A value of 1 means only the chunk the player is standing in. " +
                            "This value is also capped by the effective render distance." ) );
        }
    }
    
    public static class EntityShapes extends AbstractConfigCategory<RenderSettingsCrustConfig> {
        
        public final BooleanField enabled;
        public final DoubleField distanceSqr;
        
        EntityShapes( RenderSettingsCrustConfig parent, String category ) {
            super( parent, category, "Options for Crust's entity debug bounding box renderer." );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "If true, entities close to the player that support Crust's debug bounding box " +
                            "rendering will draw their boxes while 'show entity hitboxes' (F3+B) is active." ) );
            
            SPEC.newLine();
            
            distanceSqr = SPEC.define( new SqrDoubleField( "distance", 48.0, DoubleField.Range.NON_NEGATIVE,
                    "If entity bounding box rendering is enabled, this value determines the maximum " +
                            "distance from the player in which Crust will look for entities to render bounding " +
                            "boxes for. This value is also capped by the effective render distance." ) );
        }
    }
}