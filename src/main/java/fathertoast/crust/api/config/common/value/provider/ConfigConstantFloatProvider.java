package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.ConstantFloat} that gets
 * its value from a double field reference.
 */
public class ConfigConstantFloatProvider extends FloatProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigConstantFloatProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.DOUBLE_CODEC.fieldOf( "value" ).forGetter( ConfigConstantFloatProvider::get )
    ).apply( inst, ConfigConstantFloatProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given field. */
    public static ConfigConstantFloatProvider of( DoubleField value ) {
        return new ConfigConstantFloatProvider( new ConfigFieldReference<>( value ) );
    }
    
    
    /** This provider's config field reference. */
    private final ConfigFieldReference<Double> value;
    
    /**
     * Constructs a new instance from the given field reference.
     *
     * @param val The field reference acting as this provider's value.
     */
    private ConfigConstantFloatProvider( ConfigFieldReference<Double> val ) { value = val; }
    
    
    /** @return This provider's config field reference. */
    public ConfigFieldReference<Double> get() { return value; }
    
    /**
     * @return The current value of this provider's associated config field,
     * or a default value if something went wrong.
     */
    public float getValue() { return value.getOrElse( 0.0 ).floatValue(); }
    
    /** @return A sample value from this provider. */
    @Override
    public float sample( RandomSource random ) { return getValue(); }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public float getMinValue() { return getValue(); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public float getMaxValue() { return getValue() + 1.0F; } // if vanilla jumps off a bridge, we jump too
    
    /** @return This provider's type. */
    @Override
    public FloatProviderType<?> getType() { return CrustObjects.FloatProviders.CFG_CONSTANT.get(); }
    
    @Override // Object
    public String toString() { return "[@" + value + "]"; }
}