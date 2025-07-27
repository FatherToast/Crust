package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A list of registry entries used to link one or more numbers to specific registry objects.
 * <br></br>
 * For entity types it is recommended to use {@link EntityList},
 * as it offers more conditions specific to entities.
 */
public class RegistryEntryValueList<T> implements IStringArray {
    
    /** The registry object-value entries in this list. */
    private final RegistryValueEntry<T>[] ENTRIES;
    /** The entity type tags in this list. */
    private final List<RegistryValueTagEntry<T>> TAG_ENTRIES = new ArrayList<>();
    /** The namespace entity-value entries in this list. */
    private final List<NamespaceRegistryEntry> NAMESPACE_ENTRIES = new ArrayList<>();
    /** The default entry for this list. */
    @Nullable
    final private DefaultValueEntry DEFAULT_ENTRY;
    /** The registry to check against. */
    private final Supplier<IForgeRegistry<T>> registrySupplier;
    
    /** The number of values each entry must have. If this is negative, then entries may have any non-zero number of values. */
    private int entryValues = -1;
    /** The minimum value accepted for entry values in this list. */
    private double minValue = Double.NEGATIVE_INFINITY;
    /** The maximum value accepted for entry values in this list. */
    private double maxValue = Double.POSITIVE_INFINITY;
    
    /**
     * Create a new registry entry value list from a list of entries with an optional default entry.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    @SuppressWarnings( "unchecked" )
    public RegistryEntryValueList( @Nullable DefaultValueEntry defaultEntry, Supplier<IForgeRegistry<T>> registrySupplier, List<RegistryValueEntry<T>> entries ) {
        this( defaultEntry, registrySupplier, entries.toArray( new RegistryValueEntry[0] ) );
    }
    
    /**
     * Create a new registry entry value list from an array of entries with an optional default entry. Used for creating default configs.
     * <p>
     * By default, these lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    @SafeVarargs
    public RegistryEntryValueList( @Nullable DefaultValueEntry defaultEntry, Supplier<IForgeRegistry<T>> registrySupplier, RegistryValueEntry<T>... entries ) {
        ENTRIES = entries;
        DEFAULT_ENTRY = defaultEntry;
        this.registrySupplier = registrySupplier;
    }
    
    
    /** Adds the given tag-entries to this list. Discards duplicates. */
    public RegistryEntryValueList<T> addTagEntries( List<RegistryValueTagEntry<T>> tagEntries ) {
        for( RegistryValueTagEntry<T> entry : tagEntries ) {
            TagKey<T> tagKey = entry.TAG;
            boolean exists = false;
            
            for( RegistryValueTagEntry<T> existingEntry : TAG_ENTRIES ) {
                if( existingEntry.TAG.location().equals( tagKey.location() ) ) {
                    exists = true;
                    break;
                }
            }
            if( !exists ) {
                TAG_ENTRIES.add( entry );
            }
        }
        return this;
    }
    
    /** Adds the given namespace-entries to this list. Discards duplicates. */
    public RegistryEntryValueList<T> addNamespaceEntries( List<NamespaceRegistryEntry> namespaceEntries ) {
        for( NamespaceRegistryEntry entry : namespaceEntries ) {
            String namespace = entry.NAMESPACE;
            boolean exists = false;
            
            for( NamespaceRegistryEntry existingEntry : NAMESPACE_ENTRIES ) {
                if( existingEntry.NAMESPACE.equals( namespace ) ) {
                    exists = true;
                    break;
                }
            }
            if( !exists )
                NAMESPACE_ENTRIES.add( entry );
        }
        return this;
    }
    
    /** @return The registry this list draws from. */
    public Supplier<IForgeRegistry<T>> getRegistry() { return registrySupplier; }
    
    /** @return The entries in this list, except tags and namespaces. */
    public Iterable<RegistryValueEntry<T>> getEntries() { return List.of( ENTRIES ); }
    
    /** @return A list of tag keys in this list. */
    public List<RegistryValueTagEntry<T>> getTags() { return Collections.unmodifiableList( TAG_ENTRIES ); }
    
    /** @return A list of specified namespaces in this list. */
    public List<NamespaceRegistryEntry> getNamespaces() { return Collections.unmodifiableList( NAMESPACE_ENTRIES ); }
    
    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral( toStringList().toArray() ); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( @Nullable Object other ) {
        if( !(other instanceof RegistryEntryValueList) ) return false;
        // Compare by the string list view of the object
        return toStringList().equals( ((RegistryEntryValueList<?>) other).toStringList() );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        final List<String> list = new ArrayList<>( ENTRIES.length );
        
        for( RegistryValueEntry<T> entry : ENTRIES ) {
            list.add( entry.toString() );
        }
        for( RegistryValueTagEntry<T> tagEntry : TAG_ENTRIES ) {
            list.add( tagEntry.toString() );
        }
        for( NamespaceRegistryEntry namespaceEntry : NAMESPACE_ENTRIES ) {
            list.add( namespaceEntry.toString() );
        }
        if( DEFAULT_ENTRY != null )
            list.add( DEFAULT_ENTRY.toString() );
        
        return list;
    }
    
