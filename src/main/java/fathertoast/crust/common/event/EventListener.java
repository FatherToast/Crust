package fathertoast.crust.common.event;

import fathertoast.crust.api.event.AdvancementLoadEvent;
import fathertoast.crust.common.core.Crust;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

// TODO - TEMPORARY: this is just for testing advancement load stuff.
public class EventListener {

    @SubscribeEvent
    public void onAdvancementLoad(AdvancementLoadEvent event) {
        if (event.getId().toString().equals("minecraft:adventure/kill_a_mob")) {
            Advancement.Builder builder = event.getBuilder();
            Map<String, Criterion> criteria = builder.getCriteria();

            criteria.clear();
            builder.addCriterion("cookie", InventoryChangeTrigger.Instance.hasItems(Items.COOKIE));

            Crust.LOG.info("Builder: " + builder);
        }
    }
}
