package fathertoast.crust.common.mixin;

import fathertoast.crust.common.mixin_work.ClientMixinHooks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( WorldOpenFlows.class )
public class WorldOpenFlowsMixin {
    
    @Inject(
            method = "loadLevel",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    public void onLoadLevel( Screen screen, String worldId, CallbackInfo ci ) {
        ClientMixinHooks.handleOnLoadLevel( (WorldOpenFlows) (Object) this, screen, worldId, ci );
    }
}
