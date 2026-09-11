package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareIntEnvironment;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

/**
 * This environment evaluates against the block light level at the context's
 * position and ignores skylight.
 *
 * @see SkylightEnvironment
 * @see BrightnessEnvironment
 */
public class BlockLightEnvironment extends CompareIntEnvironment {
    
    public BlockLightEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public BlockLightEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Integer getActual( EnvironmentContext context ) {
        if( context.getBlockPos() == null ) return null;
        return context.getLevel().getBrightness( LightLayer.BLOCK, context.getBlockPos() );
    }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Integer> getValueCodec() { return BrightnessEnvironment.LIGHT_LEVEL; }
}
