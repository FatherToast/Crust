package fathertoast.crust.api.config.common.value.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Provides context for use by environment condition checks.
 * <p>
 * Uses a builder-like structure; first you pick from the various constructors or static methods available
 * which define some baseline context, and then chain methods to build optional context as needed.
 *
 * @see EnvironmentList
 * @see EnvironmentEntry
 */
@SuppressWarnings( { "UnusedReturnValue", "unused" } )
public final class EnvironmentContext {
    
    // ---- Common/Starter Context Helpers ---- //
    
    /** A simple entity context. */
    public static EnvironmentContext withTarget( Entity target ) { return new EnvironmentContext( target ); }
    
    /** A simple block position context. */
    public static EnvironmentContext withTarget( Level level, BlockPos target ) { return new EnvironmentContext( level, target ); }
    
    /** Context for an entity using the item stack in a particular hand. */
    public static EnvironmentContext entityUsingItem( LivingEntity target, InteractionHand hand ) {
        return new EnvironmentContext( target ).withCause( target.getItemInHand( hand ) );
    }
    
    /** Context for an entity using an item stack. */
    public static EnvironmentContext entityUsingItem( Entity target, ItemStack item ) {
        return new EnvironmentContext( target ).withCause( item );
    }
    
    /** Context for an entity being interacted with by an optional entity using an item stack. */
    public static EnvironmentContext entityInteract( Entity target, @Nullable Entity interactingEntity, ItemStack itemUsed ) {
        return new EnvironmentContext( target ).withCause( interactingEntity ).withCause( itemUsed );
    }
    
    /** Context for a block position being interacted with by an optional entity using an item stack. */
    public static EnvironmentContext blockInteract( Level level, BlockPos target, @Nullable Entity interactingEntity, ItemStack itemUsed ) {
        return new EnvironmentContext( level, target ).withCause( interactingEntity ).withCause( itemUsed );
    }
    
    /** Context for fishing being done by an entity angler using the item stack in a particular hand as a fishing rod. */
    public static EnvironmentContext fishing( Vec3 bobberPos, LivingEntity angler, InteractionHand hand ) {
        return new EnvironmentContext( angler.level(), null, bobberPos,
                BlockPos.containing( bobberPos ) ).withCause( angler )
                .withCause( angler.getItemInHand( hand ) );
    }
    
    /** Context for fishing being done by an optional entity angler using an item stack as its fishing rod. */
    public static EnvironmentContext fishing( Level level, Vec3 bobberPos, @Nullable Entity angler, ItemStack rod ) {
        return new EnvironmentContext( level, null, bobberPos,
                BlockPos.containing( bobberPos ) ).withCause( angler ).withCause( rod );
    }
    
    
    // ---- Instance Implementation ---- //
    
    private final LevelAccessor levelAccess;
    private final Level fullLevel;
    @Nullable
    private final WorldGenLevel worldGenLevel;
    @Nullable
    private final ChunkGenerator chunkGenerator;
    
    @Nullable
    private final Entity targetEntity;
    @Nullable
    private final Vec3 targetPos;
    @Nullable
    private final BlockPos targetBlockPos;
    
    @Nullable
    private Entity responsibleEntity;
    @Nullable
    private Vec3 responsiblePos;
    @Nullable
    private BlockPos responsibleBlockPos;
    private ItemStack responsibleTool = ItemStack.EMPTY;
    
    public EnvironmentContext( Level level ) { this( level, null, null, null ); }
    
    public EnvironmentContext( Level level, @Nullable BlockPos target ) {
        this( level, null, target == null ? null : Vec3.atLowerCornerOf( target ), target );
        if( target != null ) withCause( target );
    }
    
    public EnvironmentContext( Entity target ) {
        this( target.level(), target, target.position(), target.getOnPos() );
        withCause( target );
    }
    
    public EnvironmentContext( Level level, @Nullable Entity entityTarget, @Nullable Vec3 posTarget, @Nullable BlockPos blockPosTarget ) {
        levelAccess = level;
        fullLevel = level;
        worldGenLevel = null;
        chunkGenerator = level instanceof ServerLevel serverLevel ? serverLevel.getChunkSource().getGenerator() : null;
        
        targetEntity = entityTarget;
        targetPos = posTarget;
        targetBlockPos = filterPos( blockPosTarget );
    }
    
    @ApiStatus.Experimental
    public EnvironmentContext( WorldGenLevel level, ChunkGenerator chunkGen, @Nullable BlockPos target ) {
        levelAccess = level;
        fullLevel = level.getLevel();
        worldGenLevel = level;
        chunkGenerator = chunkGen;
        
        targetEntity = null;
        if( target == null ) {
            targetPos = null;
            targetBlockPos = null;
        }
        else {
            targetPos = Vec3.atLowerCornerOf( target );
            targetBlockPos = filterPos( target );
            withCause( target );
        }
    }
    
