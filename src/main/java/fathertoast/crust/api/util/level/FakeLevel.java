package fathertoast.crust.api.util.level;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalLong;

/**
 * A barebones fake level implementation.
 * Not safe for all use-cases, beware.
 */
// WIP - DO NOT USE THIS!!
// TODO - Work around needing registry access for the damage sources holder.
//        Probably gonna need a mixin for that.
@ApiStatus.Experimental
public class FakeLevel extends Level {
    
    /** A resource key pointing to a fake level under Crust's namespace. */
    public static final ResourceKey<Level> FAKE_LEVEL_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "fake_level" )
    );
    /** A resource key pointing to a fake dimension type under Crust's namespace. */
    public static final ResourceKey<DimensionType> FAKE_DIM_TYPE_KEY = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "fake_dim_type" )
    );
    
    /** A fake dimension type. */
    public static final DimensionType FAKE_DIM_TYPE = new DimensionType(
            OptionalLong.empty(), true, false, false, true,
            1, true, false, 0, 256, 256,
            BlockTags.INFINIBURN_OVERWORLD, ResourceLocationUtils.EMPTY, 1.0F,
            new DimensionType.MonsterSettings( false, true, ConstantInt.of( 0 ), 0 )
    );
    
    /**
     * Creates a hacky dimension type reference holder
     * containing the {@link FakeLevel#FAKE_DIM_TYPE_KEY} as its key
     * and {@link FakeLevel#FAKE_DIM_TYPE} as its value.
     */
    private static Holder<DimensionType> createFakeDimTypeHolder() {
        // noinspection ConstantConditions
        Holder.Reference<DimensionType> holder = Holder.Reference.createStandAlone( null, FAKE_DIM_TYPE_KEY );
        holder.value = FAKE_DIM_TYPE;
        return holder;
    }
    
    
    public FakeLevel() {
        this( false );
    }
    
    public FakeLevel( boolean isClientSide ) {
        this( isClientSide, false, 0, 0 );
    }
    
    public FakeLevel( boolean isClientSide, boolean isDebug, long biomeSeed, int maxChainedNeighborUpdates ) {
        // noinspection ConstantConditions
        super( new FakeLevelData(), FAKE_LEVEL_KEY, null, // DamageSources field is reassigned via mixin before it can error
                createFakeDimTypeHolder(), () -> InactiveProfiler.INSTANCE,
                isClientSide, isDebug, biomeSeed, maxChainedNeighborUpdates );
    }
    
    @Override
    public void sendBlockUpdated( BlockPos pos, BlockState state, BlockState newState, int updateNeighbors ) { }
    
    @Override
    public void playSeededSound( @Nullable Player player, double x, double y, double z,
                                 Holder<SoundEvent> soundEvent, SoundSource soundSource,
                                 float volume, float pitch, long seed ) { }
    
    @Override
    public void playSeededSound( @Nullable Player player, Entity entity, Holder<SoundEvent> soundEvent, SoundSource soundSource,
                                 float volume, float pitch, long seed ) { }
    
    @Override
    public String gatherChunkSourceStats() {
        return "No stats for FakeLevel instance!";
    }
    
    @Override
    @Nullable
    public Entity getEntity( int id ) { return null; }
    
    @Override
    @Nullable
    public MapItemSavedData getMapData( String s ) { return null; }
    
    @Override
    public void setMapData( String s, MapItemSavedData savedData ) { }
    
    @Override
    public int getFreeMapId() { return 0; }
    
    @Override
    public void destroyBlockProgress( int entityId, BlockPos pos, int progress ) { }
    
    @Override
    public void levelEvent( @Nullable Player player, int eventId, BlockPos pos, int data ) { }
    
    @Override
    public void gameEvent( GameEvent gameEvent, Vec3 vec3, GameEvent.Context context ) { }
    
    @Override
    public float getShade( Direction dir, boolean b ) { return 0; }
    
    @Override
    public List<? extends Player> players() { return List.of(); }
    
    @Override
    public FeatureFlagSet enabledFeatures() {
        return FeatureFlags.DEFAULT_FLAGS;
    }
    
    
    //
    //-------------------- UNSUPPORTED --------------------
    //
    
    @Override
    public Scoreboard getScoreboard() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RecipeManager getRecipeManager() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ChunkSource getChunkSource() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Holder<Biome> getUncachedNoiseBiome( int x, int y, int z ) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RegistryAccess registryAccess() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public DamageSources damageSources() {
        throw new UnsupportedOperationException();
    }
}
