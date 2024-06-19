package fathertoast.crust.api.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Level events. Sent from the server using {@link Level#levelEvent(int, BlockPos, int)}, then executed on the
 * client via {@link net.minecraft.client.renderer.LevelRenderer#levelEvent(int, BlockPos, int)}.
 */
@SuppressWarnings( "unused" )
public enum LevelEventHelper {
    
    // ---- Simple Events ---- //
    
    // Sound Only
    DISPENSER_DISPENSE( 1000 ), DISPENSER_FAIL( 1001 ), DISPENSER_LAUNCH( 1002 ),
    ENDER_EYE_SOUND( 1003 ),
    FIREWORK_ROCKET_SHOOT( 1004 ),
    IRON_DOOR_OPEN( 1005 ), WOODEN_DOOR_OPEN( 1006 ), WOODEN_TRAPDOOR_OPEN( 1007 ), FENCE_GATE_OPEN( 1008 ),
    IRON_DOOR_CLOSE( 1011 ), WOODEN_DOOR_CLOSE( 1012 ), WOODEN_TRAPDOOR_CLOSE( 1013 ), FENCE_GATE_CLOSE( 1014 ),
    IRON_TRAPDOOR_CLOSE( 1036 ), IRON_TRAPDOOR_OPEN( 1037 ),
    FIRE_EXTINGUISH( 1009 ),
    GHAST_WARN( 1015 ), GHAST_SHOOT( 1016 ),
    ENDER_DRAGON_SHOOT( 1017 ), ENDER_DRAGON_GROWL( 3001 ),
    BLAZE_SHOOT( 1018 ),
    PHANTOM_BITE( 1039 ),
    ZOMBIE_ATTACK_WOODEN_DOOR( 1019 ), ZOMBIE_ATTACK_IRON_DOOR( 1020 ), ZOMBIE_BREAK_WOODEN_DOOR( 1021 ),
    ZOMBIE_INFECT( 1026 ), ZOMBIE_VILLAGER_CONVERTED( 1027 ),
    ZOMBIE_CONVERTED_TO_DROWNED( 1040 ), HUSK_CONVERTED_TO_ZOMBIE( 1041 ),
    WITHER_BREAK_BLOCK( 1022 ), WITHER_SHOOT( 1024 ),
    BAT_TAKEOFF( 1025 ),
    ANVIL_DESTROY( 1029 ), ANVIL_USE( 1030 ), ANVIL_LAND( 1031 ),
    BREWING_STAND_BREW( 1035 ), GRINDSTONE_USE( 1042 ), BOOK_PAGE_TURN( 1043 ), SMITHING_TABLE_USE( 1044 ),
    PORTAL_TRAVEL( 1032 ),
    CHORUS_FLOWER_GROW( 1033 ), CHORUS_FLOWER_DEATH( 1034 ),
    
    // Particles Only
    ENDER_EYE_PARTICLE( 2003 ),
    SMOKE_AND_FLAME( 2004 ),
    EXPLOSION_PARTICLE( 2008 ), CLOUD_PARTICLES( 2009 ),
    
    // Sound & Particles
    LAVA_EXTINGUISH( 1501 ), REDSTONE_TORCH_BURNOUT( 1502 ),
    END_PORTAL_FRAME_FILL( 1503 ), END_PORTAL_FRAME_COMPLETED( 3000 );
    
    
    // ---- Metadata Events ---- //
    
    // Sound Only
    /** Plays a music disc or cancels any currently playing music disc (by 'playing' a null disc). */
    public static final MusicDiscEvent MUSIC_DISC = new MusicDiscEvent( 1010 );
    
    // Particles Only
    /** Spawns smoke particles directionally; used by dispensers. */
    public static final DispenserSmokeEvent DISPENSER_SMOKE = new DispenserSmokeEvent( 2000 );
    /** Spawns a specific number of happy villager particles via {@link net.minecraft.world.item.BoneMealItem#addGrowthParticles(LevelAccessor, BlockPos, int)}. */
    public static final BoneMealEvent BONE_MEAL_USE = new BoneMealEvent( 2005 );
    
    // Sound & Particles
    /** Spawns block particles and plays the break sound as applicable. */
    public static final BlockBreakEvent BLOCK_BREAK_FX = new BlockBreakEvent( 2001 );
    /** Spawns splash potion break and colored effect particles, then plays the splash potion break sound. */
    public static final ColorEvent SPLASH_POTION_FX = new ColorEvent( 2002 );
    /** Spawns splash potion break and colored instant-effect particles, then plays the splash potion break sound. */
    public static final ColorEvent INSTANT_SPLASH_POTION_FX = new ColorEvent( 2007 );
    /** Spawns compost fill particles and plays either the success (level increased) or failure (no change) sound. */
    public static final ComposterFillEvent COMPOSTER_FILL = new ComposterFillEvent( 1500 );
    /** Spawns dragon breath particles, optionally playing the dragon fireball explosion sound. */
    public static final OptionalSoundEvent ENDER_DRAGON_FIREBALL_HIT = new OptionalSoundEvent( 2006 );
    
    
    // ---- Simple Event Implementation ---- //
    
    public final int ID;
    
    LevelEventHelper( int id ) { ID = id; }
    
    /** Plays this event at the entity's position, if the entity is not silenced. */
    public void play( Entity entity ) { if( !entity.isSilent() ) play( entity.level(), entity.blockPosition() ); }
    
    /** Plays this event at a particular position. */
    public void play( Level level, BlockPos pos ) { play( level, null, pos ); }
    
    /** Plays this event at a particular position, optionally excluding a particular player. */
    public void play( Level level, @Nullable Player player, BlockPos pos ) {
        level.levelEvent( player, ID, pos, 0 );
    }
    
    
    // ---- Metadata Event Implementations ---- //
    
    public static abstract class MetadataEvent {
        
        public final int ID;
        
        private MetadataEvent( int id ) { ID = id; }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        protected void play( Level level, @Nullable Player player, BlockPos pos, int metadata ) {
            level.levelEvent( player, ID, pos, metadata );
        }
    }
    
    public static class MusicDiscEvent extends MetadataEvent {
        
        private MusicDiscEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void stop( Level level, @Nullable Player player, BlockPos pos ) { play( level, player, pos, null ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, @Nullable Item musicDisc ) {
            play( level, player, pos, musicDisc instanceof RecordItem ? Item.getId( musicDisc ) : 0 );
        }
    }
    
    public static class ComposterFillEvent extends MetadataEvent {
        
        private ComposterFillEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, boolean success ) {
            play( level, player, pos, success ? 1 : 0 );
        }
    }
    
    public static class DispenserSmokeEvent extends MetadataEvent {
        
        private DispenserSmokeEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, Direction direction ) {
            play( level, player, pos, direction.get3DDataValue() );
        }
    }
    
    public static class BlockBreakEvent extends MetadataEvent {
        
        private BlockBreakEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, BlockState blockState ) {
            play( level, player, pos, Block.getId( blockState ) );
        }
    }
    
    public static class ColorEvent extends MetadataEvent {
        
        private ColorEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void playRGBFloats( Level level, @Nullable Player player, BlockPos pos, float r, float g, float b ) {
            playRGB( level, player, pos, CrustMath.toRGB( r, g, b ) );
        }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void playRGB( Level level, @Nullable Player player, BlockPos pos, int r, int g, int b ) {
            playRGB( level, player, pos, CrustMath.bitsToRGB( r, g, b ) );
        }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void playRGB( Level level, @Nullable Player player, BlockPos pos, int rgb ) {
            play( level, player, pos, rgb );
        }
    }
    
    public static class BoneMealEvent extends MetadataEvent {
        
        private BoneMealEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. Uses the default number of particles (15). */
        public void play( Level level, @Nullable Player player, BlockPos pos ) { play( level, player, pos, 0 ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, int particleCount ) {
            super.play( level, player, pos, particleCount );
        }
    }
    
    public static class OptionalSoundEvent extends MetadataEvent {
        
        private OptionalSoundEvent( int id ) { super( id ); }
        
        /** Plays this event at a particular position, optionally excluding a particular player. */
        public void play( Level level, @Nullable Player player, BlockPos pos, boolean playSound ) {
            play( level, player, pos, playSound ? 1 : -1 );
        }
    }
}