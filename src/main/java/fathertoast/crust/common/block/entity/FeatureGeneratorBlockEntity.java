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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FeatureGeneratorBlockEntity extends BlockEntity {
    
    /** The feature generator data to use when generating. */
    private FeatureData data = FeatureData.newEmpty();
    
    
    public FeatureGeneratorBlockEntity( BlockPos pos, BlockState state ) {
        super( CrustObjects.BlockEntities.FEATURE_GENERATOR.get(), pos, state );
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        if( data != null ) data.saveTo( saveTag );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        data.loadFrom( loadTag );
    }
    
    /** @return This feature generator's generation settings. */
    public FeatureData getData() {
        return data;
    }
    
    /** Sets the generation data for this feature generator. */
    public void setData( FeatureData data ) {
        this.data = Objects.requireNonNull( data );
    }
    
    /**
     * Attempts to generate a feature using the current generation data.
     *
     * @return True if placing the feature succeeded.
     */
    public boolean generate() {
        // Don't do anything on client or without level.
        if( level == null || level.isClientSide )
            return false;
        
        // Neither feature ID nor tag is present, nothing to generate!
        if( data.getConfiguredFeatureId() == null && data.getTag() == null )
            return false;
        
        try {
            final ServerLevel serverLevel = (ServerLevel) level;
            final RandomSource random = level.getRandom();
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
                Crust.LOG.warn( "Feature generator tried generating with null feature. Feature ID and tag key are both invalid or empty." );
                return false;
            }
            final int yOffset = data.getYOffset();
            final int yPos = getBlockPos().getY() + yOffset;
            
            // If Y position ends up out of bounds, log a warning and give up
            if( yPos < level.getMinBuildHeight() || yPos > level.getMaxBuildHeight() ) {
                Crust.LOG.warn( "Feature generator at '{}' in dimension '{}' is trying to generate out of bounds! Generator's Y-offset: '{}'",
                        getBlockPos(), level.dimension().location(), yOffset );
                return false;
            }
            
            // Replace self with configured state
            level.setBlock( worldPosition, data.turnsInto, SaplingBlock.UPDATE_CLIENTS );
            // Try generating!
            feature.place( serverLevel, serverLevel.getChunkSource().getGenerator(), random, getBlockPos().atY( yPos ) );
            return true;
        }
        catch( Exception e ) {
            Crust.LOG.warn( "Feature generator at '{}' in dimension '{}' failed to generate its feature!",
                    getBlockPos(), level.dimension().location() );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
            return false;
        }
    }
    
    /** Wrapper for the data used when generating a feature. */
    public static class FeatureData {
        
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
