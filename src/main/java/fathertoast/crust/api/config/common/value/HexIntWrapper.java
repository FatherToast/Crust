package fathertoast.crust.api.config.common.value;

import java.util.Locale;

public record HexIntWrapper(Integer wrappedInt, int minDigits) implements ITomlIntValue {
    
    /** @return This value, converted to a single-line TOML literal. */
    @Override
    public String toTomlLiteral() {
        String hex = Integer.toHexString( get() ).toUpperCase( Locale.ROOT );
        if( minDigits > hex.length() ) {
            StringBuilder padding = new StringBuilder();
            for( int i = minDigits - hex.length(); i-- > 0; ) padding.append( '0' );
            hex = padding + hex;
        }
        return "0x" + hex;
    }
    
    /** @return The wrapped integer value. */
    @Override
    public Integer get() { return wrappedInt; }
}