package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.portal.CrustPortals;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrustPortalCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        dispatcher.register( CommandUtil.literal( ICrustApi.MOD_ID + "portal" )
                .requires( CommandUtil::canCheat )
                .then( Commands.argument( "portalType", PortalTypeArgument.portalType() )
                        .executes( ( context ) -> run( context.getSource(),
                                PortalTypeArgument.getPortalType( context, "portalType" ),
                                context.getSource().getPlayerOrException() ) ) ) );
    }
    
    
    /** Command implementation. */
    private static int run( CommandSourceStack source, PortalBuilder portalBuilder, Entity target ) {
        if( !portalBuilder.isValidDimension( target.level() ) ) {
            CommandUtil.sendFailure( source, "portal.dimension" );
            return -1;
        }
        
        boolean failed = false;
        
        Direction forward = target.getDirection();
        BlockPos.MutableBlockPos currentPos = target.blockPosition().mutable().move( forward, 3 );
        
        if( currentPos.getY() <= 0 || currentPos.getY() >= target.level().getMaxBuildHeight() - 5 ) failed = true;
        else if( isOpenSpace( target.level(), currentPos ) ) findGroundBelow( target.level(), currentPos );
        else if( findGroundAbove( target.level(), currentPos ) ) failed = true;
        BlockPos pos = currentPos.immutable();
        
        if( failed ) {
            CommandUtil.sendFailure( source, "portal" );
            return 0;
        }
        
        portalBuilder.generate( target.level(), currentPos, forward );
        CommandUtil.sendSuccess( source, "portal", CrustPortals.PORTAL_REGISTRY.get().getKey( portalBuilder ),
                pos.getX(), pos.getY(), pos.getZ() );
        return 1;
    }
    
    /** Attempts to find the ground. Resets the position if none can be found. */
    private static void findGroundBelow( Level level, BlockPos.MutableBlockPos currentPos ) {
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
    private static boolean findGroundAbove( Level level, BlockPos.MutableBlockPos currentPos ) {
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
    private static boolean isOpenSpace( Level level, BlockPos pos ) {
        final BlockState stateAtPos = level.getBlockState( pos );
        return (stateAtPos.canBeReplaced() || stateAtPos.is( BlockTags.LEAVES )) &&
                !stateAtPos.getFluidState().is( FluidTags.WATER );
    }
}