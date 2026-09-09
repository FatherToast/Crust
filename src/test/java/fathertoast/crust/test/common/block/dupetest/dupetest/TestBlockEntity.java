package fathertoast.crust.test.common.block.dupetest.dupetest;

import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.test.common.TestCrustObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Used to test {@link fathertoast.crust.client.config.RenderSettingsCrustConfig render settings config} when
 * auto-generating debug shape provider toggle options.
 * Since we already have the class {@link fathertoast.crust.test.common.block.TestBlockEntity} and the duplicate outside this subpackage,
 * the field generated for this class should end up being named {@code "crust.test_block_2"}.
 */
@SuppressWarnings( "unused" )
public class TestBlockEntity extends BlockEntity implements IDebugShapeProvider {
    
    public TestBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( TestCrustObjects.Obj.TEST_BE.get(), pos, state );
    }
    
    /**
     * @return A List of debug shapes that should be rendered in the world.
     * The list may be null, but do NOT include any null entries in the list.
     */
    @Override
    @Nullable
    public List<IDebugShape> getDebugShapes() {
        return IDebugShapeProvider.NO_SHAPES;
    }
}
