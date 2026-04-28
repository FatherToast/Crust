package fathertoast.crust.common.api.impl;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.client.accessor.IClientConfigAccessor;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.common.api.impl.accessor.apocalypse.DifficultyAccessor;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.Function;

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
        enqueueSetup();
    }
    
    
    //----------------------------------------------------------------------------
    //                         ICrustApi implementation
    //----------------------------------------------------------------------------
    
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
    
    
    //----------------------------------------------------------------------------
    //                           Misc external setup
    //----------------------------------------------------------------------------
    
    @SuppressWarnings( "deprecation" )
    private static final Function<RandomSource, Long> RANDOM_SOURCE_SEED_GETTER = ( random ) -> {
        if( random instanceof LegacyRandomSource rng )
            return rng.seed.get();
        else if( random instanceof SingleThreadedRandomSource rng )
            return rng.seed;
        else if( random instanceof ThreadSafeLegacyRandomSource rng )
            return rng.seed.get();
        return random.nextLong();
    };
    
    /** Enqueues various setup to happen on the main thread, for misc mod loading stages. */
    private static void enqueueSetup() {
        ModLoadingStage.CONSTRUCT.getDeferredWorkQueue().enqueueWork( Crust.INSTANCE.CONTAINER, CrustApi::injectCrustMathChanges );
    }
    
    /**
     * Helper method for modifying private fields in CrustMath.
     */
    private static void injectCrustMathChanges() {
        try {
            Field field = CrustMath.class.getDeclaredField( "RANDOM_SOURCE_SEED_GETTER" );
            field.setAccessible( true );
            field.set( null, RANDOM_SOURCE_SEED_GETTER );
        }
        catch( Exception e ) {
            throw new IllegalStateException( "Failed to reflectively alter CrustMath!" );
        }
    }
}