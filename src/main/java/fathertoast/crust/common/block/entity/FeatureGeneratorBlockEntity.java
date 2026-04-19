package fathertoast.crust.common.block.entity;

import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.ResourceLocationUtils;
import fathertoast.crust.api.worldgen.DummyFeature;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.core.Crust;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FeatureGeneratorBlockEntity extends BlockEntity {
    
    public static final String KEY_READY_FOR_GENERATION = "ReadyForGen";
    
    /** True if this feature generator is flagged as ready to generate. */
    private boolean readyForGen = false;
    /** The feature generator data to use for generation. */
    private FeatureData data = FeatureData.newEmpty();
    
    
    public FeatureGeneratorBlockEntity( BlockPos pos, BlockState state ) {
        super( CrustObjects.BlockEntities.FEATURE_GENERATOR.get(), pos, state );
    }
    
    /**
     * Called when this block entity is added to the world, right before
     * the first tick when the chunk is generated or loaded from disk.
     * <br><br>
     * It is possible for this to be called before the underlying chunk
     * is fully promoted and has started ticking.
     */
    @Override
    public void onLoad() {
        super.onLoad();
        
        if( level == null || level.isClientSide ) return;
        
        // Schedule placement in a later block tick
        if( !level.getBlockTicks().hasScheduledTick( worldPosition, getBlockState().getBlock() ) ) {
            level.scheduleTick( worldPosition, getBlockState().getBlock(), 1 );
        }
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        
        saveTag.putBoolean( KEY_READY_FOR_GENERATION, readyForGen );
        
        if( data != null ) data.saveTo( saveTag );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        
        if( NBTHelper.containsNumber( loadTag, KEY_READY_FOR_GENERATION ) ) {
            readyForGen = loadTag.getBoolean( KEY_READY_FOR_GENERATION );
        }
        data.loadFrom( loadTag );
    }
    
    /**
     * Called on chunk load on the server to send update data to the client.
     *
     * @return A CompoundTag containing the data to be synced.
     */
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if( data != null ) data.saveTo( tag );
        return tag;
    }
    
    /** Called on the client when receiving an update tag. */
    @Override
    public void handleUpdateTag( CompoundTag updateTag ) {
        // noinspection ConstantConditions
        if( updateTag != null ) {
            if( data == null ) data = FeatureData.newEmpty();
            data.loadFrom( updateTag );
        }
    }
    
    /** @return The data sync packet to be sent to the client. */
    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    /** Called on client when an update packet is received from the server. */
    @Override
    public void onDataPacket( Connection net, ClientboundBlockEntityDataPacket packet ) {
        CompoundTag updateTag = packet.getTag();
        // noinspection ConstantConditions
        handleUpdateTag( updateTag );
    }
    
    /** @return This feature generator's generation settings. */
    public FeatureData getData() {
        return data;
    }
    
    /** Sets the generation data for this feature generator. */
    public void setData( FeatureData data ) {
        this.data = Objects.requireNonNull( data );
    }
    
    /** @return True if this feature generator is ready to generate next time it is loaded from chunk. */
    public boolean isReadyForGen() {
        return readyForGen;
    }
    
    /**
     * Attempts to generate a feature in the world using the given feature data.
     *
     * @param level The level to generate the feature in.
     * @param pos   The block position where the feature is generating from.
     * @param data  The feature data to generate from.
     * @return True if either primary feature or fallback feature was placed.
     */
    @SuppressWarnings( { "unchecked", "rawtypes", "ConstantConditions" } )
    public static boolean generate( @Nullable LevelAccessor level, @Nullable BlockPos pos, @Nullable FeatureData data ) {
        if( level == null || pos == null || data == null ) return false;
        
        // Don't do anything on client.
        if( !(level instanceof ServerLevel serverLevel) ) return false;
        
        final boolean debug = CrustConfig.UTILITIES.FEATURE_GEN.debugMode.get();
        
        // Neither feature ID nor tag is present, nothing to generate!
        if( data.configuredFeatureId == null && data.tagKey == null ) {
            if( debug ) {
                Crust.LOG.debug( "Feature generator at '{}' in dimension '{}' was flagged as ready for placement " +
                                "but had no feature ID or tag specified!",
                        pos, serverLevel.dimension().location() );
            }
            return false;
        }
        
        try {
            final RandomSource random = serverLevel.getRandom();
            final Registry<ConfiguredFeature<?, ?>> featureReg = serverLevel.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );
            
            ConfiguredFeature<?, ?> feature = featureReg.get( data.configuredFeatureId );
            TagKey<ConfiguredFeature<?, ?>> tagKey = data.tagKey;
            
            // Is feature ID not present? Try to grab random element from tag, if possible.
            if( feature == null && tagKey != null ) {
                Optional<HolderSet.Named<ConfiguredFeature<?, ?>>> optionalTag = featureReg.getTag( tagKey );
                
                if( optionalTag.isPresent() ) {
                    HolderSet.Named<ConfiguredFeature<?, ?>> tag = optionalTag.get();
                    Optional<Holder<ConfiguredFeature<?, ?>>> optionalFeature = tag.getRandomElement( random );
                    
                    if( optionalFeature.isPresent() )
                        feature = optionalFeature.get().get();
                }
            }
            // Does the feature exist? If not, try fetching fallback!
            if( feature == null && !ResourceLocationUtils.isEmpty( data.fallbackId ) ) {
                debugMsg( debug, "Feature generator at '{}' in dimension '{}' has no feature ID! Checking fallback feature...",
                        pos, serverLevel.dimension().location() );
                feature = featureReg.get( data.fallbackId );
            }
            if( feature == null ) {
                debugMsg( debug, "Feature generator at '{}' in dimension '{}' failed to generate anything!",
                        pos, serverLevel.dimension().location() );
                return false;
            }
            final int yPos = pos.getY() + data.yOffset;
            
            // If Y position ends up out of bounds, log a warning and give up
            if( yPos < level.getMinBuildHeight() || yPos > level.getMaxBuildHeight() ) {
                debugMsg( debug, "Feature generator at '{}' in dimension '{}' is trying to generate out of bounds! Generator's Y-offset: '{}'",
                        pos, serverLevel.dimension().location(), data.yOffset );
                return false;
            }
            boolean generated = false;
            
            // Roll placement chance!
            if( random.nextDouble() <= data.chance ) {
                // Set to air first so we don't accidentally block the placement.
                serverLevel.setBlock( pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE );
                final ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
                
                // Check if we are force generating (for a DW feature)
                if( data.forceGeneration ) {
                    FeaturePlaceContext context = new FeaturePlaceContext( Optional.of( DummyFeature.CONFIGURED_INSTANCE ), serverLevel, chunkGenerator, random, pos.atY( yPos ), feature.config() );
                    generated = feature.feature().place( context );
                }
                // Otherwise, normal generation logic
                else {
                    generated = feature.place( serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos.atY( yPos ) );
                }
                // Check if we should and can generate fallback feature
                if( !generated && data.getFallbackId() != null ) {
                    debugMsg( debug, "Feature generator failed first placement attempt! Trying to place fallback..." );
                    feature = featureReg.get( data.fallbackId );
                    
                    // Don't try generating a second time if we were already trying the fallback!
                    // noinspection ConstantConditions
                    if( feature != null && !featureReg.getKey( feature ).equals( data.fallbackId ) ) {
                        generated = feature.place( serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos.atY( yPos ) );
                    }
                }
            }
            // Replace generator with final "turns into" state.
            serverLevel.setBlock( pos, data.turnsInto, Block.UPDATE_CLIENTS );
            return generated;
        }
        // Something spooky happened!
        catch( Exception e ) {
            Crust.LOG.warn( "Feature generator at '{}' in dimension '{}' failed to generate its feature!",
                    pos, serverLevel.dimension().location() );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
            return false;
        }
    }
    
    /** Convenience method for logging a debug message during placement. */
    private static void debugMsg( boolean debug, String msg, Object... args ) {
        if( debug ) Crust.LOG.debug( msg, args );
    }
    
    /** Wrapper for the data used when generating a feature. */
    public static final class FeatureData {
        
        /** The optional ID of the feature to place. This can be null, as long as "tag" is specified. */
        @Nullable
        private ResourceLocation configuredFeatureId;
        /** An optional tag of configured features to pick from. This can be null, as long as "configuredFeatureId" is specified. */
        @Nullable
        private TagKey<ConfiguredFeature<?, ?>> tagKey;
        /** An optional ID of a fallback feature to try and generate if the original placement failed. */
        @Nullable
        private ResourceLocation fallbackId;
        /** The block state the feature generator should turn into before generating. */
        private BlockState turnsInto;
        /** The Y-offset of the block position to generate at. */
        private int yOffset;
        /** The chance for attempting the placement. */
        private double chance;
        /**
         * If true, the feature will be force placed.
         * Currently only works for features from DeadlyWorld.
         */
        private boolean forceGeneration;
        
        
        //---------------------- NBT keys ----------------------
        
        public static final String TAG_FEATURE_ID = "FeatureId";
        public static final String TAG_FALLBACK_ID = "FallbackId";
        public static final String TAG_FEATURE_TAG = "FeatureTag";
        public static final String TAG_TURNS_INTO = "TurnsIntoState";
        public static final String TAG_Y_OFFSET = "YOffset";
        public static final String TAG_CHANCE = "Chance";
        public static final String TAG_FORCE_GENERATION = "ForceGeneration";
        
        
        public FeatureData( @Nullable ResourceLocation configuredFeatureId,
                            @Nullable TagKey<ConfiguredFeature<?, ?>> tag,
                            @Nullable ResourceLocation fallbackId,
                            BlockState turnsInto, int yOffset, double chance, boolean forceGeneration ) {
            this.configuredFeatureId = configuredFeatureId;
            this.tagKey = tag;
            this.fallbackId = fallbackId;
            this.turnsInto = turnsInto;
            this.yOffset = yOffset;
            this.chance = chance;
            this.forceGeneration = forceGeneration;
        }
        
        @Nullable
        public ResourceLocation getConfiguredFeatureId() {
            return configuredFeatureId;
        }
        
        @Nullable
        public TagKey<ConfiguredFeature<?, ?>> getTagKey() {
            return tagKey;
        }
        
        @Nullable
        public ResourceLocation getFallbackId() {
            return fallbackId;
        }
        
        public BlockState getTurnsInto() {
            return turnsInto;
        }
        
        public int getYOffset() {
            return yOffset;
        }
        
        public double getChance() {
            return chance;
        }
        
        public boolean forceGeneration() {
            return forceGeneration;
        }
        
        /** Saves this instance's data to the given compound tag. */
        public void saveTo( CompoundTag saveTag ) {
            if( configuredFeatureId != null ) {
                saveTag.putString( TAG_FEATURE_ID, configuredFeatureId.toString() );
            }
            if( tagKey != null ) {
                saveTag.putString( TAG_FEATURE_TAG, tagKey.location().toString() );
            }
            if( fallbackId != null ) {
                saveTag.putString( TAG_FALLBACK_ID, fallbackId.toString() );
            }
            if( turnsInto != null ) {
                NBTHelper.putBlockState( saveTag, TAG_TURNS_INTO, turnsInto );
            }
            saveTag.putInt( TAG_Y_OFFSET, yOffset );
            saveTag.putDouble( TAG_CHANCE, chance );
            saveTag.putBoolean( TAG_FORCE_GENERATION, forceGeneration );
        }
        
        /** Loads data from the given compound tag and applies it to this instance. */
        public void loadFrom( CompoundTag loadTag ) {
            if( NBTHelper.containsString( loadTag, TAG_FEATURE_ID ) ) {
                ResourceLocation id = ResourceLocation.tryParse( loadTag.getString( TAG_FEATURE_ID ) );
                if( id != null ) configuredFeatureId = id;
            }
            if( NBTHelper.containsString( loadTag, TAG_FEATURE_TAG ) ) {
                ResourceLocation id = ResourceLocation.tryParse( loadTag.getString( TAG_FEATURE_TAG ) );
                if( id != null ) tagKey = TagKey.create( Registries.CONFIGURED_FEATURE, id );
            }
            if( NBTHelper.containsString( loadTag, TAG_FALLBACK_ID ) ) {
                ResourceLocation id = ResourceLocation.tryParse( loadTag.getString( TAG_FALLBACK_ID ) );
                if( id != null ) fallbackId = id;
            }
            turnsInto = NBTHelper.getBlockState( loadTag, TAG_TURNS_INTO );
            
            if( NBTHelper.containsNumber( loadTag, TAG_Y_OFFSET ) ) {
                yOffset = loadTag.getInt( TAG_Y_OFFSET );
            }
            if( NBTHelper.containsNumber( loadTag, TAG_CHANCE ) ) {
                chance = loadTag.getDouble( TAG_CHANCE );
            }
            if( NBTHelper.containsNumber( loadTag, TAG_FORCE_GENERATION ) ) {
                forceGeneration = loadTag.getBoolean( TAG_FORCE_GENERATION );
            }
        }
        
        /** @return A new empty / default FeatureData instance. */
        public static FeatureData newEmpty() {
            return new FeatureData( null, null, null,
                    Blocks.AIR.defaultBlockState(), 0, 1.0, false );
        }
    }
}
