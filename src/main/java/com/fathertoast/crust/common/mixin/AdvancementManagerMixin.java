package com.fathertoast.crust.common.mixin;

import com.fathertoast.crust.common.util.mixin_work.CommonMixinHooks;
import com.google.gson.Gson;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Map;

@Mixin(AdvancementManager.class)
public abstract class AdvancementManagerMixin extends JsonReloadListener {

    public AdvancementManagerMixin(Gson gson, String s) {
        super(gson, s);
    }

    @Redirect(
            method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementList;<init>()V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementList;add(Ljava/util/Map;)V")
    )
    public void onApply(AdvancementList instance, Map<ResourceLocation, Advancement.Builder> entry) {
        CommonMixinHooks.handleAdvancementManagerRedirect(instance, entry);
    }
}
