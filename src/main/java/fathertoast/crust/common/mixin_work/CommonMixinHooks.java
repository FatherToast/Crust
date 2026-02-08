package fathertoast.crust.common.mixin_work;

import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeModifierMap;
import fathertoast.crust.api.event.AdvancementLoadEvent;
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
            AdvancementLoadEvent event = new AdvancementLoadEvent( id, builder );
            MinecraftForge.EVENT_BUS.post( event );
            
            // Clear requirements so they can be rebuilt
            event.getBuilder().requirements = null;
            newMap.put( event.getId(), event.getBuilder() );
        } );
        list.add( newMap );
    }
    
    public static AttributeSupplier handleModifyAttributes( Map<EntityType<? extends LivingEntity>, AttributeSupplier> forgeAttributes,
                                                            EntityType<? extends LivingEntity> type, AttributeSupplier supplier ) {
        AttributeSupplier originalAttributes = forgeAttributes.get( type );
        
        if( originalAttributes instanceof ConfigDrivenAttributeModifierMap configDrivenModMap ) {
            return new ConfigDrivenAttributeModifierMap( configDrivenModMap.getField(), new AttributeSupplier.Builder( supplier ) );
        }
        return supplier;
    }
}
