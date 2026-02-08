package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.file.TomlHelper;

/**
 * An object that is serialized to TOML as a string value.
 */
public interface ITomlStringValue extends ITomlValue {
    
    /** @return This value, converted to a single-line string. */
    String toTomlString();
    
    /** @return This value, converted to a single-line TOML literal. */
    @Override
    default String toTomlLiteral() { return TomlHelper.toBasicStringLiteral( toTomlString() ); }
    
    /**
     * @param forComment If true, particularly long values (e.g., many-element lists) should be truncated.
     * @return This value, converted to a single-line TOML literal.
     */
    @Override
    default String toTomlLiteral( boolean forComment ) {
        return TomlHelper.toBasicStringLiteral( toTomlString(), forComment );
    }
}