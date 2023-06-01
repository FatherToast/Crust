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

public class NetherPortalBuilder extends PortalBuilder {

    private final List<ResourceLocation> validDimensions;

    public NetherPortalBuilder() {
        super(Crust.resLoc("textures/icon/portal_nether.png"));
        validDimensions = ImmutableList.of(new ResourceLocation("overworld"), new ResourceLocation("the_nether"));
    }

    @Override
    public Iterable<ResourceLocation> getValidDimensions() {
        return validDimensions;
    }

    @Override
    public void generate(World world, BlockPos.Mutable currentPos, Direction forward) {
        Direction transverse = forward.getClockWise();

        currentPos.move( transverse, -1 );
        BlockPos portalCorner = currentPos.immutable();

        currentPos.move( transverse, -1 );
        currentPos.move( Direction.UP, -1 );

        BlockState frameBlock = Blocks.OBSIDIAN.defaultBlockState();
        BlockPos frameCorner = currentPos.immutable();
        for( int tv = 0; tv < 5; tv++ ) {
            for( int up = 0; up < 5; up++ ) {
                if( tv == 0 || tv == 4 || up == 0 || up == 4 ) {
                    currentPos.set( frameCorner ).move( transverse, tv )
                            .move( Direction.UP, up );

                    world.setBlock( currentPos, frameBlock, SetBlockFlags.DEFAULTS );
                }
            }
        }

        BlockState portalBlock = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue( BlockStateProperties.HORIZONTAL_AXIS, transverse.getAxis() );
        for( int tv = 0; tv < 3; tv++ ) {
            for( int up = 0; up < 3; up++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( Direction.UP, up );

                world.setBlock( currentPos, portalBlock,
                        SetBlockFlags.UPDATE_CLIENT | SetBlockFlags.SKIP_NEIGHBOR_UPDATE );
            }
        }
    }
}
