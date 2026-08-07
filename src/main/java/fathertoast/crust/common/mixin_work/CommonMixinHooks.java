package fathertoast.crust.common.mixin_work;

import fathertoast.crust.api.config.common.field.collection.AttributeOpListField;
import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeSupplier;
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
                                                 Map<EntityType<? extends LivingEntity>, AttributeOpListField> configByEntityType ) {
        forgeAttributes.forEach( ( entityType, attributeSupplier ) -> {
            if( attributeSupplier instanceof ConfigDrivenAttributeSupplier cfgDrivenSupplier ) {
                configByEntityType.put( entityType, cfgDrivenSupplier.getField() );
            }
        } );
    }
    
    public static void handleModifyAttributes( Map<EntityType<? extends LivingEntity>, AttributeSupplier> forgeAttributes,
                                               Map<EntityType<? extends LivingEntity>, AttributeOpListField> configByEntityType ) {
        configByEntityType.forEach( ( entityType, field ) -> {
            AttributeSupplier supplier = forgeAttributes.get( entityType );
            if( supplier != null ) {
                forgeAttributes.put( entityType, field.build( new AttributeSupplier.Builder( supplier ) ) );
            }
        } );
    }
}