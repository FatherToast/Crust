package fathertoast.crust.client.renderer.entryview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
    
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( @Nullable Supplier<EntityType<?>> valueSupplier, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        final EntityType<?> entityType = getValue( valueSupplier );
        
        if( entityType == null ) return;
        
        final PoseStack stack = graphics.pose();
        
        ClientLevel level = Minecraft.getInstance().level;
        
        // Check if the game's level object exists.
        // If it does, we render the entity type's entity model.
        if( level != null ) {
            // Create entity instances when they are needed.
            final Entity entity = ENTITY_INSTANCES.computeIfAbsent( entityType, ( type ) -> {
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
            
            
            if( !(entity instanceof ErroredEntity) ) {
                stack.pushPose();
                
                float scale = 0.53125F;
                float girth = Math.max( entity.getBbWidth(), entity.getBbHeight() );
                if( girth > 1.0F ) scale /= girth;
                
                // X, Y and depth (Z)
                stack.translate( widgetX + 10, widgetY + 18, 150 );
                // Flip the pose so we don't render things upside-down
                stack.mulPoseMatrix( (new Matrix4f()).scaling( 1.0F, -1.0F, 1.0F ) );
                // Standard transforms used for blocks in GUIs
                stack.mulPose( Axis.XP.rotationDegrees( 30.0F ) );
                stack.mulPose( Axis.YN.rotationDegrees( 225.0F ) );
                stack.scale( scale, scale, scale );
                
                // noinspection unchecked
                EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer( entity );
                // renderer.render( entity, 0.0F, partialTick, stack, graphics.bufferSource(), LightTexture.FULL_BRIGHT );
                
                stack.popPose();
            }
        }
        // Otherwise, try rendering the entity type's spawn egg if it exists.
        else {
            try {
                final ItemStack displayStack = SPAWN_EGG_STACKS.get( entityType );
                
                if( !displayStack.isEmpty() ) {
                    EntryViewWidget.EntryViewRenderer<ItemStack> itemStackRenderer = EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK );
                    itemStackRenderer.render( () -> displayStack, graphics, widgetX, widgetY, mouseX, mouseY, partialTick );
                }
            }
            catch( Exception e ) {
                // noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
    
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
