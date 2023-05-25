package fathertoast.crust.api.config.common.value.environment.compat;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.CompareLongEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Notes on apocalypse difficulty:
 * If Apocalypse Rebooted is not installed, this condition is ignored (always evaluates to true).
 * When no players are in the world, difficulty is assumed to be 0.
 * When position is not available, this evaluates against the minimum player difficulty value.
 * Otherwise, this evaluates against the nearest player's difficulty.
 */
public class ApocalypseDifficultyEnvironment extends CompareLongEnvironment {
    
    private static ICrustApi apiInstance;
    
    public static void register( ICrustApi instance ) { if( apiInstance == null ) apiInstance = instance; }
    
    
    public ApocalypseDifficultyEnvironment( ComparisonOperator op, long value ) { super( op, value ); }
    
    public ApocalypseDifficultyEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    protected long getMinValue() { return 0L; }
    
    // Max value cannot be specified.
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_APOCALYPSE_DIFFICULTY; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( World world, @Nullable BlockPos pos ) { return apiInstance.getDifficultyAccessor() == null || super.matches( world, pos ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Nullable
    public Long getActual( World world, @Nullable BlockPos pos ) {
        if( apiInstance.getDifficultyAccessor() == null ) return null;
        
        // Check if any players exist
        if( world.players().size() == 0 ) return 0L;
        
        // Get nearest player, if a position is available
        if( pos != null ) {
            return Math.max( 0L, apiInstance.getDifficultyAccessor().getNearestPlayerDifficulty( world, pos ) );
        }
        
        // Find player with lowest difficulty
        long minDiff = Long.MAX_VALUE;
        for( PlayerEntity player : world.players() ) {
            long diff = apiInstance.getDifficultyAccessor().getPlayerDifficulty( player );
            if( diff <= 0 ) return 0L;
            if( diff < minDiff ) minDiff = diff;
        }
        return minDiff;
    }
}