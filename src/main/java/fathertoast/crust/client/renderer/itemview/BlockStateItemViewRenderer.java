package fathertoast.crust.client.renderer.itemview;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.crust.api.client.renderer.level.FakeBlockAndTintGetter;
import fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget;
import fathertoast.crust.api.lib.CrustMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * An item view renderer implementation that renders a block state.
 * <br><br>
 * If the block state to render either doesn't have a block model
 * or likely uses a block entity renderer, an item stack containing
 * the state's block will be rendered instead, if possible.
 */
public class BlockStateItemViewRenderer implements ItemViewWidget.ItemViewRenderer<BlockState> {
    
    /** A "dummy" {@link net.minecraft.world.level.BlockAndTintGetter} implementation used to render liquids. */
    private static final FakeBlockAndTintGetter FAKE_TINT_GETTER = new FakeBlockAndTintGetter();
    
    /**
     * A list of block states that most likely have
     * bloc entity renderers associated with them.
     */
    private final ArrayList<BlockState> statesWithRenderers = new ArrayList<>();
    
    
    /**
     * Called from {@link ItemViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( ItemViewWidget.RenderContext<BlockState> ctx ) {
        final BlockState blockState = ctx.value();
        
        if( blockState == null ) return;
        
        final int x = ctx.widgetX();
        final int y = ctx.widgetY();
        final PoseStack pose = ctx.graphics().pose();
        final MultiBufferSource.BufferSource bufferSource = ctx.graphics().bufferSource();
        final RenderShape renderShape = blockState.getRenderShape();
        
        // Fluids/liquids needs special handling.
        if( blockState.getBlock() instanceof LiquidBlock && !blockState.getFluidState().isEmpty() ) {
            renderFluidFace( blockState.getFluidState(), ctx.graphics(), x, y );
        }
        // Try rendering the block state as an item
        // instead if it is invisible or uses a block entity renderer.
        else if( renderShape == RenderShape.INVISIBLE || statesWithRenderers.contains( blockState ) ) {
            renderItemStack( blockState, ctx.graphics(), x, y );
        }
        // Otherwise render the block state itself.
        else {
            renderBlockState( blockState, bufferSource, pose, x, y );
        }
    }
    
    /** Renders the given block state as an item stack. */
    private void renderItemStack( BlockState state, GuiGraphics graphics, int x, int y ) {
        // Params: stack, x, y, seed, depth
        graphics.renderItem( new ItemStack( state.getBlock() ), x + 2, y + 2, 0, -135 );
    }
    
    /** Renders the given block state with transforms similar to GUI items. */
    private void renderBlockState( BlockState state, MultiBufferSource.BufferSource bufferSource, PoseStack pose, int x, int y ) {
        pose.pushPose();
        // X, Y and depth (Z)
        pose.translate( x + 10, y + 18, 15 );
        // Flip the pose so we don't render things upside-down
        pose.mulPoseMatrix( (new Matrix4f()).scaling( 1.0F, -1.0F, 1.0F ) );
        // Standard transforms used for blocks in GUIs
        pose.mulPose( Axis.XP.rotationDegrees( 30.0F ) );
        pose.mulPose( Axis.YN.rotationDegrees( 225.0F ) );
        // A somewhat OK-ish looking scale, perchance
        pose.scale( 10.0F, 10.0F, 10.0F );
        
        final BlockRenderDispatcher renderDispatcher = Minecraft.getInstance().getBlockRenderer();
        final BakedModel model = renderDispatcher.getBlockModel( state );
        final ChunkRenderTypeSet renderTypeSet = model.getRenderTypes( state, RandomSource.create( state.getSeed( BlockPos.ZERO ) ), ModelData.EMPTY );
        
        FAKE_TINT_GETTER.setOriginPos( BlockPos.ZERO );
        FAKE_TINT_GETTER.setCurrentTint( Minecraft.getInstance().getBlockColors().getColor( state, null, null, 0 ) );
        FAKE_TINT_GETTER.setCurrentBlockState( state );
        FAKE_TINT_GETTER.setCurrentFluidState( state.getFluidState() );
        
        for( RenderType renderType : renderTypeSet ) {
            renderType = RenderTypeHelper.getMovingBlockRenderType( renderType );
            renderDispatcher.getModelRenderer().tesselateWithAO(
                    FAKE_TINT_GETTER, model, state,
                    BlockPos.ZERO, pose, bufferSource.getBuffer( renderType ),
                    false, RandomSource.create(), state.getSeed( BlockPos.ZERO ),
                    OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType );
        }
        pose.popPose();
    }
    
    /** Attempts to render a single face using the given fluid's still texture. */
    public static void renderFluidFace( FluidState fluidState, GuiGraphics graphics, int x, int y ) {
        final Minecraft mc = Minecraft.getInstance();
        final IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of( fluidState );
        final TextureAtlasSprite sprite = mc.getTextureAtlas( InventoryMenu.BLOCK_ATLAS ).apply( props.getStillTexture() );
        final int color = props.getTintColor();
        
        // Apply default tint
        RenderSystem.setShaderColor( CrustMath.getRed( color ), CrustMath.getGreen( color ), CrustMath.getBlue( color ), 1.0F );
        
        // Enable default blending if render type is translucent
        if( ItemBlockRenderTypes.getRenderLayer( fluidState ) == RenderType.translucent() ) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        // Draw fluid texture
        graphics.blit( x + 2, y + 2, 0, 16, 16, sprite );
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
        RenderSystem.disableBlend();
    }
    
    /**
     * Called after mod loading has completed to
     * perform any required setup before use.
     */
    @Override
    public void setup() {
        final Map<BlockEntityType<?>, BlockEntityRendererProvider<?>> providers;
        
        // TODO - using reflection for now because using AT on the PROVIDERS map in BlockEntityRenderers does not work??
        try {
            Field field = ObfuscationReflectionHelper.findField( BlockEntityRenderers.class, "f_173587_" ); // PROVIDERS
            // noinspection unchecked
            providers = (Map<BlockEntityType<?>, BlockEntityRendererProvider<?>>) field.get( null );
        }
        catch( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
        
        // Loop through every single block state for every registered block
        // to find which states provide block entities that have special renderers.
        for( Block block : ForgeRegistries.BLOCKS ) {
            for( BlockState blockState : block.getStateDefinition().getPossibleStates() ) {
                if( blockState.getBlock() instanceof EntityBlock entityBlock ) {
                    final BlockEntity blockEntity = entityBlock.newBlockEntity( BlockPos.ZERO, blockState );
                    
                    if( blockEntity == null ) continue;
                    
                    if( !statesWithRenderers.contains( blockState ) && providers.containsKey( blockEntity.getType() ) ) {
                        statesWithRenderers.add( blockState );
                    }
                }
            }
        }
    }
}
