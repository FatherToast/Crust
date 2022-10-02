package fathertoast.crust.api.datagen.loot;

import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChanceWithLooting;
import net.minecraft.loot.functions.ILootFunction;

import java.util.List;

/**
 * Contains helper methods used for building loot tables.
 *
 * @see LootTableBuilder
 */
public final class LootHelper {
    
    /** The condition that evaluates to true in an entity loot table when the killer is a player. */
    public static final ILootCondition.IBuilder KILLED_BY_PLAYER_CONDITION = KilledByPlayer.killedByPlayer();
    
    /** The 'uncommon' condition; that is, 25% chance + 5% chance per luck when killed by player. */
    public static final ILootCondition.IBuilder[] UNCOMMON_CONDITIONS = {
            RandomChanceWithLooting.randomChanceAndLootingBoost( 0.25F, 0.05F ),
            KILLED_BY_PLAYER_CONDITION
    };
    /** The 'rare' condition; that is, 2.5% chance when killed by player. */
    public static final ILootCondition.IBuilder[] RARE_CONDITIONS = {
            RandomChanceWithLooting.randomChanceAndLootingBoost( 0.025F, 0.0F ),
            KILLED_BY_PLAYER_CONDITION
    };
    
    /** Exactly one roll. Mostly used for defaults. */
    public static final RandomValueRange ONE_ROLL = new RandomValueRange( 1 );
    /** No rolls. Mostly used for defaults. */
    public static final RandomValueRange NO_ROLL = new RandomValueRange( 0 );
    
    /** Convenience method to put all loot entries, conditions, and functions into a loot pool. Returns the loot pool. */
    public static LootPool.Builder build( LootPool.Builder builder, List<LootEntry.Builder<?>> entries,
                                          List<ILootCondition.IBuilder> conditions, List<ILootFunction.IBuilder> functions ) {
        return build( build( builder, entries ), conditions, functions );
    }
    
    /** Convenience method to put all loot conditions and functions into a loot builder. Returns the loot builder. */
    public static <T extends ILootFunctionConsumer<?>> // Can't figure out how to require both, but function consumer is more stringent
    T build( T builder, List<ILootCondition.IBuilder> conditions, List<ILootFunction.IBuilder> functions ) {
        build( (ILootConditionConsumer<?>) builder, conditions );
        return build( builder, functions );
    }
    
    /** Convenience method to put all loot pools into a loot table. Returns the loot table. */
    public static LootTable.Builder build( LootTable.Builder builder, List<LootPool.Builder> pools ) {
        for( LootPool.Builder pool : pools ) builder.withPool( pool );
        return builder;
    }
    
    /** Convenience method to put all loot entries into a loot pool. Returns the loot pool. */
    public static LootPool.Builder build( LootPool.Builder builder, List<LootEntry.Builder<?>> entries ) {
        for( LootEntry.Builder<?> entry : entries ) builder.add( entry );
        return builder;
    }
    
    /** Convenience method to put all loot conditions into a loot builder. Returns the loot builder. */
    public static <T extends ILootConditionConsumer<?>> T build( T builder, List<ILootCondition.IBuilder> conditions ) {
        for( ILootCondition.IBuilder condition : conditions ) builder.when( condition );
        return builder;
    }
    
    /** Convenience method to put all loot functions into a loot builder. Returns the loot builder. */
    public static <T extends ILootFunctionConsumer<?>> T build( T builder, List<ILootFunction.IBuilder> functions ) {
        for( ILootFunction.IBuilder function : functions ) builder.apply( function );
        return builder;
    }
}