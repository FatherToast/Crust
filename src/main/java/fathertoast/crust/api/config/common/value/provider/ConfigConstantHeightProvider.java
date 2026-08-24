package fathertoast.crust.api.config.common.value.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.heightproviders.ConstantHeight} that gets
 * its value from an int config field.
 */ // TODO add support for anchors (new config field type?)
public class ConfigConstantHeightProvider extends HeightProvider {
    
    /** The codec used for this provider's registered provider type. */
    public static final Codec<ConfigConstantHeightProvider> CODEC = RecordCodecBuilder.create( inst -> inst.group(
            ConfigFieldReference.INT_CODEC.fieldOf( "value" ).forGetter( ConfigConstantHeightProvider::get )
    ).apply( inst, ConfigConstantHeightProvider::new ) );
    
    
    /** Creates and returns a new instance that references the given field. */
    public static ConfigConstantHeightProvider of( IConfigField<Integer> value ) {
        return new ConfigConstantHeightProvider( new ConfigFieldReference<>( value ) );
    }
    
    
    /** This provider's config field reference. */
    private final ConfigFieldReference<Integer> value;
    
    /**
     * Constructs a new instance from the given field reference.
     *
     * @param val The field reference acting as this provider's value.
     */
    private ConfigConstantHeightProvider( ConfigFieldReference<Integer> val ) { value = val; }
    
    /** @return This provider's config field reference. */
    public ConfigFieldReference<Integer> get() { return value; }
    
    /**
     * @return The current value of this provider's associated config field,
     * or a default value if something went wrong.
     */
    public int getValue() { return value.getOrElse( 0 ); }
    
    /** @return A sample value from this provider. */
    @Override
    public int sample( RandomSource random, WorldGenerationContext context ) { return getValue(); }
    
    /** @return This provider's type. */
    @Override
    public HeightProviderType<?> getType() { return CrustObjects.LevelGen.HeightProviders.CFG_CONSTANT.get(); }
    
    @Override // Object
    public String toString() { return "@" + value; }
}