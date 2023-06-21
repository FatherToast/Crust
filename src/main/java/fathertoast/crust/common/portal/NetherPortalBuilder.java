package fathertoast.crust.common.portal;

import com.google.common.collect.ImmutableSet;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Set;

public class NetherPortalBuilder extends PortalBuilder {
    
    private static final Set<ResourceLocation> VALID_DIMENSIONS = ImmutableSet.of( World.OVERWORLD.location(), World.NETHER.location() );
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public boolean isValidDimension( ResourceLocation dimension ) { return VALID_DIMENSIONS.contains( dimension ); }
    
    /**
     * Generates the portal in the world with a particular position and direction.
     *
     * @param world      The world to generate in.
     * @param currentPos The front-center position of the portal. This is often a block position directly
     *                   above a solid 'floor block'. This is mutable so that you can #move() it rather than
     *                   create numerous BlockPos objects.
     * @param forward    Horizontal facing of the portal; defined as the direction that a player is facing to
     *                   see the portal. By convention, the transverse direction is forward.getClockWise().
     */
    @Override
    public void generate( World world, BlockPos.Mutable currentPos, Direction forward ) {
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
                    
                    world.setBlock( currentPos, frameBlock, Constants.BlockFlags.DEFAULT );
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
                        Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.UPDATE_NEIGHBORS );
            }
        }
    }
}