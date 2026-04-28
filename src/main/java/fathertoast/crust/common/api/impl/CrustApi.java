package fathertoast.crust.common.api.impl;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.client.accessor.IClientConfigAccessor;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.common.api.impl.accessor.apocalypse.DifficultyAccessor;
import fathertoast.crust.common.core.Crust;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

public final class CrustApi implements ICrustApi {
    
    private final IDifficultyAccessor difficultyAccessor;
    
    public CrustApi() {
        if( ModList.get().isLoaded( "apocalypse" ) ) {
            // Do not instantiate unless Apocalypse is present
            difficultyAccessor = new DifficultyAccessor();
            Crust.LOG.info( "Instantiated Apocalypse Difficulty Accessor" );
        }
        else {
            difficultyAccessor = null;
        }
    }
    
    @Nullable
    @Override
    public IDifficultyAccessor getDifficultyAccessor() { return difficultyAccessor; }
    
    @Nullable
    @Override
    public IClientConfigAccessor getClientConfigAccessor() {
        if( FMLEnvironment.dist == Dist.CLIENT ) {
            return ClientRegister.CONFIG_ACCESSOR;
        }
        return null;
    }
}