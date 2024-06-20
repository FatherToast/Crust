package fathertoast.crust.client.mode;

import com.mojang.blaze3d.shaders.FogShape;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.mode.CrustModeEvents;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class CrustModeClientEvents {
    
    private static Float originalStepHeight;
    private static Float originalFlySpeed;

    // TODO - Fix this, no idea how this works currently
    /** Called before rendering fog. */
    @SubscribeEvent
    static void onFogDensity( ViewportEvent.RenderFog event ) {
        if( CrustModes.SUPER_VISION.enabled( Minecraft.getInstance().player ) ) {
            event.setNearPlaneDistance(1.0F);
            event.setCanceled( true ); // Event must be canceled to apply changes
        }
    }
    
    /** Called each client tick. */
    @SubscribeEvent
    static void onClientTick( TickEvent.ClientTickEvent event ) {
        if( event.phase == TickEvent.Phase.START ) {
            if( Minecraft.getInstance().level != null ) {
                CrustModeEvents.onWorldTickStart( Minecraft.getInstance().level );
            }
        }
    }
    
    /**
     * Called each player tick. Server event is handled in
     * {@link fathertoast.crust.common.mode.CrustModeEvents#onPlayerTick(TickEvent.PlayerTickEvent)}.
     */
    @SuppressWarnings("JavadocReference")
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase != TickEvent.Phase.END || event.player != Minecraft.getInstance().player ) return;
        
        Player player = event.player;
        CrustModesData playerModes = CrustModesData.of( player );
        
        // Various timers
        //int clock32 = player.tickCount & 0b1_1111;
        //int clock16 = player.tickCount & 0b1111;
        int clock4 = player.tickCount & 0b11;
        
        // Super speed
        if( clock4 == 3 ) {
            if( player.isSprinting() && playerModes.enabled( CrustModes.SUPER_SPEED ) ) {
                if( originalStepHeight == null ) originalStepHeight = player.maxUpStep();
                player.setMaxUpStep( Math.max( originalStepHeight, 1.0F ));
                
                if( player.getAbilities().flying ) {
                    if( originalFlySpeed == null ) originalFlySpeed = player.getAbilities().getFlyingSpeed();
                    player.getAbilities().setFlyingSpeed( originalFlySpeed * playerModes.get( CrustModes.SUPER_SPEED ) );
                }
            }
            else {
                if( originalStepHeight != null ) {
                    player.setMaxUpStep( originalStepHeight );
                    originalStepHeight = null;
                }
                if( originalFlySpeed != null ) {
                    player.getAbilities().setFlyingSpeed( originalFlySpeed );
                    originalFlySpeed = null;
                }
            }
        }
    }
}