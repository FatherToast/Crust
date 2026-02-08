package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.value.collection.IStringArrayValue;

import java.util.List;

/**
 * An object that can be serialized to TOML as a string array.
 *
 * @see IStringArrayValue
 */
@Deprecated( forRemoval = true ) // Use new lists impl, see above
public interface IStringArray extends IStringArrayValue {
    
    /** @return A list of objects that will represent this object when written to a TOML file. */
    @Override
    default List<?> toTomlList() { return toStringList(); }
}