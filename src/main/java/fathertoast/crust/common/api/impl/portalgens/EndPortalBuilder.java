package fathertoast.crust.common.api.impl.portalgens;

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

public class EndPortalBuilder extends PortalBuilder {
    
    private static final Set<ResourceLocation> VALID_DIMENSIONS = ImmutableSet.of( World.OVERWORLD.location(), World.END.location() );
    
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
                    world.setBlock( currentPos, frameBlock, Constants.BlockFlags.DEFAULT );
                }
            }
        }
        
        BlockState portalBlock = Blocks.END_PORTAL.defaultBlockState();
        for( int tv = 0; tv < 3; tv++ ) {
            for( int fw = 0; fw < 3; fw++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( forward, fw );
                
                world.setBlock( currentPos, portalBlock, Constants.BlockFlags.DEFAULT );
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