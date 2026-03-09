package fathertoast.crust.common.mixin_work;

import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeModifierMap;
import fathertoast.crust.api.event.advancement.AdvancementLoadEvent;
import fathertoast.crust.common.api.impl.event.advancement.ModifiableAdvancement;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class CommonMixinHooks {
    
    public static void handleAdvancementManagerRedirect( AdvancementList list, Map<ResourceLocation, Advancement.Builder> map ) {
        final HashMap<ResourceLocation, Advancement.Builder> newMap = new HashMap<>();
        
        map.forEach( ( id, builder ) -> {
            final ModifiableAdvancement modifiable = ModifiableAdvancement.copyFromBuilder( builder );
            final AdvancementLoadEvent event = new AdvancementLoadEvent( id, modifiable );
            MinecraftForge.EVENT_BUS.post( event );
            final Advancement.Builder newBuilder = ModifiableAdvancement.convertToVanilla( modifiable );
            newMap.put( id, newBuilder );
        } );
        list.add( newMap );
    }
    
    public static void collectConfigDrivenTypes( Map<EntityType<? extends LivingEntity>, AttributeSupplier> forgeAttributes,
                                                 Map<EntityType<? extends LivingEntity>, AttributeListField> configDrivenTypes ) {
        forgeAttributes.forEach( ( entityType, attributeSupplier ) -> {
            if( attributeSupplier instanceof ConfigDrivenAttributeModifierMap cfgDrivenSupplier ) {
                configDrivenTypes.put( entityType, cfgDrivenSupplier.getField() );
            }
        } );
    }
    
    public static void handleModifyAttributes( Map<EntityType<? extends LivingEntity>, AttributeSupplier> forgeAttributes,
                                               Map<EntityType<? extends LivingEntity>, AttributeListField> configDrivenTypes ) {
        configDrivenTypes.forEach( ( entityType, field ) -> {
            AttributeSupplier supplier = forgeAttributes.get( entityType );
            ConfigDrivenAttributeModifierMap cfgDrivenSupplier = new ConfigDrivenAttributeModifierMap( field, new AttributeSupplier.Builder( supplier ) );
            forgeAttributes.put( entityType, cfgDrivenSupplier );
        } );
    }
}
