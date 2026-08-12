package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.lib.number.NumberType;

/**
 * Represents a fuzzy collection that contains
 * {@link fathertoast.crust.api.config.common.value.collection.key.NumberKey number keys}.
 */
public interface INumberCollection {
    
    /** @return This number collection's number value type. */
    NumberType getNumberType();
}
