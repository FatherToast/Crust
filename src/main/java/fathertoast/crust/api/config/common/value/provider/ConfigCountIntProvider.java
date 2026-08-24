package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * A config-driven int provider that samples a count value from a double config field.
 * <p>
 * When the count has an integer value, it will simply provide the integer value.
 * Otherwise, it provides an integer that is on average equal to the double value
 * (for example, a count of 5.6 is 40% chance for 5 and 60% chance for 6).
 */
public class ConfigCountIntProvider extends IntProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigCountIntProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "count" ).forGetter( ConfigCountIntProvider::get )
    ).apply( inst, ConfigCountIntProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given field. */
    public static ConfigCountIntProvider of( IConfigField<Double> count ) {
        return new ConfigCountIntProvider( new ConfigFieldReference<>( count ) );
    }
    
    
    /** This provider's config field reference. */
    private final ConfigFieldReference<Double> value;
    
    /**
     * Constructs a new instance from the given field reference.
     *
     * @param count The field reference acting as this provider's value.
     */
    private ConfigCountIntProvider( ConfigFieldReference<Double> count ) { value = count; }
    
    /** @return This provider's config field reference. */
    public ConfigFieldReference<Double> get() { return value; }
    
    /**
     * @return The current value of this provider's associated config field,
     * or a default value if something went wrong.
     */
    public float getValue() { return value.getOrElse( 0.0 ).floatValue(); }
    
    /** @return A sample value from this provider. */
    @Override
    public int sample( RandomSource random ) {
        int min = getMinValue();
        float residual = getValue() - min;
        return residual > Float.MIN_NORMAL && random.nextFloat() < residual ? min + 1 : min;
    }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public int getMinValue() { return Mth.floor( getValue() ); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public int getMaxValue() { return Mth.ceil( getValue() ); }
    
    /** @return This provider's type. */
    @Override
    public IntProviderType<?> getType() { return CrustObjects.LevelGen.IntProviders.CFG_COUNT.get(); }
    
    @Override // Object
    public String toString() { return "x@" + value; }
}