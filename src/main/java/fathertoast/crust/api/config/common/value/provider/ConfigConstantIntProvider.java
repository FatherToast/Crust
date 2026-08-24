package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.ConstantInt} that gets
 * its value from an int field reference.
 */
public class ConfigConstantIntProvider extends IntProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigConstantIntProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.INT_CODEC.fieldOf( "value" ).forGetter( ConfigConstantIntProvider::get )
    ).apply( inst, ConfigConstantIntProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given field. */
    public static ConfigConstantIntProvider of( IntField value ) {
        return new ConfigConstantIntProvider( new ConfigFieldReference<>( value ) );
    }
    
    
    /** This provider's config field reference. */
    private final ConfigFieldReference<Integer> value;
    
    /**
     * Constructs a new instance from the given field reference.
     *
     * @param val The field reference acting as this provider's value.
     */
    private ConfigConstantIntProvider( ConfigFieldReference<Integer> val ) { value = val; }
    
    
    /** @return This provider's config field reference. */
    public ConfigFieldReference<Integer> get() { return value; }
    
    /**
     * @return The current value of this provider's associated config field,
     * or a default value if something went wrong.
     */
    public int getValue() { return value.getOrElse( 0 ); }
    
    /** @return A sample value from this provider. */
    @Override
    public int sample( RandomSource random ) { return getValue(); }
    
    /** @return The minimum value that can be returned by this provider. */
    @Override
    public int getMinValue() { return getValue(); }
    
    /** @return The maximum value that can be returned by this provider. */
    @Override
    public int getMaxValue() { return getValue(); }
    
    /** @return This provider's type. */
    @Override
    public IntProviderType<?> getType() { return CrustObjects.IntProviders.CFG_CONSTANT.get(); }
    
    @Override // Object
    public String toString() {
        return "[@" + value + "]";
    }
}