    @ApiStatus.Experimental
    public EnvironmentContext( FeaturePlaceContext<?> placeContext, @Nullable BlockPos target ) {
        this( placeContext.level(), placeContext.chunkGenerator(), target );
    }
    
    @Override
    public String toString() {
        return "EnvironmentContext{" +
                "levelAccess=" + levelAccess +
                ", fullLevel=" + fullLevel +
                ", worldGenLevel=" + worldGenLevel +
                ", chunkGenerator=" + chunkGenerator +
                ", targetEntity=" + targetEntity +
                ", targetPos=" + targetPos +
                ", targetBlockPos=" + targetBlockPos +
                ", responsibleEntity=" + responsibleEntity +
                ", responsiblePos=" + responsiblePos +
                ", responsibleBlockPos=" + responsibleBlockPos +
                ", responsibleTool=" + responsibleTool +
                '}';
    }
    
    
    // ---- Context-Building Methods ---- //
    
    /**
     * Identifies an entity as responsible for this context check.
     * Will overwrite positions from other causes (e.g., block position cause) if entity is non-null.
     */
    public EnvironmentContext withCause( @Nullable Entity entity ) {
        responsibleEntity = entity;
        if( entity != null ) {
            responsiblePos = entity.position();
            responsibleBlockPos = filterPos( entity.getOnPos() );
        }
        return this;
    }
    
    /**
     * Identifies a block position as responsible for this context check.
     * Will overwrite positions from other causes (e.g., entity cause) if pos is non-null.
     */
    public EnvironmentContext withCause( @Nullable BlockPos pos ) {
        if( pos != null ) {
            responsiblePos = Vec3.atLowerCornerOf( pos );
            responsibleBlockPos = filterPos( pos );
        }
        return this;
    }
    
    /** Identifies an item stack as responsible for this context check. Does not conflict with any other cause. */
    public EnvironmentContext withCause( @Nullable ItemStack item ) {
        responsibleTool = item == null ? ItemStack.EMPTY : item;
        return this;
    }
    
    
    // ---- Context Accessor Methods ---- //
    
    /** The level access. */
    public LevelAccessor getLevel() { return levelAccess; }
    
    /**
     * The level. You should always use {@link #getLevel()} rather than this when possible, as
     * certain level interactions using this may be unsafe during world gen.
     */
    public Level getFullLevel() { return fullLevel; }
    
    /** The world gen level. Null outside of world generation. */
    @Nullable
    public WorldGenLevel getWorldGenLevel() { return worldGenLevel; }
    
    /** The chunk generator. Null if unavailable (e.g., on client). */
    @Nullable
    public ChunkGenerator getChunkGenerator() { return chunkGenerator; }
    
    /** The entity of focus. Null if none. */
    @Nullable
    public Entity getEntity() { return targetEntity; }
    
    /**
     * The position of focus. This is the target entity's position or the lower corner of the target block position.
     * WARNING: May be outside the level access bounds.
     */
    @Nullable
    public Vec3 getPos() { return targetPos; }
    
    /** The block position of focus. Null if it would be outside the level access bounds (e.g., unloaded chunk). */
    @Nullable
    public BlockPos getBlockPos() { return targetBlockPos; }
    
    /** The entity responsible for the context check. Null if not entity-driven. */
    @Nullable
    public Entity getResponsibleEntity() { return responsibleEntity; }
    
    /**
     * The position responsible for the context check. This is the target entity's position or the center of the
     * target block position. WARNING: May be outside the level access bounds.
     */
    @Nullable
    public Vec3 getResponsiblePos() { return responsiblePos; }
    
    /**
     * The block position responsible for the context check. Null if it would be outside the level access bounds
     * (e.g., unloaded chunk).
     */
    @Nullable
    public BlockPos getResponsibleBlockPos() { return responsibleBlockPos; }
    
    /** The item stack responsible for the context check. Empty stack if not item-stack-driven. */
    public ItemStack getResponsibleTool() { return responsibleTool; }
    
    
    // ---- Internal Methods ---- //
    
    /** @return The passed block pos, or null if it appears to be outside the level access bounds. */
    @Nullable
    private BlockPos filterPos( @Nullable BlockPos pos ) {
        return pos == null || levelAccess.getChunk(
                SectionPos.blockToSectionCoord( pos.getX() ),
                SectionPos.blockToSectionCoord( pos.getZ() ),
                ChunkStatus.FULL, false ) == null ? null : pos;
    }
}