package com.fathertoast.crust.common.event;

import com.fathertoast.crust.api.AdvancementLoadEvent;
import com.fathertoast.crust.common.core.Crust;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    @SubscribeEvent
    public void onAdvancementLoad(AdvancementLoadEvent event) {
        Crust.LOGGER.info("Advancement: " + event.getId().toString());
    }
}
