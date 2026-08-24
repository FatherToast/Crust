package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight} that gets
 * its min and max values from two int config fields, or a {@link IntField.RandomRange random range field}.
 * The plateau is hard-locked to 0 so that this provider only uses two fields.
 */
public class ConfigTriangleIntProvider extends IntProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigTriangleIntProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.INT_CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigTriangleIntProvider::getMinInclusive ),
            ConfigFieldReference.INT_CODEC.fieldOf( "max_inclusive" ).forGetter( ConfigTriangleIntProvider::getMaxInclusive )
    ).apply( inst, ConfigTriangleIntProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given random-range field. */
    public static ConfigTriangleIntProvider of( IntField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    /** Creates and returns a new instance that references the given fields. */
    public static ConfigTriangleIntProvider of( IConfigField<Integer> min, IConfigField<Integer> max ) {
        return new ConfigTriangleIntProvider( new ConfigFieldReference<>( min ), new ConfigFieldReference<>( max ) );
    }
    
    
    /** This provider's min value config field reference. */
    private final ConfigFieldReference<Integer> minInclusive;
    /** This provider's max value config field reference. */
    private final ConfigFieldReference<Integer> maxInclusive;
    
    /**
     * Constructs a new instance from the given min and max field references.
     *
     * @param min The min value field reference.
     * @param max The max value field reference.
     */
    private ConfigTriangleIntProvider( ConfigFieldReference<Integer> min, ConfigFieldReference<Integer> max ) {
        minInclusive = min;
        maxInclusive = max;
    }
    
    /** @return This provider's min value config field reference. */
    public ConfigFieldReference<Integer> getMinInclusive() { return minInclusive; }
    
    /** @return This provider's max value config field reference. */
    public ConfigFieldReference<Integer> getMaxInclusive() { return maxInclusive; }
    
    /** @return A sample value from this provider. */
    @Override
    public int sample( RandomSource random ) {
        Integer min = minInclusive.get();
        Integer max = maxInclusive.get();
        
        if( min == null || max == null ) {
            ConfigUtil.LOG.error( "Invalid triangle int range: {}", this );
            return 0;
        }
        if( min > max ) {
            ConfigUtil.LOG.warn( "Empty triangle int range: {}", this );
            return min;
        }
        int range = max - min;
        if( 0 >= range ) return min;
        int midrange = range / 2;
        return min + Mth.randomBetweenInclusive( random, 0, range - midrange ) +
                Mth.randomBetweenInclusive( random, 0, midrange );
    }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public int getMinValue() { return minInclusive.getOrElse( 0 ); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public int getMaxValue() { return maxInclusive.getOrElse( 0 ); }
    
    /** @return This provider's type. */
    @Override
    public IntProviderType<?> getType() { return CrustObjects.LevelGen.IntProviders.CFG_TRIANGLE.get(); }
    
    @Override // Object
    public String toString() { return "triangle (@" + minInclusive + "-@" + maxInclusive + ")"; }
}