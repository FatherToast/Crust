package fathertoast.crust.api.config.common.value;

import java.util.List;

/**
 * An object that can be serialized to TOML as a string array.
 */
public interface IStringArray extends ITomlArrayValue {
    
    /** @return A list of strings that will represent this object when written to a TOML file. */
    List<String> toStringList();
    
    /** @return A list of objects that will represent this object when written to a TOML file. */
    default List<?> toTomlList() { return toStringList(); }
}