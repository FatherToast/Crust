package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.ItemViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.ItemViewWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a config field with a block state value,
 * handled internally as a {@link BlockStateKey.Basic} key.
 */
@SuppressWarnings( "unused" )
@ApiStatus.Experimental
public class BlockStateField extends GenericField<BlockStateKey.Basic> {
    
    /** The default block's resource location. */
    protected final ResourceLocation blockResLocDefault;
    /** The properties defining the default block state. */
    protected final BlockStatePropertyMap blockStatePropsDefault;
    
    /** The actual default block state key, once it is loaded. */
    protected BlockStateKey.Basic actualDefault;
    
    /** The default block's resource location. */
    protected ResourceLocation blockResLoc;
    /** The properties defining the default block state. */
    protected BlockStatePropertyMap blockStateProps;
    
    
    /** @return A non-blacklist, basic block state key of the given block ID and properties. */
    public static BlockStateKey.Basic of( ResourceLocation blockResLoc, BlockStatePropertyMap blockStateProps ) {
        return BlockStateKey.of( blockResLoc, blockStateProps, false );
    }
    
    /** @return A non-blacklist, basic block state key of the given block state. */
    public static BlockStateKey.Basic of( BlockState state ) {
        return BlockStateKey.of( state, false );
    }
    
    /**
     * @return A non-blacklist, basic block state key containing
     * {@link Blocks#AIR}'s default block state as its value.
     */
    public static BlockStateKey.Basic airKey() {
        return of( Blocks.AIR.defaultBlockState() );
    }
    
    
    /** Creates a new field using a string-described block state. */
    public BlockStateField( String key, String defaultValue, @Nullable String... description ) {
        super( key, BlockStateKey.of( defaultValue, false ), description );
        final String[] split = BlockStatePropertyMap.split( defaultValue );
        blockResLocDefault = ResourceLocation.parse( split[0] );
        blockStatePropsDefault = BlockStatePropertyMap.of( split[1] );
        actualDefault = BlockStateKey.of( defaultValue, false );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceLocation defaultResLoc, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        super( key, BlockStateKey.of( defaultResLoc, defaultProperties, false ), description );
        blockResLocDefault = defaultResLoc;
        blockStatePropsDefault = defaultProperties;
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, RegistryObject<? extends Block> defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        // noinspection DataFlowIssue
        this( key, defaultBlock.getId(), defaultProperties, description );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceKey<? extends Block> defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, defaultBlock.location(), defaultProperties, description );
    }
    
    /**
     * Creates a new field using a block and block state properties. This only works for vanilla
     * blocks unless you hold off config initialization until after the blocks registry is populated.
     */
    public BlockStateField( String key, Block defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( defaultBlock ) ), defaultProperties, description );
    }
    
    /**
     * Creates a new field using a pre-existing block state. This only works for vanilla blocks
     * unless you hold off config initialization until after the blocks registry is populated.
     */
    public BlockStateField( String key, BlockState defaultValue, @Nullable String... description ) {
        super( key, of( defaultValue ), description );
        blockResLocDefault = Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( defaultValue.getBlock() ) );
        blockStatePropsDefault = BlockStatePropertyMap.of( defaultValue );
        actualDefault = of( defaultValue );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Block State", blockResLocDefault +
                blockStatePropsDefault.toString(), "\"namespace:path[property1=value1,property2=value2,...]\"" ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = getDefaultValue();
            blockResLoc = blockResLocDefault;
            blockStateProps = blockStatePropsDefault;
            return;
        }
        
        final String[] split = BlockStatePropertyMap.split( raw.toString() );
        blockResLoc = ResourceLocation.tryParse( split[0] );
        if( blockResLoc == null ) {
            // Invalid resource location
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid block state! Must follow the pattern \"namespace:path[property1=value1,property2=value2,...]\". Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            value = getDefaultValue();
            blockResLoc = blockResLocDefault;
            blockStateProps = blockStatePropsDefault;
            return;
        }
        blockStateProps = BlockStatePropertyMap.of( split[1] );
        value = of( blockResLoc, blockStateProps );
    }
    
    /** @return Returns the config field's value. */
    @Override
    public BlockStateKey.Basic get() {
        BlockStateKey.Basic key = getValue();
        return key == null ? airKey() : key;
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public BlockStateKey.Basic getValue() {
        if( value == null ) {
            // Use defaults if this is called before field is loaded.
            if( blockStateProps == null || blockResLoc == null ) {
                return getDefaultValue();
            }
            return value = of( blockResLoc, blockStateProps );
        }
        return value;
    }
    
    /** @return The default value of this field. */
    @Override
    public BlockStateKey.Basic getDefaultValue() {
        if( actualDefault == null ) {
            return actualDefault = of( blockResLocDefault, blockStatePropsDefault );
        }
        return actualDefault;
    }
    
    /** @return The registered block with the given ID, or null if it does not exist. */
    @Nullable
    private Block getBlock( ResourceLocation resLoc ) {
        return ForgeRegistries.BLOCKS.getValue( resLoc );
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() {
        return new ItemViewWidgetProvider.Simple<>( get(), ItemViewRendererRegistry.getRendererOrThrow( ItemViewRendererRegistry.BLOCK_STATE ), ( s ) -> {
            ResourceLocation id = ResourceLocation.tryParse( BlockStatePropertyMap.split( s )[0] );
            return id != null && ForgeRegistries.BLOCKS.containsKey( id );
        } );
    }
}