package fathertoast.crust.api.impl.portalgens;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.lib.SetBlockFlags;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EndPortalBuilder extends PortalBuilder {

    private final List<ResourceLocation> validDimensions;

    public EndPortalBuilder() {
        super(Crust.resLoc("textures/icon/portal_end.png"));
        validDimensions = ImmutableList.of(new ResourceLocation("overworld"), new ResourceLocation("the_end"));
    }

    @Override
    public List<ResourceLocation> getValidDimensions() {
        return validDimensions;
    }

    @Override
    public void generate(World world, BlockPos.Mutable currentPos, Direction forward) {
        Direction transverse = forward.getClockWise();

        currentPos.move( Direction.UP, -1 );
        currentPos.move( transverse, -1 );
        BlockPos portalCorner = currentPos.immutable();

        currentPos.move( transverse, -1 );
        currentPos.move( forward, -1 );

        BlockPos frameCorner = currentPos.immutable();
        for( int tv = 0; tv < 5; tv++ ) {
            for( int fw = 0; fw < 5; fw++ ) {
                if( (tv == 0 || tv == 4) ^ (fw == 0 || fw == 4) ) {
                    currentPos.set( frameCorner ).move( transverse, tv )
                            .move( forward, fw );

                    BlockState frameBlock = Blocks.END_PORTAL_FRAME.defaultBlockState()
                            .setValue( BlockStateProperties.EYE, true )
                            .setValue( BlockStateProperties.HORIZONTAL_FACING, endFrameFacing( forward, tv, fw ) );
                    world.setBlock( currentPos, frameBlock, SetBlockFlags.DEFAULTS );
                }
            }
        }

        BlockState portalBlock = Blocks.END_PORTAL.defaultBlockState();
        for( int tv = 0; tv < 3; tv++ ) {
            for( int fw = 0; fw < 3; fw++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( forward, fw );

                world.setBlock( currentPos, portalBlock, SetBlockFlags.DEFAULTS );
            }
        }
    }

    /** @return The proper facing for an end frame block. */
    private static Direction endFrameFacing( Direction forward, int tv, int fw ) {
        if( tv == 0 ) return forward.getCounterClockWise();
        if( tv == 4 ) return forward.getClockWise();
        if( fw == 0 ) return forward.getOpposite();
        return forward;
    }
}
