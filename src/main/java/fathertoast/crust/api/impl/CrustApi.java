package fathertoast.crust.api.impl;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.IRegistryHelper;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.impl.accessor.apocalypse.DifficultyAccessor;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public final class CrustApi implements ICrustApi {
    
    private final IRegistryHelper registryHelper;
    private final IDifficultyAccessor difficultyAccessor;
    
    public CrustApi() {
        registryHelper = new RegistryHelper();
        
        if( ModList.get().isLoaded( "apocalypse" ) ) {
            // Do not instantiate unless Apocalypse is present
            difficultyAccessor = new DifficultyAccessor();
            ConfigUtil.LOG.info( "Instantiated Apocalypse Rebooted Difficulty Accessor" );
        }
        else {
            difficultyAccessor = null;
        }
    }
    
    @Override
    public IRegistryHelper getRegistryHelper() { return registryHelper; }
    
    @Nullable
    @Override
    public IDifficultyAccessor getDifficultyAccessor() { return difficultyAccessor; }
}