package fathertoast.crust.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fathertoast.crust.common.mixin_work.CommonMixinHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

/**
 * This mixin exists to preserve {@link fathertoast.crust.api.config.common.value.ConfigDrivenAttributeModifierMap} instances
 * that are registered during {@link net.minecraftforge.event.entity.EntityAttributeCreationEvent} but later lost
 * from merging after {@link net.minecraftforge.event.entity.EntityAttributeModificationEvent}.
 */
@Mixin( ForgeHooks.class )
public class ForgeHooksMixin {
    
    @Shadow
    @Final
    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES;
    
    @ModifyExpressionValue(
            method = "lambda$modifyAttributes$7",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;build()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;"
            )
    )
    private static AttributeSupplier crust$modifyexp_modifyAttributes( AttributeSupplier original,
                                                                       EntityType<? extends LivingEntity> entityType,
                                                                       AttributeSupplier.Builder builder ) {
        return CommonMixinHooks.handleModifyAttributes(
                FORGE_ATTRIBUTES,
                entityType,
                original
        );
    }
}
