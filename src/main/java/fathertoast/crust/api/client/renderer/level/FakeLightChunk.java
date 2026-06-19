package fathertoast.crust.api.client.renderer.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/** A fake implementation of {@link LightChunk}. */
@ApiStatus.Experimental
public class FakeLightChunk implements LightChunk {
    
    @Nullable
    private final BlockAndTintGetter blockAndTintGetter;
    private final ChunkSkyLightSources chunkSkyLightSources = new ChunkSkyLightSources( this );
    
    
    public FakeLightChunk( @Nullable BlockAndTintGetter blockAndTintGetter ) {
        this.blockAndTintGetter = blockAndTintGetter;
    }
    
    @Override
    public void findBlockLightSources( BiConsumer<BlockPos, BlockState> biConsumer ) { }
    
    @Override
    public ChunkSkyLightSources getSkyLightSources() {
        return chunkSkyLightSources;
    }
    
    @Override
    @Nullable
    public BlockEntity getBlockEntity( BlockPos pos ) {
        return blockAndTintGetter == null ? null : blockAndTintGetter.getBlockEntity( pos );
    }
    
    @Override
    public BlockState getBlockState( BlockPos pos ) {
        return blockAndTintGetter == null ? Blocks.AIR.defaultBlockState() : blockAndTintGetter.getBlockState( pos );
    }
    
    @Override
    public FluidState getFluidState( BlockPos pos ) {
        return blockAndTintGetter == null ? Fluids.WATER.defaultFluidState() : blockAndTintGetter.getFluidState( pos );
    }
    
    @Override
    public int getHeight() {
        return blockAndTintGetter == null ? 0 : blockAndTintGetter.getHeight();
    }
    
    @Override
    public int getMinBuildHeight() {
        return blockAndTintGetter == null ? 0 : blockAndTintGetter.getMinBuildHeight();
    }
}
