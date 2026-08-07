package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareDoubleEnvironment;

import javax.annotation.Nullable;

public class YEnvironment extends CompareDoubleEnvironment {
    
    public YEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public YEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Double getActual( EnvironmentContext context ) { return context.getPos() == null ? null : context.getPos().y(); }
}