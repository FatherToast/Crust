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
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.heightproviders.UniformHeight} that gets
 * its min and max values from two int config fields, or a {@link IntField.RandomRange random range field}.
 */ // TODO add support for anchors (new config field type?)
public class ConfigUniformHeightProvider extends HeightProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigUniformHeightProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.INT_CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigUniformHeightProvider::getMinInclusive ),
            ConfigFieldReference.INT_CODEC.fieldOf( "max_inclusive" ).forGetter( ConfigUniformHeightProvider::getMaxInclusive )
    ).apply( inst, ConfigUniformHeightProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given random-range field. */
    public static ConfigUniformHeightProvider of( IntField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    /** Creates and returns a new instance that references the given fields. */
    public static ConfigUniformHeightProvider of( IConfigField<Integer> min, IConfigField<Integer> max ) {
        return new ConfigUniformHeightProvider( new ConfigFieldReference<>( min ), new ConfigFieldReference<>( max ) );
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
    private ConfigUniformHeightProvider( ConfigFieldReference<Integer> min, ConfigFieldReference<Integer> max ) {
        minInclusive = min;
        maxInclusive = max;
    }
    
    /** @return This provider's min value config field reference. */
    public ConfigFieldReference<Integer> getMinInclusive() { return minInclusive; }
    
    /** @return This provider's max value config field reference. */
    public ConfigFieldReference<Integer> getMaxInclusive() { return maxInclusive; }
    
    /** @return A sample value from this provider. */
    @Override
    public int sample( RandomSource random, WorldGenerationContext context ) {
        Integer min = minInclusive.get();
        Integer max = maxInclusive.get();
        
        if( min == null || max == null ) {
            ConfigUtil.LOG.error( "Invalid uniform height range: {}", this );
            return 0;
        }
        if( min > max ) {
            ConfigUtil.LOG.warn( "Empty uniform height range: {}", this );
            return min;
        }
        return Mth.randomBetweenInclusive( random, min, max );
    }
    
    /** @return This provider's type. */
    @Override
    public HeightProviderType<?> getType() { return CrustObjects.LevelGen.HeightProviders.CFG_UNIFORM.get(); }
    
    @Override // Object
    public String toString() { return "[@" + minInclusive + "-@" + maxInclusive + "]"; }
}