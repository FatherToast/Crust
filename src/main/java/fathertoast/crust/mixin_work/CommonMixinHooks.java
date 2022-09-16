package fathertoast.crust.mixin_work;

import fathertoast.crust.api.event.AdvancementLoadEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class CommonMixinHooks {

    public static void handleAdvancementManagerRedirect(AdvancementList list, Map<ResourceLocation, Advancement.Builder> map) {
        final HashMap<ResourceLocation, Advancement.Builder> newMap = new HashMap<>();

        map.forEach((id, builder) -> {
            AdvancementLoadEvent event = new AdvancementLoadEvent(id, builder);
            MinecraftForge.EVENT_BUS.post(event);
            newMap.put(event.getId(), event.getBuilder());
        });
        list.add(newMap);
    }
}
