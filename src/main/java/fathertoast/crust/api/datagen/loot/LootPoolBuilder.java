package fathertoast.crust.api.datagen.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for a single loot pool, which can be made part of a loot table.
 * <p>
 * Each loot pool is a collection of loot entries combined with the conditions for its generation and functions to
 * perform on the loot entries, along with the number of entries to generate.
 *
 * @see LootTableBuilder
 * @see LootEntryItemBuilder
 */
@SuppressWarnings( { "unused", "WeakerAccess", "UnusedReturnValue" } )
public class LootPoolBuilder {
    
    private final String name;
    
    private NumberProvider rolls = LootHelper.ONE_ROLL;
    private NumberProvider bonusRolls = LootHelper.NO_ROLL;
    
    private final List<LootPoolEntryContainer.Builder<?>> lootEntries = new ArrayList<>();
    private final List<LootItemCondition.Builder> poolConditions = new ArrayList<>();
    private final List<LootItemFunction.Builder> poolFunctions = new ArrayList<>();
    
    /** Creates a loot pool with the given unique name. */
    public LootPoolBuilder( String id ) { name = id; }
    
    /** @return A new loot pool object reflecting the current state of this builder. */
    public LootPool.Builder toLootPool() {
        return LootHelper.build( LootPool.lootPool(), lootEntries, poolConditions, poolFunctions )
                .setRolls( rolls ).setBonusRolls( bonusRolls ).name( name );
    }
    
    /** @param value The number of rolls for this pool. */
    public LootPoolBuilder setRolls( float value ) {
        rolls = ConstantValue.exactly( value );
        return this;
    }
    
    /**
     * @param min Minimum rolls for this pool (inclusive).
     * @param max Maximum rolls for this pool (inclusive).
     */
    public LootPoolBuilder setRolls( float min, float max ) {
        rolls = UniformGenerator.between( min, max );
        return this;
    }
    
    /** @param value The additional rolls for each level of looting. */
    public LootPoolBuilder setBonusRolls( float value ) {
        bonusRolls = ConstantValue.exactly( value );
        return this;
    }
    
    /**
     * @param min Minimum additional rolls for this pool for each level of looting (inclusive).
     * @param max Maximum additional rolls for this pool for each level of looting (inclusive).
     */
    public LootPoolBuilder setBonusRolls( float min, float max ) {
        bonusRolls = UniformGenerator.between( min, max );
        return this;
    }
    
    /** @param entry A loot entry to add to this builder. */
    public LootPoolBuilder addEntry( LootPoolEntryContainer.Builder<?> entry ) {
        lootEntries.add( entry );
        return this;
    }
    
    /** @param conditions Any number of conditions to add to this builder. */
    public LootPoolBuilder addConditions( LootItemCondition.Builder... conditions ) {
        poolConditions.addAll( Arrays.asList( conditions ) );
        return this;
    }
    
    /** @param functions Any number of functions to add to this builder. */
    public LootPoolBuilder addFunctions( LootItemFunction.Builder... functions ) {
        poolFunctions.addAll( Arrays.asList( functions ) );
        return this;
    }
    
    /** Adds a loot table as a drop. */
    public LootPoolBuilder addEntryTable( ResourceLocation lootTable ) {
        return addEntryTable( lootTable, 1, 0 );
    }
    
    /** Adds a loot table as a drop. */
    public LootPoolBuilder addEntryTable( ResourceLocation lootTable, int weight, int quality, LootItemCondition.Builder... conditions ) {
        final LootPoolSingletonContainer.Builder<?> builder = LootTableReference.lootTableReference( lootTable );
        // Note: we can also extend this to allow functions, if needed
        for( LootItemCondition.Builder condition : conditions ) builder.when( condition );
        return addEntry( builder.setWeight( weight ).setQuality( quality ) );
    }
    
    /** Adds an empty entry. */
    public LootPoolBuilder addEntryEmpty( int weight, int quality, LootItemCondition.Builder... conditions ) {
        LootPoolSingletonContainer.Builder<?> builder = EmptyLootItem.emptyItem();
        for( LootItemCondition.Builder condition : conditions ) builder.when( condition );
        return addEntry( builder.setWeight( weight ).setQuality( quality ) );
    }
}