package fathertoast.crust.api.config.common.value;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;

import java.util.List;

/**
 * An object that can be serialized to TOML as a string array.
 */
public interface IStringArray extends ITomlValue {
    
    /** @return A list of strings that will represent this object when written to a TOML file. */
    List<String> toStringList();
    
    /** @return This value, converted to a single-line TOML literal. */
    default String toTomlLiteral() { return toTomlLiteral( false ); }
    
    /** @return This value, converted to a single-line TOML literal. Used for writing comments and optionally for writing values. */
    default String toTomlLiteral( boolean forComment ) {
        return TomlHelper.toArrayLiteral( toStringList(), forComment );
    }
    
    /** Writes this value to file. */
    default void write( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeArray( toStringList(), output );
    }
}