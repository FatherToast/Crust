package fathertoast.crust.api.config.common.value;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;

import java.util.List;

/**
 * An object that can be serialized to TOML as an array.
 */
public interface ITomlArrayValue extends ITomlValue {
    
    /** @return A list of objects that will represent this value when written to a TOML file. */
    List<?> toTomlList();
    
    /** @return This value, converted to a single-line TOML literal. */
    default String toTomlLiteral() { return toTomlLiteral( false ); }
    
    /** @return This value, converted to a single-line TOML literal. Used for writing comments and optionally for writing values. */
    default String toTomlLiteral( boolean forComment ) {
        return TomlHelper.toArrayLiteral( toTomlList(), forComment );
    }
    
    /** Writes this value to file. */
    default void write( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeArray( toTomlList(), output );
    }
}