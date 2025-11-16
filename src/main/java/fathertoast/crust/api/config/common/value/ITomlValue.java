package fathertoast.crust.api.config.common.value;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;

/**
 * An object that can be serialized to TOML. Implementing this interface allows direct control
 * over how the object is written in TOML files without overriding {@link Object#toString()}.
 * <p>
 * Overriding #toString() works fine in many cases, but this interface must be used if you
 * cannot have #toString() return the TOML literal or would like to write a multi-line value.
 */
public interface ITomlValue {
    
    /** @return This value, converted to a single-line TOML literal. */
    String toTomlLiteral();
    
    /**
     * @param forComment If true, particularly long values (e.g., many-element lists) should be truncated.
     * @return This value, converted to a single-line TOML literal.
     */
    default String toTomlLiteral( boolean forComment ) { return toTomlLiteral(); }
    
    /** Writes this value to file. */
    default void write( CrustTomlWriter writer, CharacterOutput output ) {
        // Default impl writes a single-line value, override this for lists you want to print on multiple lines
        writer.writeLine( toTomlLiteral( false ), output );
    }
}