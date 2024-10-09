package fathertoast.crust.client;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.IntField;

public class RenderSettingsCrustConfigFile extends AbstractConfigFile {

    public final RenderSettingsCrustConfigFile.BlockEntityBBRendering BLOCK_ENTITY_BB_RENDERING;

    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    RenderSettingsCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "Misc. settings for in-world rendering related features." );


        BLOCK_ENTITY_BB_RENDERING = new RenderSettingsCrustConfigFile.BlockEntityBBRendering( this, "block_entity_BB_rendering",
                "Options for Crust's block entity bounding box renderer.");
    }

    /**
     * Category for config editor buttons.
     */
    public static class BlockEntityBBRendering extends AbstractConfigCategory<RenderSettingsCrustConfigFile> {

        public final BooleanField enabled;
        public final IntField distance;

        BlockEntityBBRendering( RenderSettingsCrustConfigFile parent, String category, String categoryDescription ) {
            super( parent, category, categoryDescription );

            enabled = SPEC.define( new BooleanField( "enabled", true, "If true, block entities close to the player that support Crust's bounding box outline rendering will draw their boxes if 'show entity hitboxes' is active. " +
                    "This is primarily a debug feature." ) );

            SPEC.newLine();

            distance = SPEC.define( new IntField("distance", 3, IntField.Range.POSITIVE, "If block entity bounding box rendering is enabled, this value determines the 'radius' in chunks around" +
                    " the player in which Crust will look for block entities to render bounding boxes for. A value of 1 means only the chunk the player is standing in. This value is also capped by the effective render distance."));
        }
    }
}
