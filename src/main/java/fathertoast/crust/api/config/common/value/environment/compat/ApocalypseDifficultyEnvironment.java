package fathertoast.crust.api.config.common.value.environment.compat;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareLongEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Notes on apocalypse difficulty:
 * If Apocalypse Rebooted is not installed, this condition is ignored (always evaluates to true).
 * When no players are in the world, this will evaluate as false.
 * When position is not available, this evaluates against the lowest player difficulty in the world.
 * Otherwise, this evaluates against the nearest player's difficulty.
 */
public class ApocalypseDifficultyEnvironment extends CompareLongEnvironment {
    
    private static ICrustApi apiInstance;
    
    public static void register( ICrustApi instance ) { if( apiInstance == null ) apiInstance = instance; }
    
    
    public ApocalypseDifficultyEnvironment( ComparisonOperator op, long value ) { super( op, value ); }
    
    public ApocalypseDifficultyEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    // Min and max values should not be specified, since they are dependent on AR configs.
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( World world, @Nullable BlockPos pos ) { return apiInstance.getDifficultyAccessor() == null || super.matches( world, pos ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Nullable
    public Long getActual( World world, @Nullable BlockPos pos ) {
        if( apiInstance.getDifficultyAccessor() == null ) return null;
        
        // Check if any players exist
        if( world.players().size() == 0 ) return null;
        
        // Get nearest player, if a position is available
        if( pos != null ) {
            return apiInstance.getDifficultyAccessor().getNearestPlayerDifficulty( world, pos );
        }
        
        // Find player with lowest difficulty, if we don't have a position
        long minDiff = Long.MAX_VALUE;
        for( PlayerEntity player : world.players() ) {
            long diff = apiInstance.getDifficultyAccessor().getPlayerDifficulty( player );
            if( diff < minDiff ) minDiff = diff;
        }
        return minDiff;
    }
}