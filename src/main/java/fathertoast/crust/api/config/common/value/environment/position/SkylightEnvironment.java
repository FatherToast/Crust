package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareIntEnvironment;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

/**
 * This environment evaluates against the skylight level at the context's position
 * and ignores block light.
 *
 * @see SkylightEnvironment
 * @see BrightnessEnvironment
 */
public class SkylightEnvironment extends CompareIntEnvironment {
    
    public SkylightEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public SkylightEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Integer getActual( EnvironmentContext context ) {
        if( context.getBlockPos() == null ) return null;
        return context.getLevel().getBrightness( LightLayer.SKY, context.getBlockPos() );
    }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Integer> getValueCodec() { return BrightnessEnvironment.LIGHT_LEVEL; }
}
