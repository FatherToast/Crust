package fathertoast.crust.common.mixin;

import com.google.gson.Gson;
import fathertoast.crust.common.mixin_work.CommonMixinHooks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin( ServerAdvancementManager.class )
public abstract class ServerAdvancementManagerMixin extends SimpleJsonResourceReloadListener {
    
    public ServerAdvancementManagerMixin( Gson gson, String s ) {
        super( gson, s );
    }
    
    @Redirect(
            require = 1,
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/AdvancementList;add(Ljava/util/Map;)V",
                    ordinal = 0
            )
    )
    public void crust$redirect_apply( AdvancementList instance, Map<ResourceLocation, Advancement.Builder> entry ) {
        CommonMixinHooks.handleAdvancementManagerRedirect( instance, entry );
    }
}
