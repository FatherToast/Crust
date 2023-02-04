package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.api.lib.SetBlockFlags;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.core.Crust;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CrustPortalCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        dispatcher.register(CommandUtil.literal( Crust.MOD_ID + "portal" )
                .requires( CommandUtil::canCheat )
                .then(Commands.argument("portalType", PortalTypeArgument.portalType())
                        .executes((context) -> run( context.getSource(), PortalTypeArgument.getPortalType(context, "portalType"), context.getSource().getPlayerOrException()))));
    }

    
    /** Command implementation. */
    private static int run(CommandSource source, PortalBuilder portalBuilder, Entity target ) {
        if( !isDimensionValid( portalBuilder, target.level ) ) {
            CommandUtil.sendFailure( source, "portal.dimension" );
            return -1;
        }
        
        boolean failed = false;
        
        Direction forward = target.getDirection();
        BlockPos.Mutable currentPos = target.blockPosition().mutable().move( forward, 3 );
        
        if( currentPos.getY() <= 0 || currentPos.getY() >= target.level.getMaxBuildHeight() - 5 ) failed = true;
        else if( isOpenSpace( target.level, currentPos ) ) findGroundBelow( target.level, currentPos );
        else if( findGroundAbove( target.level, currentPos ) ) failed = true;
        BlockPos pos = currentPos.immutable();
        
        if( failed ) { // || !canPlace( mode, target.level, currentPos, facing ) ) {
            CommandUtil.sendFailure( source, "portal" );
            return 0;
        }
        
        place( portalBuilder, target.level, currentPos, forward );
        CommandUtil.sendSuccess( source, "portal." + portalBuilder.getRegistryName().getPath(),
                pos.getX(), pos.getY(), pos.getZ() );
        return 1;
    }
    
    public static boolean isDimensionValid(PortalBuilder portalBuilder, World world ) {
        ResourceLocation currentWorldId = world.dimension().location();
        List<ResourceLocation> validDimensions = portalBuilder.getValidDimensions();

        if (validDimensions.isEmpty())
            return false;

        for (ResourceLocation rl : portalBuilder.getValidDimensions()) {
            if (rl.equals(currentWorldId))
                return true;
        }
        return false;
    }
    
    /** Attempts to find the ground. Resets the position if none can be found. */
    private static void findGroundBelow( World level, BlockPos.Mutable currentPos ) {
        final int yI = currentPos.getY();
        final int minY = Math.max( yI - 8, 0 );
        
        while( currentPos.getY() > minY ) {
            currentPos.move( 0, -1, 0 );
            if( !isOpenSpace( level, currentPos ) ) {
                // Move back up one to ensure the current pos is replaceable
                currentPos.move( 0, 1, 0 );
                return;
            }
        }
        // Initial y was replaceable, so we can default to this
        currentPos.setY( yI );
    }
    
    /** @return Attempts to find the ground. Returns true if no position could be found. */
    private static boolean findGroundAbove( World level, BlockPos.Mutable currentPos ) {
        final int yI = currentPos.getY();
        final int maxY = Math.min( yI + 8, level.getMaxBuildHeight() - 5 );
        
        while( currentPos.getY() < maxY ) {
            currentPos.move( 0, 1, 0 );
            // Found a replaceable pos
            if( isOpenSpace( level, currentPos ) ) return false;
        }
        // Initial y was not replaceable, so we must cancel the entire operation
        return true;
    }
    
    /** @return True if the position should be considered "open space"; i.e. not a part of the ground. */
    private static boolean isOpenSpace( World level, BlockPos pos ) {
        final BlockState stateAtPos = level.getBlockState( pos );
        return (stateAtPos.getMaterial().isReplaceable() || stateAtPos.is( BlockTags.LEAVES )) &&
                !stateAtPos.getFluidState().is( FluidTags.WATER );
    }
    
    /** Places the portal. */
    private static void place(PortalBuilder portalBuilder, World level, BlockPos.Mutable currentPos, Direction forward ) {
        portalBuilder.generate(level, currentPos, forward);
    }
    
    /** Places a Nether portal. */
    private static void placeNetherPortal( World level, BlockPos.Mutable currentPos, Direction forward ) {
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
                    
                    level.setBlock( currentPos, frameBlock, SetBlockFlags.DEFAULTS );
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
                        SetBlockFlags.UPDATE_CLIENT | SetBlockFlags.SKIP_NEIGHBOR_UPDATE );
            }
        }
    }
    
    /** Places an End portal. */
    private static void placeEndPortal( World level, BlockPos.Mutable currentPos, Direction forward ) {
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
                    level.setBlock( currentPos, frameBlock, SetBlockFlags.DEFAULTS );
                }
            }
        }
        
        BlockState portalBlock = Blocks.END_PORTAL.defaultBlockState();
        for( int tv = 0; tv < 3; tv++ ) {
            for( int fw = 0; fw < 3; fw++ ) {
                currentPos.set( portalCorner ).move( transverse, tv )
                        .move( forward, fw );
                
                level.setBlock( currentPos, portalBlock, SetBlockFlags.DEFAULTS );
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