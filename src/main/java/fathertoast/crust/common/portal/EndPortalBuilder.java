package fathertoast.crust.common.portal;

import com.google.common.collect.ImmutableSet;
import com.mojang.math.Constants;
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

public class EndPortalBuilder extends PortalBuilder {
    
    private static final Set<ResourceLocation> VALID_DIMENSIONS = ImmutableSet.of( Level.OVERWORLD.location(), Level.END.location() );
    
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
                    level.setBlock( currentPos, frameBlock, Block.UPDATE_ALL );
                }
            }
        }
        
        BlockState portalBlock = Blocks.END_PORTAL.defaultBlockState();
        for( int tv = 0; tv < 3; tv++ ) {
            for( int fw = 0; fw < 3; fw++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( forward, fw );

                level.setBlock( currentPos, portalBlock, Block.UPDATE_ALL );
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