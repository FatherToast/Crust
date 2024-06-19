package fathertoast.crust.api.datagen.loot;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;

/**
 * Contains helper methods used for building loot tables.
 *
 * @see LootTableBuilder
 */
public final class LootHelper {
    
    /** The condition that evaluates to true in an entity loot table when the killer is a player. */
    public static final LootItemCondition.Builder KILLED_BY_PLAYER_CONDITION = LootItemKilledByPlayerCondition.killedByPlayer();
    
    /** The 'uncommon' condition; that is, 25% chance + 5% chance per luck when killed by player. */
    public static final LootItemCondition.Builder[] UNCOMMON_CONDITIONS = {
            LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost( 0.25F, 0.05F ),
            KILLED_BY_PLAYER_CONDITION
    };
    /** The 'rare' condition; that is, 2.5% chance when killed by player. */
    public static final LootItemCondition.Builder[] RARE_CONDITIONS = {
            LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost( 0.025F, 0.0F ),
            KILLED_BY_PLAYER_CONDITION
    };
    
    /** Exactly one roll. Mostly used for defaults. */
    public static final ConstantValue ONE_ROLL = ConstantValue.exactly( 1 );
    /** No rolls. Mostly used for defaults. */
    public static final ConstantValue NO_ROLL = ConstantValue.exactly( 0 );
    
    /** Convenience method to put all loot entries, conditions, and functions into a loot pool. Returns the loot pool. */
    public static LootPool.Builder build( LootPool.Builder builder, List<LootPoolEntryContainer.Builder<?>> entries,
                                         List<LootItemCondition.Builder> conditions, List<LootItemFunction.Builder> functions ) {
        return build( build( builder, entries ), conditions, functions );
    }


    /** Convenience method to put all loot conditions and functions into a loot builder. Returns the loot builder. */
    public static <T extends FunctionUserBuilder<?>> // Can't figure out how to require both, but function consumer is more stringent
    T build( T builder, List<LootItemCondition.Builder> conditions, List<LootItemFunction.Builder> functions ) {
        build( (ConditionUserBuilder<?> ) builder, conditions );
        return build( builder, functions );
    }

    /** Convenience method to put all loot pools into a loot table. Returns the loot table. */
    public static LootTable.Builder build( LootTable.Builder builder, List<LootPool.Builder> pools ) {
        for( LootPool.Builder pool : pools ) builder.withPool( pool );
        return builder;
    }
    
    /** Convenience method to put all loot entries into a loot pool. Returns the loot pool. */
    public static LootPool.Builder build( LootPool.Builder builder, List<LootPoolEntryContainer.Builder<?>> entries ) {
        for( LootPoolEntryContainer.Builder<?> entry : entries ) builder.add( entry );
        return builder;
    }
    
    /** Convenience method to put all loot conditions into a loot builder. Returns the loot builder. */
    public static <T extends ConditionUserBuilder<?>> T build(T builder, List<LootItemCondition.Builder> conditions ) {
        for( LootItemCondition.Builder condition : conditions ) builder.when( condition );
        return builder;
    }
    
    /** Convenience method to put all loot functions into a loot builder. Returns the loot builder. */
    public static <T extends FunctionUserBuilder<?>> T build(T builder, List<LootItemFunction.Builder> functions ) {
        for( LootItemFunction.Builder function : functions ) builder.apply( function );
        return builder;
    }
}