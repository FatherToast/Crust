package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.PredicateEnumEnvironment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PositionEnvironment extends PredicateEnumEnvironment<PositionEnvironment.Value> {
    
    @SuppressWarnings( "DataFlowIssue" ) // We ensure context.getBlockPos() != null prior to testing any of these
    public enum Value implements Predicate<EnvironmentContext> {
        CAN_SEE_SKY( context ->
                context.getLevel().canSeeSky( context.getBlockPos() ) ),
        IS_IN_VILLAGE( context -> context.getLevel() instanceof ServerLevel level &&
                level.isVillage( context.getBlockPos() ) ),
        IS_NEAR_VILLAGE( context -> context.getLevel() instanceof ServerLevel level &&
                level.isCloseToVillage( context.getBlockPos(), 3 ) ),
        IS_NEAR_RAID( context -> context.getLevel() instanceof ServerLevel level &&
                level.isRaided( context.getBlockPos() ) ),
        IS_IN_WATER( context ->
                context.getLevel().getFluidState( context.getBlockPos() ).is( FluidTags.WATER ) ),
        IS_IN_LAVA( context ->
                context.getLevel().getFluidState( context.getBlockPos() ).is( FluidTags.LAVA ) ),
        IS_IN_FLUID( context ->
                !context.getLevel().getFluidState( context.getBlockPos() ).isEmpty() ),
        HAS_REDSTONE_POWER( context ->
                context.getLevel().getDirectSignalTo( context.getBlockPos() ) > 0 );
        
        private final Predicate<EnvironmentContext> PREDICATE;
        
        Value( Predicate<EnvironmentContext> supplier ) { PREDICATE = supplier; }
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) {
            return context.getBlockPos() != null && PREDICATE.test( context );
        }
    }
    
    public PositionEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public PositionEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value, Value.values() ); }
}