package fathertoast.crust.common.block.entity;

import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.lib.NBTHelper;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
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
     */
    @Override
    public void onLoad() {
        if( level == null ) return;
        
        // Check if we should try generating.
        if( isReadyForGen() ) {
            generate( level, getBlockPos(), getData(), false );
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
     * @param debug True if this generation call is a test.
     * @return True if nothing went wrong and {@link net.minecraft.world.level.levelgen.feature.Feature#place(FeatureConfiguration, WorldGenLevel, ChunkGenerator, RandomSource, BlockPos)}
     * returned true for the feature that was attempted to place.
     */
    public static boolean generate( LevelReader level, BlockPos pos, FeatureData data, boolean debug ) {
        Objects.requireNonNull( level );
        
        // Don't do anything on client.
        if( !(level instanceof ServerLevel serverLevel) ) return false;
        
        // Neither feature ID nor tag is present, nothing to generate!
        if( data.getConfiguredFeatureId() == null && data.getTag() == null )
            return false;
        
        try {
            final RandomSource random = serverLevel.getRandom();
            final Registry<ConfiguredFeature<?, ?>> featureReg = serverLevel.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );
            
            ConfiguredFeature<?, ?> feature = featureReg.get( data.getConfiguredFeatureId() );
            TagKey<ConfiguredFeature<?, ?>> tagKey = data.getTag();
            
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
            // No feature, we can't proceed!
            if( feature == null ) {
                if( debug ) {
                    Crust.LOG.warn( "Feature generator tried generating with null feature! Feature ID and tag key are both invalid or empty." );
                }
                return false;
            }
            final int yPos = pos.getY() + data.getYOffset();
            
            // If Y position ends up out of bounds, log a warning and give up
            if( yPos < level.getMinBuildHeight() || yPos > level.getMaxBuildHeight() ) {
                if( debug ) {
                    Crust.LOG.warn( "Feature generator at '{}' in dimension '{}' is trying to generate out of bounds! Generator's Y-offset: '{}'",
                            pos, serverLevel.dimension().location(), data.getYOffset() );
                }
                return false;
            }
            // Set to air first so we don't mess with generation
            serverLevel.setBlock( pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE );
            // Try generating!
            boolean generated = feature.place( serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos.atY( yPos ) );
            
            // Replace generator with final "turns into" state.
            serverLevel.setBlock( pos, data.getTurnsInto(), SaplingBlock.UPDATE_CLIENTS );
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
    
    /** Wrapper for the data used when generating a feature. */
    public static final class FeatureData {
        
        /** The optional ID of the feature to place. */
        @Nullable
        private ResourceLocation configuredFeatureId;
        
        /** An optional tag of configured features to pick from. */
        @Nullable
        private TagKey<ConfiguredFeature<?, ?>> tag;
        
        /** The block state the feature generator should turn into before generating. */
        private BlockState turnsInto;
        
        /** The Y-offset of the block position to generate at. */
        private int yOffset;
        
        
        //---------------------- NBT keys ----------------------
        
        public static final String TAG_FEATURE_ID = "FeatureId";
        public static final String TAG_FEATURE_TAG = "FeatureTag";
        public static final String TAG_TURNS_INTO = "TurnsIntoState";
        public static final String TAG_Y_OFFSET = "YOffset";
        
        
        public FeatureData( @Nullable ResourceLocation configuredFeatureId,
                            @Nullable TagKey<ConfiguredFeature<?, ?>> tag,
                            BlockState turnsInto,
                            int yOffset ) {
            this.configuredFeatureId = configuredFeatureId;
            this.tag = tag;
            this.turnsInto = turnsInto;
            this.yOffset = yOffset;
        }
        
        @Nullable
        public ResourceLocation getConfiguredFeatureId() {
            return configuredFeatureId;
        }
        
        @Nullable
        public TagKey<ConfiguredFeature<?, ?>> getTag() {
            return tag;
        }
        
        public BlockState getTurnsInto() {
            return turnsInto;
        }
        
        public int getYOffset() {
            return yOffset;
        }
        
        /** Saves this instance's data to NBT. */
        public void saveTo( CompoundTag saveTag ) {
            if( configuredFeatureId != null ) {
                saveTag.putString( TAG_FEATURE_ID, configuredFeatureId.toString() );
            }
            if( tag != null ) {
                saveTag.putString( TAG_FEATURE_TAG, tag.location().toString() );
            }
            if( turnsInto != null ) {
                NBTHelper.putBlockState( saveTag, TAG_TURNS_INTO, turnsInto );
            }
            saveTag.putInt( TAG_Y_OFFSET, yOffset );
        }
        
        /** Loads data from NBT and applies it to this instance. */
        public void loadFrom( CompoundTag loadTag ) {
            if( NBTHelper.containsString( loadTag, TAG_FEATURE_ID ) ) {
                ResourceLocation id = ResourceLocation.tryParse( loadTag.getString( TAG_FEATURE_ID ) );
                if( id != null ) configuredFeatureId = id;
            }
            if( NBTHelper.containsString( loadTag, TAG_FEATURE_TAG ) ) {
                ResourceLocation id = ResourceLocation.tryParse( loadTag.getString( TAG_FEATURE_TAG ) );
                if( id != null ) tag = TagKey.create( Registries.CONFIGURED_FEATURE, id );
            }
            turnsInto = NBTHelper.getBlockState( loadTag, TAG_TURNS_INTO );
            yOffset = loadTag.getInt( TAG_Y_OFFSET );
        }
        
        /** @return A new empty / default FeatureData instance. */
        public static FeatureData newEmpty() {
            return new FeatureData( null, null, Blocks.AIR.defaultBlockState(), 0 );
        }
    }
}
