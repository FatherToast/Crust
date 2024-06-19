package fathertoast.crust.common.portal;

import com.google.common.collect.ImmutableSet;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;


import java.util.Set;

public class NetherPortalBuilder extends PortalBuilder {
    
    private static final Set<ResourceLocation> VALID_DIMENSIONS = ImmutableSet.of( Level.OVERWORLD.location(), Level.NETHER.location() );
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public boolean isValidDimension( ResourceLocation dimension ) { return VALID_DIMENSIONS.contains( dimension ); }
    
    /**
     * Generates the portal in the world with a particular position and direction.
     *
     * @param level      The world to generate in.
     * @param currentPos The front-center position of the portal. This is often a block position directly
     *                   above a solid 'floor block'. This is mutable so that you can #move() it rather than
     *                   create numerous BlockPos objects.
     * @param forward    Horizontal facing of the portal; defined as the direction that a player is facing to
     *                   see the portal. By convention, the transverse direction is forward.getClockWise().
     */
    @Override
    public void generate( Level level, BlockPos.MutableBlockPos currentPos, Direction forward ) {
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

                    level.setBlock( currentPos, frameBlock, Block.UPDATE_ALL );
                }
            }
        }
        
        BlockState portalBlock = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue( BlockStateProperties.HORIZONTAL_AXIS, transverse.getAxis() );
        for( int tv = 0; tv < 3; tv++ ) {
            for( int up = 0; up < 3; up++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( Direction.UP, up );

                level.setBlock( currentPos, portalBlock,
                        Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS );
            }
        }
    }
}