    /** @return True if the object is contained in this list. Optionally checks tags. */
    public boolean contains( @Nullable T registryObject, @Nullable Predicate<TagKey<T>> tagCheck ) {
        if( registryObject == null ) return false;
        
        final RegistryValueEntry<T> targetEntry = new RegistryValueEntry<>( registrySupplier.get().getKey( registryObject ) );
        
        for( RegistryValueEntry<T> currentEntry : ENTRIES ) {
            if( currentEntry.contains( registrySupplier.get(), targetEntry ) )
                return true;
        }
        
        if( tagCheck != null ) {
            for( RegistryValueTagEntry<T> tagEntry : TAG_ENTRIES ) {
                if( tagCheck.test( tagEntry.TAG ) )
                    return true;
            }
        }
        
        for( NamespaceRegistryEntry namespaceEntry : NAMESPACE_ENTRIES ) {
            if( namespaceEntry.contains( targetEntry.REG_KEY.getNamespace() ) )
                return true;
        }
        return false;
    }
    
    /**
     * @param registryObject The registry object to retrieve values for.
     * @param tagCheck       Optional tag check logic.
     * @return The array of values of the best-match entry. Returns null if the object is not contained in this list.
     */
    @Nullable
    public double[] getValues( @Nullable T registryObject, @Nullable Predicate<TagKey<T>> tagCheck ) {
        if( registryObject == null ) return null;
        
        final RegistryValueEntry<T> targetEntry = new RegistryValueEntry<>( registrySupplier.get().getKey( registryObject ) );
        
        
        for( RegistryValueEntry<T> currentEntry : ENTRIES ) {
            if( currentEntry.contains( registrySupplier.get(), targetEntry ) )
                return currentEntry.VALUES;
        }
        // Check tag entries
        if( tagCheck != null ) {
            for( RegistryValueTagEntry<T> tagEntry : TAG_ENTRIES ) {
                if( tagCheck.test( tagEntry.TAG ) )
                    return tagEntry.VALUES;
            }
        }
        // Check namespace entries
        for( NamespaceRegistryEntry namespaceEntry : NAMESPACE_ENTRIES ) {
            if( namespaceEntry.contains( targetEntry.REG_KEY.getNamespace() ) )
                return namespaceEntry.VALUES;
        }
        // Return default values, if possible
        if( DEFAULT_ENTRY != null )
            return DEFAULT_ENTRY.VALUES;
        
        return null;
    }
    
    /**
     * @param registryObject The registry object to retrieve a value for.
     * @param tagCheck       Optional tag check logic.
     * @return The first value in the best-match entry's value array. Returns 0 if the object is not contained in this
     * list or has no values specified. This should only be used for 'single value' lists.
     * @see #setSingleValue()
     * @see #setSinglePercent()
     */
    public double getValue( @Nullable T registryObject, @Nullable Predicate<TagKey<T>> tagCheck ) {
        final double[] values = getValues( registryObject, tagCheck );
        return values == null || values.length < 1 ? 0.0 : values[0];
    }
    
    /**
     * @param registryObject The registry object to roll a value for.
     * @param tagCheck       Optional tag check logic.
     * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the object
     * is not contained in this list or has no values specified. This should only be used for 'single percent' lists.
     * @see #setSinglePercent()
     */
    public boolean rollChance( @Nullable T registryObject, RandomSource random, @Nullable Predicate<TagKey<T>> tagCheck ) {
        return ENTRIES.length > 0 && registryObject != null && random.nextDouble() < getValue( registryObject, tagCheck );
    }
    
    /** Marks this list as a simple percentage listing; exactly one percent (0 to 1) per entry. */
    public RegistryEntryValueList<T> setSinglePercent() { return setSingleValue().setRange0to1(); }
    
    /** Marks this list as identification only; no values will be linked to any entries. */
    public RegistryEntryValueList<T> setNoValues() { return setMultiValue( 0 ); }
    
    /** Marks this list as single-value; each entry will have exactly one value. */
    public RegistryEntryValueList<T> setSingleValue() { return setMultiValue( 1 ); }
    
    /** Marks this list as multi-value; each entry will have the specified number of values. */
    public RegistryEntryValueList<T> setMultiValue( int numberOfValues ) {
        entryValues = numberOfValues;
        return this;
    }
    
    /** Bounds entry values in this list between 0 and 1, inclusive. */
    public RegistryEntryValueList<T> setRange0to1() { return setRange( 0.0, 1.0 ); }
    
    /** Bounds entry values in this list to any positive value (>= +0). */
    public RegistryEntryValueList<T> setRangePos() { return setRange( 0.0, Double.POSITIVE_INFINITY ); }
    
    /** Bounds entry values in this list to the specified limits, inclusive. Note that 0 must be within the range. */
    public RegistryEntryValueList<T> setRange( double min, double max ) {
        minValue = min;
        maxValue = max;
        return this;
    }
    
    /**
     * @return The number of values that must be included in each entry.
     * A negative value implies any non-zero number of values is allowed.
     */
    public int getRequiredValues() { return entryValues; }
    
    /** @return The minimum value that can be given to entry values. */
    public double getMinValue() { return minValue; }
    
    /** @return The maximum value that can be given to entry values. */
    public double getMaxValue() { return maxValue; }
}