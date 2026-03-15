package fathertoast.crust.api.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public final class EnvironmentHelper {
    
    // ---- CHUNK METHODS ---- //
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( LevelAccessor level, BlockPos pos ) { return isLoaded( level, pos.getX(), pos.getZ() ); }
    
    /** @return True if the given position is in a loaded chunk. */
    public static boolean isLoaded( LevelAccessor level, int x, int z ) { return isChunkPosLoaded( level, x >> 4, z >> 4 ); }
    
    /** @return True if the given chunk position is a loaded chunk. */
    public static boolean isChunkPosLoaded( LevelAccessor level, int chunkX, int chunkZ ) {
        return level.hasChunk( chunkX, chunkZ );
    }
    
    // Utility class
    private EnvironmentHelper() { }
}