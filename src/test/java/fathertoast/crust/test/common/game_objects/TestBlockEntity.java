package fathertoast.crust.test.common.game_objects;

import fathertoast.crust.api.util.IBlockEntityDebugShapeProvider;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.SphereMeshShape;
import fathertoast.crust.test.common.TestRegistryObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class TestBlockEntity extends BlockEntity implements IBlockEntityDebugShapeProvider {
    
    private AABB renderBox = null;
    private IDebugShape sphere = null;
    
    public TestBlockEntity( BlockPos pos, BlockState state ) {
        super( TestRegistryObjects.TEST_BE.get(), pos, state );
    }

    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void onLoad() {
        renderBox = new AABB( getBlockPos() ).inflate( 3.0D, 2.0D, 3.0D );
        sphere = new SphereMeshShape( 2, ChatFormatting.GREEN.getColor() );
    }

    @SuppressWarnings( "ConstantConditions" )
    @Nullable
    @Override
    public List<IDebugShape> getDebugShapes() {
        List<IDebugShape> shapes = IBlockEntityDebugShapeProvider.fromBBs( renderBox );
        shapes.add( sphere );
        return shapes;
    }
}