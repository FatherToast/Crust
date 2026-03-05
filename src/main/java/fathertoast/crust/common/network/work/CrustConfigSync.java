package fathertoast.crust.common.network.work;

import com.electronwill.nightconfig.core.CommentedConfig;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.network.message.S2CSendConfigData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is responsible for keeping track of configs that need
 * to be synced from server to client and holds handler methods
 * for the sync packet.
 */
@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public final class CrustConfigSync {
    
    /** A map containing Crust configs with sync enabled, mapped to an ID string. */
    private static final HashMap<String, AbstractConfigFile> TRACKED_CONFIGS = new HashMap<>();
    
    /**
     * Called when mod loading has completed. At this point
     * all configs that are supposed to be registered are expected to be, and we collect all configs
     * that has set {@link AbstractConfigFile#SYNC_ENABLED} to true and map them to
     * a unique config ID.
     */
    @SubscribeEvent
    public static void onLoadingComplete( FMLLoadCompleteEvent event ) {
        Collection<ConfigManager> cfgManagers = ConfigManager.getAll();
        
        for( ConfigManager cfgManager : cfgManagers ) {
            for( AbstractConfigFile cfg : cfgManager.getConfigs() ) {
                if( cfg.SYNC_ENABLED ) {
                    TRACKED_CONFIGS.put( ResourceLocation.fromNamespaceAndPath( cfgManager.MOD_ID, cfg.SPEC.NAME ).toString(), cfg );
                }
            }
        }
    }
    
    /**
     * Builds the list of config sync packets to send from server to client on login.
     * One packet is constructed per tracked config.
     *
     * @param isLocal True if we are on an integrated server.
     * @return The list of config sync packets to send on login.
     */
    public static List<Pair<String, S2CSendConfigData>> syncConfigs( boolean isLocal ) {
        // Don't send configs from integrated server, configs are shared by both sides
        if( isLocal ) return List.of();
        
        final List<Pair<String, S2CSendConfigData>> configData = new ArrayList<>();
        
        for( String key : TRACKED_CONFIGS.keySet() ) {
            AbstractConfigFile config = TRACKED_CONFIGS.get( key );
            byte[] data;
            
            try {
                data = Files.readAllBytes( config.SPEC.getNightConfig().getNioPath() );
            }
            catch( IOException e ) {
                throw new RuntimeException( e );
            }
            configData.add( Pair.of( key, new S2CSendConfigData( key, data ) ) );
        }
        return configData;
    }
    
    /**
     * Called from {@link S2CSendConfigData#handle(S2CSendConfigData, Supplier)}.
     * <br>
     * Processes config data sent from the server.
     */
    public static void processConfigSync( S2CSendConfigData message, NetworkEvent.Context context ) {
        final String configId = message.configId;
        final byte[] configData = message.fileData;
        
        if( !TRACKED_CONFIGS.containsKey( configId ) ) {
            throw new IllegalStateException( "Received bogus Crust config ID from server: '{}'. This config ID is not mapped to a syn ced config on this client!" );
        }
        // TODO
        AbstractConfigFile configFile = TRACKED_CONFIGS.get( configId );
        CommentedConfig cfg = configFile.SPEC.getFormat().createParser().parse( new ByteArrayInputStream( configData ) );
    }
    
    private CrustConfigSync() { }
}
