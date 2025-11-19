package fathertoast.crust.api.config.common.value.collection;

import java.util.List;

/**
 * An object that can be serialized to TOML as a string array.
 */
public interface IStringArrayValue extends ITomlArrayValue {
    
    /** @return A list of strings that represent this object's value when written to file. */
    List<String> toStringList();
}