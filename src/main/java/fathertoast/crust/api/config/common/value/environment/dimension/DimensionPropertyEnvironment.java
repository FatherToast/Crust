package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DimensionPropertyEnvironment extends EnumEnvironment<DimensionPropertyEnvironment.Value> {
    
    /**
     * Represents all boolean values defined by dimension type, named to match data pack format.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Custom_dimension#Syntax">Data pack format (Minecraft Wiki)</a>
     */
    @SuppressWarnings("SpellCheckingInspection")
    public enum Value {
        ULTRAWARM( DimensionType::ultraWarm ),
        NATURAL( DimensionType::natural ),
        HAS_SKYLIGHT( DimensionType::hasSkyLight ),
        HAS_CEILING( DimensionType::hasCeiling ),
        FIXED_TIME( DimensionType::hasFixedTime ),
        PIGLIN_SAFE( DimensionType::piglinSafe ),
        BED_WORKS( DimensionType::bedWorks ),
        RESPAWN_ANCHOR_WORKS( DimensionType::respawnAnchorWorks ),
        HAS_RAIDS( DimensionType::hasRaids );
        
        private final Function<DimensionType, Boolean> SUPPLIER;
        
        Value( Function<DimensionType, Boolean> supplier ) { SUPPLIER = supplier; }
        
        public boolean of( DimensionType dimType ) { return SUPPLIER.apply( dimType ); }
    }
    
    public DimensionPropertyEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public DimensionPropertyEnvironment( AbstractConfigField field, String line ) { super( field, line, Value.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) { return VALUE.of( level.dimensionType() ) != INVERT; }
}