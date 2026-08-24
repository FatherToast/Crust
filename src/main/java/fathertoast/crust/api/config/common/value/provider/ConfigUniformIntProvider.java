package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.UniformInt} that gets
 * its min and max values from two config double fields, or a {@link DoubleField.RandomRange random range field }.
 */
public class ConfigUniformIntProvider extends IntProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigUniformIntProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.INT_CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigUniformIntProvider::getMinInclusive ),
            ConfigFieldReference.INT_CODEC.fieldOf( "max_inclusive" ).forGetter( ConfigUniformIntProvider::getMaxInclusive )
    ).apply( inst, ConfigUniformIntProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given random-range field. */
    public static ConfigUniformIntProvider of( IntField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    /** Creates and returns a new instance that references the given fields. */
    public static ConfigUniformIntProvider of( IntField min, IntField max ) {
        return new ConfigUniformIntProvider( new ConfigFieldReference<>( min ), new ConfigFieldReference<>( max ) );
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
    private ConfigUniformIntProvider( ConfigFieldReference<Integer> min, ConfigFieldReference<Integer> max ) {
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
            ConfigUtil.LOG.error( "Invalid uniform int range: {}", this );
            return Integer.MAX_VALUE;
        }
        if( min > max ) {
            ConfigUtil.LOG.warn( "Empty uniform int range: {}", this );
            return min;
        }
        return Mth.randomBetweenInclusive( random, min, max );
    }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public int getMinValue() { return minInclusive.getOrElse( Integer.MAX_VALUE ); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public int getMaxValue() { return maxInclusive.getOrElse( Integer.MIN_VALUE ); }
    
    /** @return This provider's type. */
    @Override
    public IntProviderType<?> getType() { return CrustObjects.IntProviders.CFG_UNIFORM.get(); }
    
    @Override // Object
    public String toString() { return "[@" + minInclusive + "-@" + maxInclusive + "]"; }
}