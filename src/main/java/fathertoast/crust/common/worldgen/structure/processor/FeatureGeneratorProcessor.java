package fathertoast.crust.common.worldgen.structure.processor;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
        // Check if block is a feature generator and has NBT
        if( blockInfo.nbt() != null && blockInfo.state().is( CrustObjects.Blocks.FEATURE_GENERATOR.get() ) ) {
            CompoundTag tag = blockInfo.nbt();
            tag.putBoolean( FeatureGeneratorBlockEntity.KEY_READY_FOR_GENERATION, true );
            return new StructureTemplate.StructureBlockInfo( blockInfo.pos(), blockInfo.state(), blockInfo.nbt() );
        }
        return blockInfo;
    }
    
    @Override
    protected StructureProcessorType<?> getType() {
        return CrustObjects.StructureProcessors.FEATURE_GEN_ACTIVATOR.get();
    }
}
