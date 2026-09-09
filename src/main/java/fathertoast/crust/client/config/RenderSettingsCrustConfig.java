package fathertoast.crust.client.config;

import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

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
    
    private final Map<Class<?>, BooleanField> shapeProviderToggles = new HashMap<>();
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    public RenderSettingsCrustConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, true,
                "Settings for features related to in-world rendering." );
        final Map<Class<?>, String> debugShapeProviders = getShapeProviderClasses();
        
        SPEC.category( "misc" );
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
        
        SPEC.newLine();
        buildShapeProviderToggles( BlockEntity.class, shapeProviderToggles, debugShapeProviders );
        
        SPEC.category( "entity_debug_shapes" );
        entityShapes = SPEC.define( new BooleanField( "enabled", true,
                "If true, entities close to the player that support Crust's debug bounding box " +
                        "rendering will draw their boxes while 'show entity hitboxes' (F3+B) is active." ) );
        entityShapesDistanceSqr = SPEC.define( new SqrDoubleField( "distance", 48.0, DoubleField.Range.NON_NEGATIVE,
                "If entity bounding box rendering is enabled, this value determines the maximum " +
                        "distance from the player in which Crust will look for entities to render bounding " +
                        "boxes for. This value is also capped by the effective render distance." ) );
        
        SPEC.newLine();
        buildShapeProviderToggles( Entity.class, shapeProviderToggles, debugShapeProviders );
    }
    
    
    /**
     * @return True if the shape provider associated with the given class is enabled.
     * Returns false otherwise or if the given class is somehow not contained in the map of provider classes.
     */
    public boolean isShapeProviderEnabled( IDebugShapeProvider provider ) {
        if( shapeProviderToggles.containsKey( provider.getClass() ) ) {
            return shapeProviderToggles.get( provider.getClass() ).get();
        }
        return false;
    }
    
    /**
     * Attempts to build a toggle boolean field for each debug shape provider of a specific type.
     *
     * @param baseClass           The base class of the implementing provider type, such as {@link Block} or {@link BlockEntity}.
     * @param debugShapeProviders The map of identified providers and the namespace they likely belong to.
     */
    private void buildShapeProviderToggles( Class<?> baseClass, Map<Class<?>, BooleanField> fieldMap,
                                            Map<Class<?>, String> debugShapeProviders ) {
        final String typeName = ConfigUtil.camelCaseToLowerSpace( baseClass.getSimpleName() );
        if( !debugShapeProviders.isEmpty() ) {
            SPEC.titledComment( ChatFormatting.AQUA + ConfigUtil.properCase( typeName ) + " shape providers",
                    "Below is a column of toggle options per " + typeName + " shape provider, " +
                            "such that each provider can individually be turned on or off." );
        }
        final String category = SPEC.loadingCategory;
        
        for( Class<?> clazz : debugShapeProviders.keySet() ) {
            if( baseClass.isAssignableFrom( clazz ) ) {
                final String modId = debugShapeProviders.get( clazz );
                String fieldName = modId + "." + ConfigUtil.camelCaseToLowerUnderscore( clazz.getSimpleName() );
                boolean fieldExists = SPEC.hasField( category + fieldName );
                int i = 0;
                
                // Just in case we somehow encounter multiple provider classes with the same name
                while( fieldExists ) {
                    ++i;
                    fieldExists = SPEC.hasField( category + fieldName + "_" + i );
                    
                    if( !fieldExists ) {
                        fieldName = fieldName + "_" + i;
                        break;
                    }
                }
                fieldMap.put( clazz, SPEC.define( new BooleanField( fieldName, true,
                        "If true, debug shape providers of this type will have their debug shapes " +
                                "rendered when debug shape rendering is enabled." ) ) );
            }
        }
    }
    
    /**
     * Iterates through all scan data via {@link ModList#get()} and attempts to find all
     * classes that implement {@link IDebugShapeProvider} and return them in a list.
     *
     * @return A map of all classes implementing the {@link IDebugShapeProvider} interface linked to
     * the ID of the mod that owns the class.
     */
    private Map<Class<?>, String> getShapeProviderClasses() {
        final Map<Class<?>, String> shapeProviderClasses = new HashMap<>();
        final Type targetInterface = Type.getType( IDebugShapeProvider.class );
        
        // Iterate through all scanned classes
        for( ModFileScanData scanData : ModList.get().getAllScanData() ) {
            for( ModFileScanData.ClassData classData : scanData.getClasses() ) {
                // Look for classes implementing IDebugShapeProvider
                if( classData.interfaces().contains( targetInterface ) ) {
                    final String className = classData.clazz().getClassName();
                    try {
                        final Class<?> clazz = Class.forName( className );
                        // Make an attempt at identifying the mod that owns the class
                        final String modId = scanData.getTargets().keySet().stream().findFirst().orElse( "missing_id" );
                        shapeProviderClasses.put( clazz, modId );
                    }
                    catch( ClassNotFoundException e ) {
                        ConfigUtil.LOG.error( "Failed to load class '{}' for Crust's render settings config!", className );
                        if( !FMLEnvironment.production ) {
                            // noinspection CallToPrintStackTrace
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return shapeProviderClasses;
    }
}