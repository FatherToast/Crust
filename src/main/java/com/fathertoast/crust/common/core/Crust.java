package com.fathertoast.crust.common.core;

import com.fathertoast.crust.common.core.config.IToastyConfigInterface;
import com.fathertoast.crust.common.event.EventListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Crust.MODID)
public class Crust {

    public static final String MODID = "crust";

    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public Crust() {
        // TODO - Make even toastier; toast the computer!
        IToastyConfigInterface.toastOverload();

        MinecraftForge.EVENT_BUS.register(new EventListener());
    }
}
