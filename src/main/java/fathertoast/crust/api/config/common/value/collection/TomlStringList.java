package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;


/**
 * A list of values represented in files by a string array.
 * <p>
 * Extending classes should typically provide a no-args constructor to support load operations,
 * as well as a var-args constructor to simplify default value creation.
 */
@ApiStatus.Experimental
public abstract class TomlStringList<T extends ITomlStringValue> implements IStringArrayValue {
    
    private List<T> underlyingList;
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    protected TomlStringList() { underlyingList = Collections.emptyList(); }
    
    /** Constructs a list containing the elements provided. Use this for creating default values during config definition. */
    @SafeVarargs
    protected TomlStringList( T... a ) { this( Arrays.asList( a ) ); }
    
    /** Constructs a list containing the elements provided. Use this for creating default values during config definition. */
    protected TomlStringList( Collection<? extends T> c ) { setList( c ); }
    
    
    /**
     * Loads the list value. If anything goes wrong, correct it at the lowest level possible and
     * provide useful feedback, identifying the config field if present.
     *
     * @param field The config field we are loading for, or null if not loading from a config.
     * @param value List value to load from. This generally comes from a TOML string array value
     *              (config loading) or a string list tag (NBT loading).
     */
    public abstract void load( @Nullable AbstractConfigField field, List<String> value );
    
    /** Call this to set the list value during {@link #load(AbstractConfigField, List)}. */
    protected final void setList( Collection<? extends T> newList ) { underlyingList = List.copyOf( newList ); }
    
    
    /** Convenience method to load this value from NBT. */
    public void load( CompoundTag tag, String name ) { load( null, NBTHelper.getStringList( tag, name ) ); }
    
    /** Convenience method to write this value to NBT. */
    public void write( CompoundTag tag, String name ) { NBTHelper.putStringList( tag, name, toStringList() ); }
    
    
    /** @return An unmodifiable list of objects that represent this object's value when written to file. */
    public List<T> getList() { return underlyingList; }
    
    /** @return The number of elements. */
    public int size() { return getList().size(); }
    
    /** @return True if this contains no elements. */
    public boolean isEmpty() { return getList().isEmpty(); }
    
    
    /** @return A list of strings that represent this object's value when written to file. */
    @Override // IStringArrayValue
    public List<String> toStringList() {
        final List<T> list = getList();
        final List<String> strings = new ArrayList<>( list.size() );
        for( T e : list ) strings.add( e.toTomlString() );
        return strings;
    }
    
    /** @return A list of objects that will represent this object when written to a TOML file. */
    @Override // ITomlArrayValue
    public List<T> toTomlList() { return getList(); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( @Nullable Object other ) {
        if( !(other instanceof TomlStringList) ) return false;
        // Compare by the string list view of the object
        return toStringList().equals( ((TomlStringList<?>) other).toStringList() );
    }
}