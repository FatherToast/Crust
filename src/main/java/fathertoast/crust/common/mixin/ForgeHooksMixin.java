package fathertoast.crust.common.mixin;

import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeModifierMap;
import fathertoast.crust.common.mixin_work.CommonMixinHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * This mixin exists to preserve {@link ConfigDrivenAttributeModifierMap} instances
 * that are registered during {@link net.minecraftforge.event.entity.EntityAttributeCreationEvent} but later lost
 * from merging attributes after {@link net.minecraftforge.event.entity.EntityAttributeModificationEvent}.
 */
@Mixin( ForgeHooks.class )
public class ForgeHooksMixin {
    
    @Shadow
    @Final
    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES;
    
    @Unique
    private static Map<EntityType<? extends LivingEntity>, AttributeListField> CONFIG_DRIVEN_TYPES = new HashMap<>();
    
    
    @Inject(
            method = "modifyAttributes",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/event/entity/EntityAttributeModificationEvent;<init>(Ljava/util/Map;)V",
                    ordinal = 0
            )
    )
    private static void crust$onModifyAttributesFirst( CallbackInfo ci ) {
        CommonMixinHooks.collectConfigDrivenTypes( FORGE_ATTRIBUTES, CONFIG_DRIVEN_TYPES );
    }
    
    @Inject(
            method = "modifyAttributes",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void crust$onModifyAttributesSecond( CallbackInfo ci ) {
        CommonMixinHooks.handleModifyAttributes( FORGE_ATTRIBUTES, CONFIG_DRIVEN_TYPES );
    }
}
