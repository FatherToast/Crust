package fathertoast.crust.api;

import fathertoast.crust.api.client.accessor.IClientConfigAccessor;
import fathertoast.crust.api.util.OnClient;
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
    
    /**
     * If Apocalypse is installed, this can be used to easily
     * access a player's difficulty and misc. event data.
     *
     * @return The Apocalypse difficulty accessor, if it exists.
     * Returns null otherwise.
     */
    @Nullable
    IDifficultyAccessor getDifficultyAccessor();
    
    /**
     * @return Crust's client configs wrapped in an easy-to-read
     * interface to read their values.
     * <br><br>
     * If called on dedicated server or before
     * Crust's client configs have been initialized (during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}),
     * this will return null.
     */
    @OnClient
    @Nullable
    IClientConfigAccessor getClientConfigAccessor();
}