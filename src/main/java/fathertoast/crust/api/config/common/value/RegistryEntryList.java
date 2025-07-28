package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * A list of entries used to match registry entries.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@SuppressWarnings( "unused" )
public class RegistryEntryList<T> implements IStringArray {
    
    /** The registry this list acts as a subset of. */
    private final IForgeRegistry<T> REGISTRY;
    
    /** The entries in this list. */
    protected final Set<T> UNDERLYING_SET = new HashSet<>();
    /** The tags in this list. */
    private final List<TagKey<T>> TAGS = new ArrayList<>();
    /** Entire namespaces specified in this list. */
    private final List<String> NAMESPACES = new ArrayList<>();
    /** The list used to write back to file. */
    protected final List<String> PRINT_LIST = new ArrayList<>();
    
    
    protected RegistryEntryList( IForgeRegistry<T> registry ) { REGISTRY = registry; }
    
    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     */
    @SafeVarargs
    public RegistryEntryList( IForgeRegistry<T> registry, T... entries ) {
        this( registry );
        for( T entry : entries ) {
            if( UNDERLYING_SET.add( entry ) ) PRINT_LIST.add( ConfigUtil.toString( registry.getKey( entry ) ) );
        }
    }
    
    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * Also allows adding tags.
     */
    @SafeVarargs
    public RegistryEntryList( IForgeRegistry<T> registry, @Nullable List<String> namespaces, @Nullable List<TagKey<T>> tags, T... entries ) {
        this( registry, entries );
        
        if( tags != null && !tags.isEmpty() ) {
            for( TagKey<T> tag : tags ) tag( tag ); // tag tags tag tag
        }
        
        if( namespaces != null && !namespaces.isEmpty() ) {
            for( String namespace : namespaces ) namespace( namespace ); // namespace namespaces namespace namespace
        }
    }
    
    /**
     * Create a new registry entry list from a list of registry key strings.
     */
    public RegistryEntryList( AbstractConfigField field, IForgeRegistry<T> registry, List<String> entries ) {
        this( registry );
        for( String line : entries ) {
            if( line.startsWith( "#" ) ) {
                // Get substring after '#' and check if it passes as a valid resource location
                ResourceLocation tagLocation = ResourceLocation.tryParse( line.substring( 1 ) );
                
                // Not a valid resource location, outrageous
                if( tagLocation == null ) {
                    ConfigUtil.LOG.warn( "Invalid tag key for {} \"{}\"! Skipping tag. Invalid tag key: {}",
                            field.getClass(), field.getKey(), line );
                }
                else {
                    tag( new TagKey<>( registry.getRegistryKey(), tagLocation ) );
                }
            }
            else if( line.endsWith( "*" ) ) {
                String[] parts = line.split( ":" );
                
                if( parts[0].isEmpty() ) {
                    ConfigUtil.LOG.warn( "Invalid namespace entry for {} \"{}\"! Skipping. Invalid namespace entry: {}",
                            field.getClass(), field.getKey(), line );
                }
                else {
                    namespace( parts[0] );
                }
            }
            else {
                // Add a single registry entry
                final ResourceLocation regKey = ResourceLocation.parse( line );
                if( mergeFrom( regKey ) ) {
                    PRINT_LIST.add( regKey.toString() );
                }
                else {
                    ConfigUtil.LOG.warn( "Invalid or duplicate entry for {} \"{}\"! Deleting entry. Invalid entry: {}",
                            field.getClass(), field.getKey(), line );
                }
            }
        }
    }
    
    /** Adds the specified tag key to this registry list, unless it already exists in the list. */
    public final void tag( TagKey<T> tag ) {
        boolean exists = false;
        
        for( TagKey<T> tagKey : TAGS ) {
            if( tag.location().equals( tagKey.location() ) ) {
                exists = true;
                break;
            }
        }
        if( !exists ) {
            TAGS.add( tag );
            PRINT_LIST.add( ConfigUtil.toString( tag ) );
        }
    }
    
    /** Adds the specified namespace to the list, unless it already exists in the list. */
    public final void namespace( String namespace ) {
        boolean exists = false;
        
        for( String s : NAMESPACES ) {
            if( s.equals( namespace ) ) {
                exists = true;
                break;
            }
        }
        if( !exists ) {
            NAMESPACES.add( ConfigUtil.namespaceWildcard( namespace ) );
        }
    }
    
    /** @return The registry this list draws from. */
    public IForgeRegistry<T> getRegistry() { return REGISTRY; }
    
    /** @return The entries in this list, except tags and namespaces. */
    public Set<T> getEntries() { return Collections.unmodifiableSet( UNDERLYING_SET ); }
    
    /** @return A list of tag keys in this list. */
    public List<TagKey<T>> getTags() { return Collections.unmodifiableList( TAGS ); }
    
    /** @return A list of specified namespaces in this list. */
    public List<String> getNamespaces() { return Collections.unmodifiableList( NAMESPACES ); }
    
    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral( PRINT_LIST.toArray() ); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( @Nullable Object other ) {
        if( !(other instanceof RegistryEntryList) ) return false;
        // Compare by the registries used and string list view of the object
        return getRegistry() == ((RegistryEntryList<?>) other).getRegistry() &&
                toStringList().equals( ((RegistryEntryList<?>) other).toStringList() );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() { return PRINT_LIST; }
    
    /** @return Returns true if there are no entries in this list. */
    public boolean isEmpty() { return UNDERLYING_SET.isEmpty() && TAGS.isEmpty() && NAMESPACES.isEmpty(); }
    
    /**
     * @return Returns true if the entry is contained in this list.<br></br>
     * Use {@link RegistryEntryList#containsOrTag(Object, Predicate)} to check against tags as well.
     */
    public boolean contains( @Nullable T entry ) {
        if( UNDERLYING_SET.contains( entry ) )
            return true;
        
        ResourceLocation regKey = REGISTRY.getKey( entry );
        if( regKey != null ) {
            for( String namespace : NAMESPACES ) {
                if( namespace.equals( regKey.getNamespace() ) )
                    return true;
            }
        }
        return false;
    }
    
    /** @return Returns true if the entry is contained in this list, also checking against tags. */
    public boolean containsOrTag( @Nullable T entry, Predicate<TagKey<T>> tagPredicate ) {
        if( contains( entry ) ) return true;
        
        for( TagKey<T> tagKey : TAGS ) {
            if( tagPredicate.test( tagKey ) ) return true;
        }
        return false;
    }
    
    /** @return Adds the registry entry if it exists and isn't already present, returns true if successful. */
    protected boolean mergeFrom( ResourceLocation regKey ) {
        final T entry = REGISTRY.getValue( regKey );
        return entry != null && UNDERLYING_SET.add( entry );
    }
}