package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.UniformFloat} that gets
 * its min and max values from two config double fields, or a {@link DoubleField.RandomRange random range field}.
 */
public class ConfigUniformFloatProvider extends FloatProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigUniformFloatProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigUniformFloatProvider::getMinInclusive ),
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "max_exclusive" ).forGetter( ConfigUniformFloatProvider::getMaxExclusive )
    ).apply( inst, ConfigUniformFloatProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given random-range field. */
    public static ConfigUniformFloatProvider of( DoubleField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    /** Creates and returns a new instance that references the given fields. */
    public static ConfigUniformFloatProvider of( DoubleField min, DoubleField max ) {
        return new ConfigUniformFloatProvider( new ConfigFieldReference<>( min ), new ConfigFieldReference<>( max ) );
    }
    
    
    /** This provider's min value config field reference. */
    private final ConfigFieldReference<Double> minInclusive;
    /** This provider's max value config field reference. */
    private final ConfigFieldReference<Double> maxExclusive;
    
    /**
     * Constructs a new instance from the given min and max field references.
     *
     * @param min The min value field reference.
     * @param max The max value field reference.
     */
    private ConfigUniformFloatProvider( ConfigFieldReference<Double> min, ConfigFieldReference<Double> max ) {
        minInclusive = min;
        maxExclusive = max;
    }
    
    
    /** @return This provider's min value config field reference. */
    public ConfigFieldReference<Double> getMinInclusive() { return minInclusive; }
    
    /** @return This provider's max value config field reference. */
    public ConfigFieldReference<Double> getMaxExclusive() { return maxExclusive; }
    
    /** @return A sample value from this provider. */
    @Override
    public float sample( RandomSource random ) {
        Double min = minInclusive.get();
        Double max = maxExclusive.get();
        
        if( min == null || max == null ) {
            ConfigUtil.LOG.error( "Invalid uniform int range: {}", this );
            return Integer.MAX_VALUE;
        }
        if( min > max ) {
            ConfigUtil.LOG.warn( "Empty uniform int range: {}", this );
            return min.floatValue();
        }
        return Mth.randomBetween( random, min.floatValue(), max.floatValue() );
    }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public float getMinValue() { return minInclusive.getOrElse( (double) Float.MAX_VALUE ).floatValue(); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public float getMaxValue() { return maxExclusive.getOrElse( (double) Float.MIN_VALUE ).floatValue(); }
    
    /** @return This provider's type. */
    @Override
    public FloatProviderType<?> getType() { return CrustObjects.FloatProviders.CFG_UNIFORM.get(); }
    
    @Override // Object
    public String toString() { return "[@" + minInclusive + "-@" + maxExclusive + "]"; }
}