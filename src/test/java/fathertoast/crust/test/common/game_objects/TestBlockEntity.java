package fathertoast.crust.test.common.game_objects;

import fathertoast.crust.api.util.IBlockEntityBBProvider;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.test.common.TestRegistryObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class TestBlockEntity extends BlockEntity implements IBlockEntityBBProvider {

    private AABB renderBox = null;

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(TestRegistryObjects.TEST_BE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        renderBox = new AABB(getBlockPos()).inflate(3.0D, 2.0D, 3.0D);
    }

    @Nullable
    @Override
    public List<AABB> getBoundingBoxes() {
        return List.of(renderBox);
    }
}
