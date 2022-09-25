package fathertoast.crust.client.mode;

import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID )
public class CrustModeClientEvents {
    
    private static Float originalStepHeight;
    private static Float originalFlySpeed;
    
    /**
     * Called each player tick. Server event is handled in
     * {@link fathertoast.crust.common.mode.CrustModeEvents#onPlayerTick(TickEvent.PlayerTickEvent)}.
     */
    @SubscribeEvent
    static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END && event.player == Minecraft.getInstance().player ) {
            PlayerEntity player = event.player;
            CrustModesData playerModes = CrustModesData.of( player );
            
            // Various timers
            //            int clock32 = player.tickCount & 0b1_1111;
            //            int clock16 = clock32 & 0b1111;
            int clock4 = player.tickCount & 0b11;
            
            if( clock4 == 3 ) {
                if( player.isSprinting() && playerModes.enabled( CrustModes.SUPER_SPEED ) ) {
                    if( originalStepHeight == null ) originalStepHeight = player.maxUpStep;
                    player.maxUpStep = Math.max( originalStepHeight, 1.0F );
                    
                    if( player.abilities.flying ) {
                        if( originalFlySpeed == null ) originalFlySpeed = player.abilities.getFlyingSpeed();
                        player.abilities.setFlyingSpeed( originalFlySpeed * playerModes.get( CrustModes.SUPER_SPEED ) );
                    }
                }
                else {
                    if( originalStepHeight != null ) {
                        player.maxUpStep = originalStepHeight;
                        originalStepHeight = null;
                    }
                    if( originalFlySpeed != null ) {
                        player.abilities.setFlyingSpeed( originalFlySpeed );
                        originalFlySpeed = null;
                    }
                }
            }
        }
    }
}