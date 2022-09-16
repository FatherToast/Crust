package fathertoast.crust.api.lib;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Bit flags that can be used for block-setting methods. These can be combined with bitwise-OR (|).
 *
 * @see World#setBlock(BlockPos, BlockState, int)
 * @see World#setBlock(BlockPos, BlockState, int, int)
 */
@SuppressWarnings( "unused" )
public class SetBlockFlags {
    
    /** Triggers a block update. */
    public static final int BLOCK_UPDATE = 0b0000_0001; // 1
    /** On servers, sends the change to clients. On clients, triggers a render update. */
    public static final int UPDATE_CLIENT = 0b0000_0010; // 2
    /** Prevents clients from performing a render update. */
    public static final int SKIP_RENDER_UPDATE = 0b0000_0100; // 4
    /** Forces clients to immediately perform the render update on the main thread. Generally used for direct player actions. */
    public static final int PRIORITY_RENDER_UPDATE = 0b0000_1000; // 8
    /** Prevents neighboring blocks from being notified of the change. */
    public static final int SKIP_NEIGHBOR_UPDATE = 0b0001_0000; // 16
    /** Prevents neighbor blocks that are removed by the change from dropping as items. Used by multi-part blocks to prevent dupes. */
    public static final int SKIP_NEIGHBOR_DROPS = 0b0010_0000; // 32
    /** Marks the change as the result of a block moving. Generally prevents connection states from being updated. Used by pistons. */
    public static final int IS_MOVED = 0b0100_0000; // 64
    /** Prevents light levels from being recalculated when set. */
    public static final int SKIP_LIGHT_UPDATE = 0b1000_0000; // 128
    
    /** The set block flags used for most non-world-gen purposes. */
    public static final int DEFAULTS = BLOCK_UPDATE | UPDATE_CLIENT; // 3
    /** The set block flags used for most world-gen purposes. */
    public static final int DEFAULT_GEN = UPDATE_CLIENT; // 2
}