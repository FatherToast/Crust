package fathertoast.crust.common.worldgen.structure.processor;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class FeatureGeneratorProcessor extends StructureProcessor {
    
    public static final Codec<FeatureGeneratorProcessor> CODEC = Codec.unit( FeatureGeneratorProcessor::new );
    
    /** Called for each block placed in the structure. */
    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process( LevelReader level, BlockPos pos, BlockPos pos2,
                                                         StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo,
                                                         StructurePlaceSettings settings, @Nullable StructureTemplate template ) {
        // If block is not a feature generator or NBT is null we return early
        if( blockInfo.nbt() == null || !blockInfo.state().is( CrustObjects.Blocks.FEATURE_GENERATOR.get() ) )
            return blockInfo;
        
        // TODO - Test if it is safe to generate like this, or if we should
        //        delay the generation until the block entity has been created
        //        and its first tick runs.
        FeatureGeneratorBlockEntity.FeatureData data = FeatureGeneratorBlockEntity.FeatureData.newEmpty();
        data.loadFrom( blockInfo.nbt() );
        FeatureGeneratorBlockEntity.generate( level, pos, data, false );
        
        return new StructureTemplate.StructureBlockInfo( blockInfo.pos(), data.getTurnsInto(), null );
    }
    
    @Override
    protected StructureProcessorType<?> getType() {
        return CrustObjects.StructureProcessors.FEATURE_GEN_ACTIVATOR.get();
    }
}
