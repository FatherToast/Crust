package fathertoast.crust.client.renderer.entryview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fathertoast.crust.api.config.common.value.collection.key.EntityKey.Extends.ErroredEntity;

/**
 * An entry view renderer implementation that renders a
 * static entity model based on an entity type value, if
 * a level object is available. If not, the entity's spawn egg
 * item is displayed instead, if available.
 */
public class EntityTypeEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<EntityType<?>> {
    
    /** A map of entity types linked to W item stack containing their spawn egg, if it exists. */
    private static final Map<EntityType<?>, ItemStack> SPAWN_EGG_STACKS = new HashMap<>();
    /** A map of entity types linked to related entity instances. */
    private static final Map<EntityType<?>, Entity> ENTITY_INSTANCES = new HashMap<>();
    
    /** Called when a config file screen is closed to free up memory. */
    public static void releaseInstances() { ENTITY_INSTANCES.clear(); }
    
    private static Entity getEntity( EntityType<?> entityType, ClientLevel level ) {
        return ENTITY_INSTANCES.computeIfAbsent( entityType, type -> {
            try {
                Entity instance = type.create( level );
                if( instance != null ) {
                    return instance;
                }
            }
            catch( Exception e ) {
                // noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
            return new ErroredEntity( type, level );
        } );
    }
    
    
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( EntityType<?> displayValue, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        // Check if the game's level object exists.
        // If it does, try to render the entity type's entity model.
        ClientLevel level = Minecraft.getInstance().level;
        float width = displayValue.getWidth();
        float height = displayValue.getHeight();
        if( level != null && (width > 0.0F || height > 0.0F) ) {
            // Create entity instances when they are needed.
            final Entity entity = getEntity( displayValue, level );
            if( !(entity instanceof ErroredEntity) ) {
                PoseStack poseStack = graphics.pose();
                poseStack.pushPose();
                
                // Scale to fit the bounding box in a 16x16 area
                float scale;
                float yOffset;
                if( width > 0.0F ) {
                    float diagonalWidth = 1.4142135623731F * width;
                    if( height > 0.0F ) {
                        float diagonalHeight = Mth.sqrt( diagonalWidth * diagonalWidth + height * height ) *
                                (float) Math.sin( Math.atan2( height, diagonalWidth ) + 0.523598775598299 );
                        scale = 16.0F / Math.max( diagonalWidth, diagonalHeight );
                        yOffset = (diagonalHeight - height) / 2.0F * scale;
                    }
                    else {
                        scale = 16.0F / diagonalWidth;
                        yOffset = 0.0F;
                    }
                }
                else {
                    scale = 18.475208614068F / height;
                    yOffset = 0.0F;
                }
                
                // X, Y and depth (Z)
                poseStack.translate( widgetX + 10, widgetY + 17 - yOffset, -100 );
                // Flip the pose so we don't render things upside-down
                poseStack.mulPoseMatrix( new Matrix4f().scaling( 1.0F, -1.0F, 1.0F ) );
                // Standard transforms used for blocks in GUIs, but flipped 180 deg around Y axis
                poseStack.mulPose( Axis.XP.rotationDegrees( 30.0F ) );
                poseStack.mulPose( Axis.YN.rotationDegrees( 45.0F ) );
                poseStack.scale( scale, scale, scale );
                
                renderEntity( Minecraft.getInstance().getEntityRenderDispatcher().getRenderer( entity ),
                        entity, 0.0F, 0.0F, poseStack, graphics );
                
                poseStack.popPose();
                return;
            }
        }
        // Otherwise, try rendering the entity type's spawn egg if it exists.
        try {
            final ItemStack displayStack = SPAWN_EGG_STACKS.get( displayValue );
            if( !displayStack.isEmpty() ) {
                EntryViewWidget.EntryViewRenderer<ItemStack> itemStackRenderer = EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK );
                itemStackRenderer.render( displayStack, graphics, widgetX, widgetY, mouseX, mouseY, partialTick );
            }
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    private <T extends Entity> void renderEntity( EntityRenderer<T> renderer, Entity entity, float rotation, float partialTick,
                                                  PoseStack poseStack, GuiGraphics graphics ) {
        //noinspection unchecked
        renderer.render( (T) entity, rotation, partialTick, poseStack,
                graphics.bufferSource(), LightTexture.FULL_BRIGHT );
    }
    
    /** Called when the display value is changed to populate the widget's tooltip. */
    @Override
    public void updateTooltip( List<FormattedCharSequence> tooltip, EntityType<?> displayValue ) {
        addLine( tooltip, displayValue.getDescription() );
    }
    
    /** Called after mod loading has completed to perform any required setup before use. */
    @Override
    public void setup() {
        for( EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES ) {
            // Collect spawn eggs from all entities and store
            // item stacks of them for later.
            final Item spawnEgg = ForgeSpawnEggItem.fromEntityType( entityType );
            if( spawnEgg != null ) {
                SPAWN_EGG_STACKS.put( entityType, new ItemStack( spawnEgg ) );
            }
            else {
                SPAWN_EGG_STACKS.put( entityType, ItemStack.EMPTY );
            }
        }
    }
}