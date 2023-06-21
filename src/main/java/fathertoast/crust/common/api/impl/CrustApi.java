package fathertoast.crust.common.api.impl;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.common.api.impl.accessor.apocalypse.DifficultyAccessor;
import fathertoast.crust.common.core.Crust;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public final class CrustApi implements ICrustApi {
    
    private final IDifficultyAccessor difficultyAccessor;
    
    public CrustApi() {
        if( ModList.get().isLoaded( "apocalypse" ) ) {
            // Do not instantiate unless Apocalypse is present
            difficultyAccessor = new DifficultyAccessor();
            Crust.LOG.info( "Instantiated Apocalypse Rebooted Difficulty Accessor" );
        }
        else {
            difficultyAccessor = null;
        }
    }
    
    @Nullable
    @Override
    public IDifficultyAccessor getDifficultyAccessor() { return difficultyAccessor; }
}