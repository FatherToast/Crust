package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class PositionEnvironment extends EnumEnvironment<PositionEnvironment.Value> {
    
    public enum Value {
        CAN_SEE_SKY( ( level, pos ) -> pos != null && level.canSeeSky( pos ) ),
        IS_IN_VILLAGE( ( level, pos ) -> pos != null && level instanceof ServerLevel && ((ServerLevel) level).isVillage( pos ) ),
        IS_NEAR_VILLAGE( ( level, pos ) -> pos != null && level instanceof ServerLevel &&
                ((ServerLevel) level).isCloseToVillage( pos, 3 ) ),
        IS_NEAR_RAID( ( level, pos ) -> pos != null && level instanceof ServerLevel && ((ServerLevel) level).isRaided( pos ) ),
        IS_IN_WATER( ( level, pos ) -> pos != null && level.getFluidState( pos ).is( FluidTags.WATER ) ),
        IS_IN_LAVA( ( level, pos ) -> pos != null && level.getFluidState( pos ).is( FluidTags.LAVA ) ),
        IS_IN_FLUID( ( level, pos ) -> pos != null && !level.getFluidState( pos ).isEmpty() ),
        HAS_REDSTONE_POWER( ( level, pos ) -> pos != null && level.getDirectSignalTo( pos ) > 0 );
        
        private final BiFunction<Level, BlockPos, Boolean> SUPPLIER;
        
        Value( BiFunction<Level, BlockPos, Boolean> supplier ) { SUPPLIER = supplier; }
        
        public boolean of( Level world, @Nullable BlockPos pos ) { return SUPPLIER.apply( world, pos ); }
    }
    
    public PositionEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public PositionEnvironment( AbstractConfigField field, String line ) { super( field, line, Value.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) { return VALUE.of( level, pos ) != INVERT; }
}