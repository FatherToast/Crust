package fathertoast.crust.api.lib;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.ChunkStatus;

public final class EnvironmentHelper {
    
    // ---- CHUNK METHODS ---- //
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( IWorldReader world, BlockPos pos ) { return isLoaded( world, pos.getX(), pos.getZ() ); }
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( IWorldReader world, int x, int z ) { return isChunkPosLoaded( world, x >> 4, z >> 4 ); }
    
    /** @return True if the given chunk position is a loaded chunk. */
    public static boolean isChunkPosLoaded( IWorldReader world, int chunkX, int chunkZ ) {
        return world.getChunk( chunkX, chunkZ, ChunkStatus.FULL, false ) != null;
    }
}