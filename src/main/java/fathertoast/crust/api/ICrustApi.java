package fathertoast.crust.api;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;

/**
 * The main interface of Crust's API.<br
 * <br>
 * The API instance gets parsed to
 * all registered Crust plugins during
 * {@link FMLCommonSetupEvent}
 */
public interface ICrustApi {
    
    /** The mod's id. */
    String MOD_ID = "crust";
    
    @Nullable
    IDifficultyAccessor getDifficultyAccessor();
}