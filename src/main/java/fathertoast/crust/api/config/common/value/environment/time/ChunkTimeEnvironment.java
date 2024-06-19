package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareLongEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ChunkTimeEnvironment extends CompareLongEnvironment {
    
    public ChunkTimeEnvironment( ComparisonOperator op, long value ) { super( op, value ); }
    
    public ChunkTimeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected long getMinValue() { return 0L; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    public Long getActual( Level level, @Nullable BlockPos pos ) {
        // Ignore deprecation; this is intentionally the same method used by World#getCurrentDifficultyAt
        //noinspection deprecation
        return pos == null || !level.hasChunkAt( pos ) ? null : level.getChunkAt( pos ).getInhabitedTime();
    }
}