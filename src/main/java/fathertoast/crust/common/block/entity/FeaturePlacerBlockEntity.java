package fathertoast.crust.common.block.entity;

import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FeaturePlacerBlockEntity extends BlockEntity {
    
    public FeaturePlacerBlockEntity( BlockPos pos, BlockState state ) {
        super( CrustObjects.BlockEntities.FEATURE_PLACER.get(), pos, state );
    }
}
