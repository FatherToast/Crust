package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.Block;
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
    /** The list used to write back to file. */
    protected final List<String> PRINT_LIST = new ArrayList<>();
    
    protected RegistryEntryList( IForgeRegistry<T> registry ) { REGISTRY = registry; }
    
    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * <p>
     * This method of creation can not take advantage of the * notation.
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
     * <p>
     * This method of creation can not take advantage of the * notation.
     */
    @SafeVarargs
    public RegistryEntryList( IForgeRegistry<T> registry, List<TagKey<T>> tags, T... entries ) {
        this( registry, entries );
        tags( tags );
    }
    
    /**
     * Create a new registry entry list from a list of registry key strings.
     */
    public RegistryEntryList( AbstractConfigField field, IForgeRegistry<T> registry, List<String> entries ) {
        this( registry );
        for( String line : entries ) {
            if ( line.startsWith( "#" ) ) {
                // Get substring after '#' and check if it passes as a valid resource location
                ResourceLocation tagLocation = ResourceLocation.tryParse( line.substring( 1 ) );

                // Not a valid resource location, outrageous
                if ( tagLocation == null ) {
                    ConfigUtil.LOG.warn( "Invalid tag key for {} \"{}\"! Skipping tag. Invalid tag key: {}",
                            field.getClass(), field.getKey(), line );
                }
                else {
                    tag( new TagKey<>( registry.getRegistryKey(), tagLocation ) );
                }
            }
            else if( line.endsWith( "*" ) ) {
                // Handle special case; add all entries in namespace
                if( !mergeFromNamespace( line.substring( 0, line.length() - 1 ) ) ) {
                    // Don't delete this kind of entry
                    ConfigUtil.LOG.warn( "Namespace entry for {} \"{}\" did not match anything! Questionable entry: {}",
                            field.getClass(), field.getKey(), line );
                }
                PRINT_LIST.add( line );
            }
            else {
                // Add a single registry entry
                final ResourceLocation regKey = new ResourceLocation( line );
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

        for ( TagKey<T> tagKey : TAGS ) {
            if ( tag.location().equals( tagKey.location() ) ) {
                exists = true;
                break;
            }
        }
        if ( !exists ) {
            TAGS.add( tag );
            PRINT_LIST.add( ConfigUtil.toString( tag ) );
        }
    }

    /** Adds the specified tag keys to this registry list. */
    public final void tags( Collection<TagKey<T>> tags ) {
        if ( tags.isEmpty() ) return;

        for ( TagKey<T> tag : tags )
            tag( tag );
    }
    
    /** @return The registry this list draws from. */
    public IForgeRegistry<T> getRegistry() { return REGISTRY; }
    
    /** @return The entries in this list, except tags. */
    public Set<T> getEntries() { return Collections.unmodifiableSet( UNDERLYING_SET ); }

    /** @return A list of tag keys in this list. */
    public List<TagKey<T>> getTags() { return Collections.unmodifiableList( TAGS ); }
    
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
    public boolean isEmpty() { return UNDERLYING_SET.isEmpty() && TAGS.isEmpty(); }
    
    /** @return Returns true if the entry is contained in this list. */
    public boolean contains( @Nullable T entry ) {
        return UNDERLYING_SET.contains( entry ); }

    /** @return Returns true if the entry is contained in this list, also checking against tags. */
    public boolean containsOrTag( @Nullable T entry, Predicate<TagKey<T>> tagPredicate ) {
        if (contains( entry )) return true;

        for ( TagKey<T> tagKey : TAGS ) {
            if ( tagPredicate.test(tagKey) ) return true;
        }
        return false;
    }
    
    /** @return Adds the registry entry if it exists and isn't already present, returns true if successful. */
    protected boolean mergeFrom( ResourceLocation regKey ) {
        final T entry = REGISTRY.getValue( regKey );
        return entry != null && UNDERLYING_SET.add( entry );
    }
    
    /**
     * @param namespace Merges all registry entries with keys that start with a namespace into this list.
     * @return True if any registry entries were actually added.
     */
    protected boolean mergeFromNamespace( String namespace ) {
        boolean foundAny = false;
        for( ResourceLocation regKey : REGISTRY.getKeys() ) {
            if( regKey.toString().startsWith( namespace ) ) {
                if( mergeFrom( regKey ) ) foundAny = true;
            }
        }
        return foundAny;
    }
}