package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A list of block entries used to match specific block states.
 */
@SuppressWarnings( "unused" )

@Deprecated( forRemoval = true )
public class BlockList implements IStringArray {
    
    /** The block-value entries in this list. */
    private final Map<Block, BlockEntry> UNDERLYING_MAP = new HashMap<>();
    /** The block tags in this list. */
    private final List<TagKey<Block>> TAGS = new ArrayList<>();
    /** The namespace entries in this list */
    private final List<String> NAMESPACES = new ArrayList<>();
    /**
     * The list used to write block entries back to file (tag and namespace entries are handled separately).
     * Consists of cloned single-state block entries.
     */
    private final List<BlockEntry> PRINT_LIST = new ArrayList<>();
    
    /**
     * Create a new block list from an array of entries. Used for creating default configs.
     * <p>
     * This method of block list creation can not take advantage of the * notation.
     */
    public BlockList( BlockEntry... entries ) {
        for( BlockEntry entry : entries ) {
            mergeFrom( entry );
        }
    }
    
    /**
     * Create a new block list from an array of entries. Used for creating default configs.
     * Also allows adding tags.
     * <p>
     * This method of block list creation can not take advantage of the * notation.
     */
    public BlockList( @Nullable List<String> namespaces, @Nullable List<TagKey<Block>> tags, BlockEntry... entries ) {
        this( entries );
        
        if( tags != null )
            tags( tags );
        
        if( namespaces != null ) {
            namespaces( namespaces );
        }
    }
    
    /**
     * Create a new block list from a list of block state strings.
     */
    public BlockList( AbstractConfigField field, List<String> entries ) {
        for( String line : entries ) {
            if( line.startsWith( "#" ) ) {
                // Get substring after '#' and check if it passes as a valid resource location
                ResourceLocation tagLocation = ResourceLocation.tryParse( line.substring( 1 ) );
                
                // Not a valid resource location, outrageous
                if( tagLocation == null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Block entry has invalid tag key! Skipping. Entry: {}", line );
                }
                else {
                    tag( BlockTags.create( tagLocation ) );
                }
            }
            else if( line.endsWith( "*" ) ) {
                String[] parts = line.split( ":" );
                
                if( parts[0].isEmpty() ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Block entry has invalid namespace! Skipping. Entry: {}", line );
                }
                else {
                    namespace( parts[0] );
                }
            }
            else {
                // Add a single block entry
                BlockEntry entry = new BlockEntry( field, line );
                if( entry.BLOCK == Blocks.AIR ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Block entry is invalid! Deleting. Invalid entry: {}", line );
                }
                else {
                    mergeFrom( entry );
                }
            }
        }
    }
    
    /** Adds the specified tag key to this BlockList, unless it already exists in the list. */
    public final BlockList tag( TagKey<Block> tag ) {
        boolean exists = false;
        
        for( TagKey<Block> tagKey : TAGS ) {
            if( tag.location().equals( tagKey.location() ) ) {
                exists = true;
                break;
            }
        }
        if( !exists ) {
            TAGS.add( tag );
        }
        return this;
    }
    
    /** Adds the specified tag keys to this BlockList. */
    public final void tags( Collection<TagKey<Block>> tags ) {
        if( tags.isEmpty() ) return;
        
        for( TagKey<Block> tag : tags )
            tag( tag );
    }
    
    /** Adds the specified namespace entry to this BlockList, unless it already exists in the list. */
    public final BlockList namespace( String namespace ) {
        boolean exists = false;
        
        for( String s : NAMESPACES ) {
            if( s.equals( namespace ) ) {
                exists = true;
                break;
            }
        }
        if( !exists ) {
            NAMESPACES.add( namespace );
        }
        return this;
    }
    
    /** Adds the specified namespace entries to this BlockList. */
    public final void namespaces( Collection<String> namespaces ) {
        if( namespaces.isEmpty() ) return;
        
        for( String namespace : namespaces )
            namespace( namespace );
    }
    
    /** @return A string representation of this object. */
    @Override
    public String toString() { return toTomlLiteral(); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( @Nullable Object other ) {
        if( !(other instanceof BlockList) ) return false;
        // Compare by the string list view of the object
        return toStringList().equals( ((BlockList) other).toStringList() );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        List<String> list = new ArrayList<>( PRINT_LIST.size() );
        for( BlockEntry entry : PRINT_LIST ) {
            list.add( entry.toString() );
        }
        for( TagKey<Block> tagKey : TAGS ) {
            list.add( ConfigUtil.toString( tagKey ) );
        }
        for( String namespace : NAMESPACES ) {
            list.add( ConfigUtil.namespaceWildcard( namespace ) );
        }
        return list;
    }
    
    /** @return Returns true if there are no entries in this block list, including tag and namespace entries. */
    public boolean isEmpty() {
        return UNDERLYING_MAP.isEmpty() && TAGS.isEmpty() && NAMESPACES.isEmpty();
    }
    
    /** @return Returns true if the block is contained in this list. Prioritizes unique entries over tags. */
    public boolean matches( BlockState blockState ) {
        // Regular entries
        BlockEntry entry = UNDERLYING_MAP.get( blockState.getBlock() );
        if( entry != null ) return entry.matches( blockState );
        
        // Check tags
        for( TagKey<Block> tagKey : TAGS ) {
            if( blockState.is( tagKey ) )
                return true;
        }
        String namespace = ForgeRegistries.BLOCKS.getKey( blockState.getBlock() ).getNamespace();
        
        // Check namespace wildcards
        for( String ns : NAMESPACES ) {
            if( namespace.equals( ns ) )
                return true;
        }
        return false;
    }
    
    /** @param otherEntry Merges all matching from a block entry into this list. */
    private void mergeFrom( BlockEntry otherEntry ) {
        PRINT_LIST.add( otherEntry );
        BlockEntry currentEntry = UNDERLYING_MAP.get( otherEntry.BLOCK );
        
        if( currentEntry == null ) {
            UNDERLYING_MAP.put( otherEntry.BLOCK, otherEntry );
        }
        else {
            currentEntry.mergeFrom( otherEntry );
        }
    }
}