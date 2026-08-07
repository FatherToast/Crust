package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.OperationStats;
import fathertoast.crust.api.config.common.value.collection.value.OperatorValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over and apply attribute operations.
 *
 * @see RegObjKey
 * @see OperatorValue
 * @see OperationStats
 * @see fathertoast.crust.api.config.common.field.collection.AttributeOpListField
 * @see fathertoast.crust.api.config.common.value.ConfigDrivenAttributeSupplier
 */
@SuppressWarnings( "unused" )
public class AttributeOpList extends RegistryValueList<Attribute, OperationStats> {
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public AttributeOpList() { super( ForgeRegistries.ATTRIBUTES, OperationStats.CODEC ); }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AttributeOpList.Builder} is much easier.
     */
    @SafeVarargs
    public AttributeOpList( FuzzyEntry<Attribute, OperationStats>... keys ) {
        super( IRegWrapper.of( ForgeRegistries.ATTRIBUTES ), OperationStats.CODEC, keys );
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AttributeOpList.Builder} is much easier.
     */
    public AttributeOpList( Collection<FuzzyEntry<Attribute, OperationStats>> keys ) {
        super( IRegWrapper.of( ForgeRegistries.ATTRIBUTES ), OperationStats.CODEC, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public AttributeOpList makeNew() { return new AttributeOpList(); }
    
    /**
     * Applies all attribute operations in this list to the entity attribute builder.
     *
     * @return The builder, for convenience in building.
     */
    public AttributeSupplier.Builder apply( AttributeSupplier.Builder builder ) {
        for( Pair<Attribute, OperationStats> entry : entries() ) {
            if( entry == null ) continue;
            
            AttributeInstance attributeInstance = builder.builder.get( entry.key() );
            if( attributeInstance != null ) apply( attributeInstance, entry.value() );
        }
        return builder;
    }
    
    /** Applies all attribute operations in this list to the entity. */
    public void apply( LivingEntity entity ) {
        for( Pair<Attribute, OperationStats> entry : entries() ) {
            if( entry == null ) continue;
            
            AttributeInstance attributeInstance = entity.getAttribute( entry.key() );
            if( attributeInstance != null ) apply( attributeInstance, entry.value() );
        }
    }
    
    /** Applies an operation to an attribute instance. */
    private static void apply( AttributeInstance attributeInstance, OperationStats op ) {
        attributeInstance.setBaseValue( op.apply( attributeInstance.getBaseValue() ) );
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing attribute op lists smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<Attribute, OperationStats, AttributeOpList, B> {
        
        public final IRegWrapper<Attribute> registry = IRegWrapper.of( ForgeRegistries.ATTRIBUTES );
        
        public Builder() { super( OperationStats.CODEC ); }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public AttributeOpList build() { return new AttributeOpList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds an operation based on the attribute resource location. */
        public B put( String resLoc, OperationStats op ) { return put( RegObjKey.of( registry, resLoc, false ), op ); }
        
        /** Adds an operation based on the attribute resource location. */
        public B put( ResourceLocation resLoc, OperationStats op ) { return put( RegObjKey.of( registry, resLoc, false ), op ); }
        
        /** Adds an operation based on the attribute registry object. */
        public B put( RegistryObject<? extends Attribute> regObj, OperationStats op ) { return put( RegObjKey.of( registry, regObj, false ), op ); }
        
        /** Adds an operation based on the attribute resource key. */
        public B put( ResourceKey<? extends Attribute> resKey, OperationStats op ) { return put( RegObjKey.of( registry, resKey, false ), op ); }
        
        /** Adds an operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B put( Attribute attribute, OperationStats op ) { return put( RegObjKey.of( registry, attribute, false ), op ); }
        
        /** Adds an operation based on the attribute resource location. */
        public B put( String resLoc, OperatorValue op, double d ) { return put( resLoc, OperationStats.of( op, d ) ); }
        
        /** Adds an operation based on the attribute resource location. */
        public B put( ResourceLocation resLoc, OperatorValue op, double d ) { return put( resLoc, OperationStats.of( op, d ) ); }
        
        /** Adds an operation based on the attribute registry object. */
        public B put( RegistryObject<? extends Attribute> regObj, OperatorValue op, double d ) { return put( regObj, OperationStats.of( op, d ) ); }
        
        /** Adds an operation based on the attribute resource key. */
        public B put( ResourceKey<? extends Attribute> resKey, OperatorValue op, double d ) { return put( resKey, OperationStats.of( op, d ) ); }
        
        /** Adds an operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B put( Attribute attribute, OperatorValue op, double d ) { return put( attribute, OperationStats.of( op, d ) ); }
        
        
        // ---- Assign Op Keys ---- //
        
        /** Adds an 'assign' operation based on the attribute resource location. */
        public B putAssign( String resLoc, double d ) { return put( resLoc, OperationStats.assign( d ) ); }
        
        /** Adds an 'assign' operation based on the attribute resource location. */
        public B putAssign( ResourceLocation resLoc, double d ) { return put( resLoc, OperationStats.assign( d ) ); }
        
        /** Adds an 'assign' operation based on the attribute registry object. */
        public B putAssign( RegistryObject<? extends Attribute> regObj, double d ) { return put( regObj, OperationStats.assign( d ) ); }
        
        /** Adds an 'assign' operation based on the attribute resource key. */
        public B putAssign( ResourceKey<? extends Attribute> resKey, double d ) { return put( resKey, OperationStats.assign( d ) ); }
        
        /** Adds an 'assign' operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B putAssign( Attribute attribute, double d ) { return put( attribute, OperationStats.assign( d ) ); }
        
        
        // ---- Multiply Op Keys ---- //
        
        /** Adds a 'multiply' operation based on the attribute resource location. */
        public B putMultiply( String resLoc, double d ) { return put( resLoc, OperationStats.multiply( d ) ); }
        
        /** Adds a 'multiply' operation based on the attribute resource location. */
        public B putMultiply( ResourceLocation resLoc, double d ) { return put( resLoc, OperationStats.multiply( d ) ); }
        
        /** Adds a 'multiply' operation based on the attribute registry object. */
        public B putMultiply( RegistryObject<? extends Attribute> regObj, double d ) { return put( regObj, OperationStats.multiply( d ) ); }
        
        /** Adds a 'multiply' operation based on the attribute resource key. */
        public B putMultiply( ResourceKey<? extends Attribute> resKey, double d ) { return put( resKey, OperationStats.multiply( d ) ); }
        
        /** Adds a 'multiply' operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B putMultiply( Attribute attribute, double d ) { return put( attribute, OperationStats.multiply( d ) ); }
        
        
        // ---- Divide Op Keys ---- //
        
        /** Adds a 'divide' operation based on the attribute resource location. */
        public B putDivide( String resLoc, double d ) { return put( resLoc, OperationStats.divide( d ) ); }
        
        /** Adds a 'divide' operation based on the attribute resource location. */
        public B putDivide( ResourceLocation resLoc, double d ) { return put( resLoc, OperationStats.divide( d ) ); }
        
        /** Adds a 'divide' operation based on the attribute registry object. */
        public B putDivide( RegistryObject<? extends Attribute> regObj, double d ) { return put( regObj, OperationStats.divide( d ) ); }
        
        /** Adds a 'divide' operation based on the attribute resource key. */
        public B putDivide( ResourceKey<? extends Attribute> resKey, double d ) { return put( resKey, OperationStats.divide( d ) ); }
        
        /** Adds a 'divide' operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B putDivide( Attribute attribute, double d ) { return put( attribute, OperationStats.divide( d ) ); }
        
        
        // ---- Add Op Keys ---- //
        
        /** Adds an 'add' operation based on the attribute resource location. */
        public B putAdd( String resLoc, double d ) { return put( resLoc, OperationStats.add( d ) ); }
        
        /** Adds an 'add' operation based on the attribute resource location. */
        public B putAdd( ResourceLocation resLoc, double d ) { return put( resLoc, OperationStats.add( d ) ); }
        
        /** Adds an 'add' operation based on the attribute registry object. */
        public B putAdd( RegistryObject<? extends Attribute> regObj, double d ) { return put( regObj, OperationStats.add( d ) ); }
        
        /** Adds an 'add' operation based on the attribute resource key. */
        public B putAdd( ResourceKey<? extends Attribute> resKey, double d ) { return put( resKey, OperationStats.add( d ) ); }
        
        /** Adds an 'add' operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B putAdd( Attribute attribute, double d ) { return put( attribute, OperationStats.add( d ) ); }
        
        
        // ---- Subtract Op Keys ---- //
        
        /** Adds a 'subtract' operation based on the attribute resource location. */
        public B putSubtract( String resLoc, double d ) { return put( resLoc, OperationStats.subtract( d ) ); }
        
        /** Adds a 'subtract' operation based on the attribute resource location. */
        public B putSubtract( ResourceLocation resLoc, double d ) { return put( resLoc, OperationStats.subtract( d ) ); }
        
        /** Adds a 'subtract' operation based on the attribute registry object. */
        public B putSubtract( RegistryObject<? extends Attribute> regObj, double d ) { return put( regObj, OperationStats.subtract( d ) ); }
        
        /** Adds a 'subtract' operation based on the attribute resource key. */
        public B putSubtract( ResourceKey<? extends Attribute> resKey, double d ) { return put( resKey, OperationStats.subtract( d ) ); }
        
        /** Adds a 'subtract' operation based on the registered attribute object. Only suitable for vanilla stuff. */
        public B putSubtract( Attribute attribute, double d ) { return put( attribute, OperationStats.subtract( d ) ); }
    }
}