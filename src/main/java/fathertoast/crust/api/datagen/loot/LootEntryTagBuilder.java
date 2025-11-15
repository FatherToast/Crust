package fathertoast.crust.api.datagen.loot;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the same utilities as {@link LootEntryItemBuilder},
 * except this entry picks a random item from an item tag.
 */
public class LootEntryTagBuilder {
    
    /**
     * Predicate for 'entity on fire'. Copied from the not-easily-accessible field
     * {@link net.minecraft.data.loot.EntityLootSubProvider#ENTITY_ON_FIRE}.
     */
    public static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity()
            .flags( EntityFlagsPredicate.Builder.flags().setOnFire( true ).build() );
    
    private final TagKey<Item> TAG;
    
    private int weight = 1;
    private int quality = 0;
    
    private final List<LootItemFunction.Builder> itemFunctions = new ArrayList<>();
    private final List<LootItemCondition.Builder> entryConditions = new ArrayList<>();
    
    /** Creates a new loot entry builder based on an item tag. */
    public LootEntryTagBuilder( TagKey<Item> tag ) { TAG = tag; }
    
    
    /** @return A new loot entry object reflecting the current state of this builder. */
    public LootPoolSingletonContainer.Builder<?> toLootEntry() {
        return LootHelper.build( TagEntry.expandTag( TAG ), entryConditions, itemFunctions )
                .setWeight( weight ).setQuality( quality );
    }
    
    /** @param value A new weight for the loot entry. */
    public LootEntryTagBuilder setWeight( int value ) {
        weight = value;
        return this;
    }
    
    /** @param value A new quality for the loot entry. Quality alters the weight of this entry based on luck level. */
    public LootEntryTagBuilder setQuality( int value ) {
        quality = value;
        return this;
    }
    
    /** @param condition A condition to add to this builder. */
    public LootEntryTagBuilder addCondition( LootItemCondition.Builder condition ) {
        entryConditions.add( condition );
        return this;
    }
    
    /** Adds a stack size function. */
    public LootEntryTagBuilder setCount( int value ) {
        return addFunction( SetItemCountFunction.setCount( ConstantValue.exactly( value ) ) );
    }
    
    /** Adds a stack size function. */
    public LootEntryTagBuilder setCount( int min, int max ) {
        return addFunction( SetItemCountFunction.setCount( UniformGenerator.between( min, max ) ) );
    }
    
    /** Adds a looting enchant (luck) bonus function. Gross. */
    public LootEntryTagBuilder addLootingBonus( float value ) {
        return addFunction( LootingEnchantFunction.lootingMultiplier( ConstantValue.exactly( value ) ) );
    }
    
    /** Adds a looting enchant (luck) bonus function. Gross. */
    public LootEntryTagBuilder addLootingBonus( float min, float max ) {
        return addFunction( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( min, max ) ) );
    }
    
    /** Adds a looting enchant (luck) bonus function. Gross. */
    public LootEntryTagBuilder addLootingBonus( float min, float max, int limit ) {
        return addFunction( LootingEnchantFunction.lootingMultiplier( UniformGenerator.between( min, max ) )
                .setLimit( limit ) );
    }
    
    /** Adds a set damage function. */
    public LootEntryTagBuilder setDamage( int value ) {
        return addFunction( SetItemDamageFunction.setDamage( ConstantValue.exactly( value ) ) );
    }
    
    /** Adds a set damage function. */
    public LootEntryTagBuilder setDamage( int min, int max ) {
        return addFunction( SetItemDamageFunction.setDamage( UniformGenerator.between( min, max ) ) );
    }
    
    /** Adds an NBT tag compound function. */
    public LootEntryTagBuilder setNBTTag( CompoundTag tag ) { return addFunction( SetNbtFunction.setTag( tag ) ); }
    
    /** Adds a smelt function with the EntityOnFire condition. */
    public LootEntryTagBuilder smeltIfBurning() {
        return addFunction( SmeltItemFunction.smelted().when( LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.THIS, ENTITY_ON_FIRE ) ) );
    }
    
    /** Adds a random enchantment function. */
    public LootEntryTagBuilder applyOneRandomApplicableEnchant() {
        return addFunction( EnchantRandomlyFunction.randomApplicableEnchantment() );
    }
    
    /** Adds a random enchantment function. */
    public LootEntryTagBuilder applyOneRandomEnchant( Enchantment... enchantments ) {
        final EnchantRandomlyFunction.Builder builder = new EnchantRandomlyFunction.Builder();
        for( Enchantment enchant : enchantments ) builder.withEnchantment( enchant );
        return addFunction( builder );
    }
    
    /** Adds an enchanting function. */
    public LootEntryTagBuilder enchant( int level, boolean treasure ) {
        final EnchantWithLevelsFunction.Builder builder = EnchantWithLevelsFunction.enchantWithLevels( ConstantValue.exactly( level ) );
        if( treasure ) builder.allowTreasure();
        return addFunction( builder );
    }
    
    /** Adds an enchanting function. */
    public LootEntryTagBuilder enchant( int levelMin, int levelMax, boolean treasure ) {
        final EnchantWithLevelsFunction.Builder builder = EnchantWithLevelsFunction.enchantWithLevels( UniformGenerator.between( levelMin, levelMax ) );
        if( treasure ) builder.allowTreasure();
        return addFunction( builder );
    }
    
    /** Adds an item function to this builder. */
    public LootEntryTagBuilder addFunction( LootItemFunction.Builder function ) {
        itemFunctions.add( function );
        return this;
    }
}
