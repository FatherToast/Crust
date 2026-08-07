package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.LongValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareLongEnvironment;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import javax.annotation.Nullable;

public class ChunkTimeEnvironment extends CompareLongEnvironment {
    
    public ChunkTimeEnvironment( ComparatorValue op, long value ) { super( op, value ); }
    
    public ChunkTimeEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Long> getValueCodec() { return LongValueCodec.NON_NEGATIVE; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Long getActual( EnvironmentContext context ) {
        ChunkAccess chunk = context.getBlockPos() == null ? null : context.getLevel().getChunk(
                SectionPos.blockToSectionCoord( context.getBlockPos().getX() ),
                SectionPos.blockToSectionCoord( context.getBlockPos().getZ() ),
                ChunkStatus.FULL, false );
        return chunk == null ? null : chunk.getInhabitedTime();
    }
}