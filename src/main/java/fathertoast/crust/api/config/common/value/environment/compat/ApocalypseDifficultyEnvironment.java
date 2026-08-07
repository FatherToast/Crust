package fathertoast.crust.api.config.common.value.environment.compat;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareLongEnvironment;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Notes on Apocalypse difficulty:
 * If Apocalypse is not installed or when no players are in the world, this will evaluate as false.
 * When position is not available, this evaluates against the lowest player difficulty in the world.
 * Otherwise, this evaluates against the nearest player's difficulty.
 */
public class ApocalypseDifficultyEnvironment extends CompareLongEnvironment {
    
    private static ICrustApi apiInstance;
    
    public static void register( ICrustApi instance ) { if( apiInstance == null ) apiInstance = instance; }
    
    
    public ApocalypseDifficultyEnvironment( ComparatorValue op, long value ) { super( op, value ); }
    
    public ApocalypseDifficultyEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return True if Apocalypse is installed. */
    protected boolean isApocalypseInstalled() { return apiInstance.getDifficultyAccessor() != null; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Long getActual( EnvironmentContext context ) {
        // Check if Apocalypse is installed and any players exist
        if( apiInstance.getDifficultyAccessor() == null || context.getFullLevel().players().isEmpty() ) return null;
        
        // Get nearest player, if a position is available
        if( context.getBlockPos() != null ) {
            return apiInstance.getDifficultyAccessor().getNearestPlayerDifficulty( context.getFullLevel(), context.getBlockPos() );
        }
        
        // Find player with the lowest difficulty, if we don't have a position
        long minDiff = Long.MAX_VALUE;
        for( Player player : context.getFullLevel().players() ) {
            long diff = apiInstance.getDifficultyAccessor().getPlayerDifficulty( player );
            if( diff < minDiff ) minDiff = diff;
        }
        return minDiff == Long.MAX_VALUE ? null : minDiff;
    }
}