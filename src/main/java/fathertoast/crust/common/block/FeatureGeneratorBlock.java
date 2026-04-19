package fathertoast.crust.common.block;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public class FeatureGeneratorBlock extends Block implements EntityBlock {
    
    
    public FeatureGeneratorBlock() {
        super( BlockBehaviour.Properties.of()
                .strength( -1.0F, 3600000.0F )
                .sound( SoundType.AMETHYST )
                .noLootTable()
        );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult ) {
        // Do nothing if player is not in creative
        if( !player.isCreative() ) return InteractionResult.PASS;
        
        if( level.getExistingBlockEntity( pos ) instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            // Test generation code if player has debug stick and is sneaking.
            if( player.getItemInHand( hand ).getItem() == Items.DEBUG_STICK ) {
                return FeatureGeneratorBlockEntity.generate( level, featureGenerator.getBlockPos(), featureGenerator.getData() )
                        ? InteractionResult.sidedSuccess( level.isClientSide )
                        : InteractionResult.PASS;
            }
            else if( player instanceof ServerPlayer serverPlayer ) {
                // Open editor screen
                getScreenOpener().accept( serverPlayer, featureGenerator );
            }
            return InteractionResult.sidedSuccess( level.isClientSide );
        }
        return InteractionResult.FAIL;
    }
    
    /**
     * Called when A user uses the creative pick block button on this block.
     * <br><br>
     * Overridden to copy over feature gen data to the cloned item stack.
     */
    @Override
    public ItemStack getCloneItemStack( BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player ) {
        if( level.getExistingBlockEntity( pos ) instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            final FeatureGeneratorBlockEntity.FeatureData featureData = featureGenerator.getData();
            
            if( featureData.isEmpty() )
                return super.getCloneItemStack( state, target, level, pos, player );
            
            final ItemStack cloneStack = new ItemStack( this );
            
            CompoundTag blockEntityData = NBTHelper.getOrCreateCompound( cloneStack.getOrCreateTag(), BlockItem.BLOCK_ENTITY_TAG );
            featureData.saveTo( blockEntityData );
            
            return cloneStack;
        }
        return super.getCloneItemStack( state, target, level, pos, player );
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable BlockGetter level, List<Component> components, TooltipFlag tooltipFlag ) {
        super.appendHoverText( itemStack, level, components, tooltipFlag );
        
        CompoundTag compoundTag = itemStack.getTag();
        if( compoundTag == null ) return;
        
        if( NBTHelper.containsCompound( compoundTag, BlockItem.BLOCK_ENTITY_TAG ) ) {
            CompoundTag blockEntityData = compoundTag.getCompound( BlockItem.BLOCK_ENTITY_TAG );
            FeatureGeneratorBlockEntity.FeatureData data = FeatureGeneratorBlockEntity.FeatureData.newEmpty();
            
            data.loadFrom( blockEntityData );
            FeatureGeneratorBlockEntity.FeatureData.appendHoverText( data, components, tooltipFlag );
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void tick( BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random ) {
        if( serverLevel.getExistingBlockEntity( pos ) instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            if( featureGenerator.isReadyForGen() )
                FeatureGeneratorBlockEntity.generate( serverLevel, featureGenerator.getBlockPos(), featureGenerator.getData() );
        }
    }
    
    /** @return A BiConsumer that requests opening X screen for Y player. */
    protected BiConsumer<ServerPlayer, BlockEntity> getScreenOpener() {
        return CrustPacketHandler::openFeatureGeneratorScreen;
    }
    
    @Override
    @Nullable
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new FeatureGeneratorBlockEntity( pos, state );
    }
}
