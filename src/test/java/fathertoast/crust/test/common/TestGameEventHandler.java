package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.event.advancement.AdvancementLoadEvent;
import fathertoast.crust.api.event.advancement.IModifiableAdvancement;
import fathertoast.crust.api.event.advancement.IModifiableDisplayInfo;
import fathertoast.crust.api.event.advancement.IModifiableReward;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public class TestGameEventHandler {
    
    private static int elCountastico;
    
    //@SubscribeEvent // Comment the annotation out to 'turn off' this console spam
    static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            if( elCountastico++ > 40 ) {
                elCountastico = 0;
                
                List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();
                if( players.isEmpty() ) return;
                ServerPlayer player = players.get( 0 );
                
                TestCrust.LOG.info( "Environment Test Results for {} (-1 is 'no match'):", player.getGameProfile().getName() );
                TestCrust.LOG.info( "  {} = {}", TestCrust.CONFIG.GENERAL.environmentListField.getKey(),
                        TestCrust.CONFIG.GENERAL.environmentListField.getOrElse(
                                EnvironmentContext.withTarget( player ), -1 ) );
            }
        }
    }
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingHurt( LivingHurtEvent event ) {
        final Level level = event.getEntity().level();
        
        if( level.isClientSide() ) return;
        if( event.getSource().getEntity() instanceof Player player ) {
            // Test registry map
            EntityType<?> entityType = event.getEntity().getType();
            Integer intVal = TestCrust.CONFIG.GENERAL.registryMapField.get( entityType );
            if( intVal == null ) {
                TestCrust.LOG.debug( "Entity NOT matched: {}", entityType );
            }
            else {
                TestCrust.LOG.debug( "Value = {} for entity: {}", intVal, entityType );
            }
            
            // Test entity map
            Double[] doubles = TestCrust.CONFIG.GENERAL.entityMapField.get( event.getEntity() );
            if( doubles == null ) {
                TestCrust.LOG.debug( "Entity NOT matched: {}", entityType );
            }
            else {
                TestCrust.LOG.debug( "Values = {} for entity: {}", doubles, entityType );
            }
            
            // Test number set
            ItemStack heldStack = player.getMainHandItem();
            
            if( TestCrust.CONFIG.GENERAL.numberSetField.contains( heldStack.getCount() ) ) {
                // Hurrah! Let us celebrate with murder and fireworks :D
                FireworkRocketEntity rocket = new FireworkRocketEntity( level, new ItemStack( Items.FIREWORK_ROCKET ), event.getEntity() );
                level.addFreshEntity( rocket );
            }
        }
    }
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onAdvancementLoad( AdvancementLoadEvent event ) {
        final ResourceLocation advancementId = event.getId();
        
        /// Test for modifying criteria.
        /// Expected result:
        ///    - Instead of any seed, ALL seeds must be planted.
        ///    - "Cobbletoast" block must also be placed.
        if( advancementId.equals( ResourceLocation.withDefaultNamespace( "husbandry/plant_seed" ) ) ) {
            final IModifiableAdvancement advancement = event.getAdvancement();
            
            advancement.addCriterion(
                    "cobbletoast",
                    ItemUsedOnLocationTrigger.TriggerInstance.placedBlock( TestCrustObjects.Obj.TEST_BLOCK.get() ),
                    false
            );
            advancement.setRequirementsStrategy( RequirementsStrategy.AND );
        }
        
        ///  Test for the reward system.
        ///  Expected result:
        ///      - Grants 30 experience points.
        ///      - Drops "minecraft:chests/simple_dungeon" loot table.
        ///      - Unlocks recipe for Golden Carrot item.
        else if( advancementId.equals( ResourceLocation.withDefaultNamespace( "husbandry/tactical_fishing" ) ) ) {
            final IModifiableAdvancement advancement = event.getAdvancement();
            final IModifiableReward reward = advancement.getReward();
            
            reward.addExp( 30 );
            reward.getLootTables().add( ResourceLocation.withDefaultNamespace( "chests/simple_dungeon" ) );
            reward.getUnlockedRecipes().add( ResourceLocation.withDefaultNamespace( "recipes/brewing/golden_carrot" ) );
        }
        
        ///  Test for modifying display info.
        ///  Expected result:
        ///      - Replace icon with barrier block.
        ///      - Becomes hidden.
        ///      - Frame type gets set to {@link FrameType.CHALLENGE}.
        else if( advancementId.equals( ResourceLocation.withDefaultNamespace( "nether/brew_potion" ) ) ) {
            final IModifiableAdvancement advancement = event.getAdvancement();
            final IModifiableDisplayInfo displayInfo = advancement.getOrCreateDisplayInfo();
            
            displayInfo.setIconItem( Items.BARRIER );
            displayInfo.setHidden( true );
            displayInfo.setFrameType( FrameType.CHALLENGE );
        }
    }
}