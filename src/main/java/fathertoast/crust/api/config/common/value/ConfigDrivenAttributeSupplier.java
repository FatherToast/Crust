package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.field.collection.AttributeOpListField;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A modified attribute supplier that is backed by a config field.
 * Changes to the config field will be immediately reflected in the attribute supplier.
 * <p>
 * Typically created using {@link AttributeOpListField#build(Builder)} during
 * {@link net.minecraftforge.event.entity.EntityAttributeCreationEvent}.
 */
public class ConfigDrivenAttributeSupplier extends AttributeSupplier {
    
    private final AttributeOpListField FIELD;
    private final Map<Attribute, AttributeInstance> BASE_ATTRIBUTES;
    
    private AttributeSupplier underlyingMap;
    
    public ConfigDrivenAttributeSupplier( AttributeOpListField field, AttributeSupplier.Builder builder ) {
        super( builder.builder );
        FIELD = field;
        BASE_ATTRIBUTES = builder.builder;
        field.linkedAttributeSupplier = this;
    }
    
    /** @return The field associated with this attribute supplier. */
    public AttributeOpListField getField() { return FIELD; }
    
    /** Called when the config field is loaded to force reload. */
    public void invalidate() { underlyingMap = null; }
    
    /** Called before any access to the underlying map to ensure it is loaded (nonnull). */
    private void validate() {
        if( underlyingMap != null ) return;
        
        // Create a deep clone of the base attribute map
        final AttributeSupplier.Builder builder = builder();
        for( Map.Entry<Attribute, AttributeInstance> entry : BASE_ATTRIBUTES.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue().getBaseValue() );
        }
        FIELD.apply( builder );
        underlyingMap = builder.build();
    }
    
    @Override
    public double getValue( Attribute attribute ) {
        validate();
        return underlyingMap.getValue( attribute );
    }
    
    @Override
    public double getBaseValue( Attribute attribute ) {
        validate();
        return underlyingMap.getBaseValue( attribute );
    }
    
    @Override
    public double getModifierValue( Attribute attribute, UUID uuid ) {
        validate();
        return underlyingMap.getModifierValue( attribute, uuid );
    }
    
    @Override
    @Nullable
    public AttributeInstance createInstance( Consumer<AttributeInstance> onChanged, Attribute attribute ) {
        validate();
        return underlyingMap.createInstance( onChanged, attribute );
    }
    
    @Override
    public boolean hasAttribute( Attribute attribute ) { return BASE_ATTRIBUTES.containsKey( attribute ); }
    
    @Override
    public boolean hasModifier( Attribute attribute, UUID uuid ) {
        validate();
        return underlyingMap.hasModifier( attribute, uuid );
    }
}