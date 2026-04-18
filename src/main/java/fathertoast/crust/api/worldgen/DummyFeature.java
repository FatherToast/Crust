package fathertoast.crust.api.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;


/**
 * This empty feature is primarily used to fill the optional in {@link FeaturePlaceContext} with a
 * non-null value to enable "force-placing" placement logic for DeadlyWorld features.
 */
public class DummyFeature extends Feature<DummyFeature.Configuration> {
    public record Configuration() implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance
                .stable( new DummyFeature.Configuration() ) );
    }
    
    /** The singleton dummy configured feature. */
    public static final ConfiguredFeature<DummyFeature.Configuration, DummyFeature> CONFIGURED_INSTANCE =
            new ConfiguredFeature<>( new DummyFeature(), new DummyFeature.Configuration() );
    
    public DummyFeature() { this( DummyFeature.Configuration.CODEC ); }
    
    public DummyFeature( Codec<DummyFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) { return false; }
}