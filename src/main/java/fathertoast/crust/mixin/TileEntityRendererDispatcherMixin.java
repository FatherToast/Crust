package fathertoast.crust.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.mixin_work.ClientMixinHooks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class TileEntityRendererDispatcherMixin {

    @Inject(method = "setupAndRender", at = @At(value = "HEAD"))
    private static void onRender(TileEntityRenderer<TileEntity> renderer, TileEntity tileEntity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, CallbackInfo ci) {
        ClientMixinHooks.handleTileEntityRendering(tileEntity, matrixStack, buffer);
    }
}
