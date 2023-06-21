package fathertoast.crust.common.core;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
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
    
    /** Called when an entity is taking damage. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingHurt( LivingHurtEvent event ) {
        if( event.getEntityLiving() != null && event.getSource() != DamageSource.OUT_OF_WORLD && !event.getSource().isBypassMagic() &&
                event.getEntityLiving().hasEffect( CrustObjects.vulnerability() ) ) {
            
            final EffectInstance vulnerability = event.getEntityLiving().getEffect( CrustObjects.vulnerability() );
            if( vulnerability == null ) return;
            
            // Take 25% more damage per effect level (vs. Damage Resistance's 20% less per level)
            event.setAmount( Math.max( event.getAmount() * (1.0F + 0.25F * (vulnerability.getAmplifier() + 1)), 0.0F ) );
        }
    }
    
    /** Called when an entity lands on the ground. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingFall( LivingFallEvent event ) {
        if( event.getEntityLiving() != null && event.getEntityLiving().hasEffect( CrustObjects.weight() ) ) {
            
            final EffectInstance weight = event.getEntityLiving().getEffect( CrustObjects.weight() );
            if( weight == null ) return;
            
            // Increase effective fall distance by ~33% per effect level
            event.setDamageMultiplier( event.getDamageMultiplier() * (1.0F + 0.3334F * (weight.getAmplifier() + 1)) );
        }
    }
}