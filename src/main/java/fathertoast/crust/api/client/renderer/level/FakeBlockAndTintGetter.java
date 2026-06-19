package fathertoast.crust.api.client.renderer.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A fake implementation of {@link net.minecraft.world.level.BlockAndTintGetter}
 * with setter methods for manipulating returned values as needed.
 */
@ApiStatus.Experimental
public class FakeBlockAndTintGetter implements BlockAndTintGetter {
    
    /** A dummy light engine instance. */
    private final LevelLightEngine dummyLightEngine = new LevelLightEngine( new LightChunkGetter() {
        final LightChunk emptyChunk = new FakeLightChunk( FakeBlockAndTintGetter.this );
        
        @Override
        @Nullable
        public LightChunk getChunkForLighting( int x, int z ) { return emptyChunk; }
        
        @Override
        public BlockGetter getLevel() { return FakeBlockAndTintGetter.this; }
    }, false, false );
    
    /** Used for fluid rendering, when checking neighboring fluid states. */
    @Nullable
    private BlockPos currentOriginPos;
    @Nullable
    private Integer currentShade;
    
    private int currentHeight;
    private int currentMinBuildHeight;
    private BlockEntity currentBlockEntity;
    private BlockState currentBlockState;
    private FluidState currentFluidState;
    private int currentTint;
    private int brightness;
    private int rawBrightness;
    private boolean canSeeSky;
    
    //
    // ------------------------- Setters -------------------------
    //
    
    public void setOriginPos( @Nullable BlockPos originPos ) {
        currentOriginPos = originPos;
    }
    
    public void setCurrentHeight( int height ) {
        currentHeight = height;
    }
    
    public void setCurrentMinBuildHeight( int minBuildingHeight ) {
        currentMinBuildHeight = minBuildingHeight;
    }
    
    public void setCurrentBlockEntity( BlockEntity blockEntity ) {
        currentBlockEntity = blockEntity;
    }
    
    public void setCurrentBlockState( BlockState blockState ) {
        currentBlockState = blockState;
    }
    
    public void setCurrentFluidState( FluidState fluidState ) {
        currentFluidState = fluidState;
    }
    
    public void setCurrentShade( @Nullable Integer shade ) {
        currentShade = shade;
    }
    
    public void setCurrentTint( int tint ) {
        currentTint = tint;
    }
    
    public void setBrightness( int value ) {
        brightness = value;
    }
    
    public void setRawBrightness( int value ) {
        rawBrightness = value;
    }
    
    public void setCanSeeSky( boolean seeSky ) {
        canSeeSky = seeSky;
    }
    
    //
    // --------------- BlockAndTintGetter Implementation ---------------
    //
    
    @Override
    public int getHeight() { return currentHeight; }
    
    @Override
    public int getMinBuildHeight() { return currentMinBuildHeight; }
    
    @Override
    @Nullable
    public BlockEntity getBlockEntity( BlockPos pos ) { return currentBlockEntity; }
    
    @Override
    public BlockState getBlockState( BlockPos pos ) {
        if( currentOriginPos != null ) {
            return currentOriginPos.equals( pos ) ? currentBlockState : Blocks.AIR.defaultBlockState();
        }
        return currentBlockState;
    }
    
    @Override
    public FluidState getFluidState( BlockPos pos ) {
        if( currentOriginPos != null ) {
            return currentOriginPos.equals( pos ) ? currentFluidState : Fluids.EMPTY.defaultFluidState();
        }
        return currentFluidState;
    }
    
    /** Essentially copy-pasted from {@link net.minecraft.client.multiplayer.ClientLevel}. */
    @Override
    public float getShade( Direction dir, boolean shade ) {
        if( currentShade != null ) {
            return currentShade;
        }
        if( !shade ) {
            return 1.0F;
        }
        else {
            return switch( dir ) {
                case DOWN -> 0.5F;
                case NORTH, SOUTH -> 0.8F;
                case WEST, EAST -> 0.6F;
                default -> 1.0F;
            };
        }
    }
    
    @Override
    public int getBlockTint( BlockPos pos, ColorResolver colorResolver ) { return currentTint; }
    
    @Override
    public int getBrightness( LightLayer p_45518_, BlockPos p_45519_ ) {
        return brightness;
    }
    
    @Override
    public int getRawBrightness( BlockPos p_45525_, int p_45526_ ) {
        return rawBrightness;
    }
    
    @Override
    public boolean canSeeSky( BlockPos pos ) {
        return canSeeSky;
    }
    
    @Override
    public LevelLightEngine getLightEngine() { return dummyLightEngine; }
}
