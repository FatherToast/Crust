package fathertoast.crust.api.lib.number;

/** Represents a type of number. */
public enum NumberType {
    BYTE( "Byte" ), SHORT( "Short" ),
    INT( "Integer" ), LONG( "Long" ),
    FLOAT( "Float" ), DOUBLE( "Double" );
    
    final String name;
    
    NumberType( String name ) {
        this.name = name;
    }
    
    /** @return The display name of the type. */
    public String getName() {
        return name;
    }
}
