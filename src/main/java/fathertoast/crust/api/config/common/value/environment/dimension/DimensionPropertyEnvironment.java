package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.PredicateEnumEnvironment;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DimensionPropertyEnvironment extends PredicateEnumEnvironment<DimensionPropertyEnvironment.Value> {
    /**
     * Represents all boolean values defined by dimension type, named to match data pack format.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Custom_dimension#Syntax">Data pack format (Minecraft Wiki)</a>
     */
    @SuppressWarnings( "SpellCheckingInspection" )
    public enum Value implements Predicate<EnvironmentContext> {
        ULTRAWARM( DimensionType::ultraWarm ),
        NATURAL( DimensionType::natural ),
        HAS_SKYLIGHT( DimensionType::hasSkyLight ),
        HAS_CEILING( DimensionType::hasCeiling ),
        FIXED_TIME( DimensionType::hasFixedTime ),
        PIGLIN_SAFE( DimensionType::piglinSafe ),
        BED_WORKS( DimensionType::bedWorks ),
        RESPAWN_ANCHOR_WORKS( DimensionType::respawnAnchorWorks ),
        HAS_RAIDS( DimensionType::hasRaids );
        
        private final Predicate<DimensionType> PREDICATE;
        
        Value( Predicate<DimensionType> supplier ) { PREDICATE = supplier; }
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) { return PREDICATE.test( context.getLevel().dimensionType() ); }
    }
    
    public DimensionPropertyEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public DimensionPropertyEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value, Value.values() ); }
}