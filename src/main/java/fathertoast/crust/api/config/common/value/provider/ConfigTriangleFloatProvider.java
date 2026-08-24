package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.TrapezoidFloat} that gets
 * its min and max values from two double config fields, or a {@link DoubleField.RandomRange random range field}.
 * The plateau is hard-locked to 0 so that this provider only uses two fields.
 */
public class ConfigTriangleFloatProvider extends FloatProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigTriangleFloatProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigTriangleFloatProvider::getMinInclusive ),
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "max_exclusive" ).forGetter( ConfigTriangleFloatProvider::getMaxExclusive )
    ).apply( inst, ConfigTriangleFloatProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given random-range field. */
    public static ConfigTriangleFloatProvider of( DoubleField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    /** Creates and returns a new instance that references the given fields. */
    public static ConfigTriangleFloatProvider of( IConfigField<Double> min, IConfigField<Double> max ) {
        return new ConfigTriangleFloatProvider( new ConfigFieldReference<>( min ), new ConfigFieldReference<>( max ) );
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
    private ConfigTriangleFloatProvider( ConfigFieldReference<Double> min, ConfigFieldReference<Double> max ) {
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
            ConfigUtil.LOG.error( "Invalid triangle float range: {}", this );
            return 0.0F;
        }
        if( min > max ) {
            ConfigUtil.LOG.warn( "Empty triangle float range: {}", this );
            return min.floatValue();
        }
        float range = max.floatValue() - min.floatValue();
        float midrange = range / 2.0F;
        return min.floatValue() + random.nextFloat() * (range - midrange) + random.nextFloat() * midrange;
    }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public float getMinValue() { return minInclusive.getOrElse( 0.0 ).floatValue(); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public float getMaxValue() { return maxExclusive.getOrElse( 0.0 ).floatValue(); }
    
    /** @return This provider's type. */
    @Override
    public FloatProviderType<?> getType() { return CrustObjects.LevelGen.FloatProviders.CFG_TRIANGLE.get(); }
    
    @Override // Object
    public String toString() { return "triangle (@" + minInclusive + "-@" + maxExclusive + ")"; }
}