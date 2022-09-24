package fathertoast.crust.api.impl;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.IRegistryHelper;
import fathertoast.crust.api.lib.SetBlockFlags;
import fathertoast.crust.api.portal.IPortalBuilder;
import fathertoast.crust.api.portal.IPortalGenerator;
import fathertoast.crust.common.core.Crust;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

@CrustPlugin
public class InternalCrustPlugin implements ICrustPlugin {

    private static final ResourceLocation ID = Crust.resLoc("builtin_plugin");

    public static IPortalBuilder NETHER_PORTAL;
    public static IPortalBuilder END_PORTAL;


    @Override
    public void onLoad(ICrustApi apiInstance) {
        IRegistryHelper registryHelper = apiInstance.getRegistryHelper();
        registerPortalBuilders(registryHelper);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    private static void registerPortalBuilders(IRegistryHelper registryHelper) {
        NETHER_PORTAL = registryHelper.registerPortalBuilder(
                new ResourceLocation("nether"),
                Crust.resLoc("textures/icon/portal_nether.png"),
                Arrays.asList(new ResourceLocation("overworld"), new ResourceLocation("the_nether")),
                DefaultPortalGenerators.NETHER_PORTAL_GEN
        );
        END_PORTAL = registryHelper.registerPortalBuilder(
                new ResourceLocation("end"),
                Crust.resLoc("textures/icon/portal_end.png"),
                Arrays.asList(new ResourceLocation("overworld"), new ResourceLocation("the_end")),
                DefaultPortalGenerators.END_PORTAL_GEN
        );
    }

    public static class DefaultPortalGenerators {

        public static IPortalGenerator NETHER_PORTAL_GEN = (World level, BlockPos.Mutable currentPos, Direction forward) -> {
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
        };

        public static IPortalGenerator END_PORTAL_GEN = (World level, BlockPos.Mutable currentPos, Direction forward) -> {
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
        };

        /** @return The proper facing for an end frame block. */
        private static Direction endFrameFacing( Direction forward, int tv, int fw ) {
            if( tv == 0 ) return forward.getCounterClockWise();
            if( tv == 4 ) return forward.getClockWise();
            if( fw == 0 ) return forward.getOpposite();
            return forward;
        }
    }
}
