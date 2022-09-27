package fathertoast.crust.common.core;

import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID )
public class CrustForgeEvents {
    
    /** Set of all players that have had their Crust modes changed recently. */
    private static final Set<ServerPlayerEntity> NEED_CRUST_MODE_UPDATE = new HashSet<>();
    
    private static int updateCounter;
    
    /** Queues the player for a Crust mode update to notify the client of changes. */
    public static void markModesDirty( PlayerEntity player ) {
        NEED_CRUST_MODE_UPDATE.add( (ServerPlayerEntity) player );
    }
    
    /** Called when an entity is spawned/added into the world. */
    @SubscribeEvent
    static void onEntityJoinWorld( EntityJoinWorldEvent event ) {
        if( !event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayerEntity ) {
            markModesDirty( (PlayerEntity) event.getEntity() );
        }
    }
    
    /** Called each server tick. */
    @SubscribeEvent
    static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            updateCounter++;
            if( updateCounter >= 3 ) {
                updateCounter = 0;
                if( !NEED_CRUST_MODE_UPDATE.isEmpty() ) {
                    for( ServerPlayerEntity player : NEED_CRUST_MODE_UPDATE ) {
                        CrustPacketHandler.sendCrustModesUpdate( player );
                    }
                    NEED_CRUST_MODE_UPDATE.clear();
                }
            }
        }
    }
}