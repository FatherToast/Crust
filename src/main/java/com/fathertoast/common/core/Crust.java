package com.fathertoast.common.core;

import com.fathertoast.common.core.config.IToastyConfigInterface;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Crust.MODID)
public class Crust {

    public static final String MODID = "crust";

    private static final Logger LOGGER = LogManager.getLogger(MODID);


    public Crust() {
        // TODO - Make even toastier; toast the computer!
        IToastyConfigInterface.toastOverload();
    }
}
