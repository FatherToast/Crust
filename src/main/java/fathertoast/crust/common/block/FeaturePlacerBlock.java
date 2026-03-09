package fathertoast.crust.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FeaturePlacerBlock extends Block implements EntityBlock {
    
    public FeaturePlacerBlock() {
        super( BlockBehaviour.Properties.of()
                .strength( 1.0F )
                .sound( SoundType.AMETHYST )
        );
    }
    
    @Override
    @Nullable
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return null;
    }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return null; // EntityBlock.super.getTicker( p_153212_, p_153213_, p_153214_ );
    }
}
