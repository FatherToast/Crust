package fathertoast.crust.api.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public final class EnvironmentHelper {
    
    // ---- CHUNK METHODS ---- //
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( LevelAccessor world, BlockPos pos ) { return isLoaded( world, pos.getX(), pos.getZ() ); }
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( LevelAccessor world, int x, int z ) { return isChunkPosLoaded( world, x >> 4, z >> 4 ); }
    
    /** @return True if the given chunk position is a loaded chunk. */
    public static boolean isChunkPosLoaded( LevelAccessor world, int chunkX, int chunkZ ) {
        return world.hasChunk( chunkX, chunkZ );
    }
}