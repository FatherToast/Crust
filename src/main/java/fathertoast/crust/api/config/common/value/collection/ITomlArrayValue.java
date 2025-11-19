package fathertoast.crust.api.config.common.value.collection;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.ITomlValue;

import java.util.List;

/**
 * An object that can be serialized to TOML as an array.
 */
public interface ITomlArrayValue extends ITomlValue {
    
    /** @return A list of objects that will represent this value when written to a TOML file. */
    List<?> toTomlList();
    
    /** @return This value, converted to a single-line TOML literal. */
    @Override
    default String toTomlLiteral() { return TomlHelper.toArrayLiteral( toTomlList() ); }
    
    /** @return This value, converted to a single-line TOML literal. Used for writing comments and optionally for writing values. */
    @Override
    default String toTomlLiteral( boolean forComment ) {
        return TomlHelper.toArrayLiteral( toTomlList(), forComment );
    }
    
    /** Writes this value to file. */
    @Override
    default void write( CrustTomlWriter writer, CharacterOutput output ) { writer.writeArray( toTomlList(), output ); }
}