package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.api.util.OnClient;
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
 * Represents a config field containing a block state value
 * wrapped in a {@link BlockStateKey.Basic} key.
 */
@ApiStatus.Experimental
public class BlockStateField extends GenericField<BlockStateKey.Basic> {
    
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
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceLocation defaultResLoc, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        super( key, BlockStateKey.of( defaultResLoc, defaultProperties, false ), description );
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
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Block State", valueDefault,
                "\"namespace:path[property1=value1,property2=value2,...]\"" ) );
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
            return;
        }
        final String[] split = BlockStatePropertyMap.split( raw.toString() );
        final ResourceLocation blockId = ResourceLocation.tryParse( split[0] );
        
        if( blockId == null ) {
            // Invalid resource location
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid block state! Must follow the pattern \"namespace:path[property1=value1,property2=value2,...]\". Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            value = getDefaultValue();
            return;
        }
        value = of( blockId, BlockStatePropertyMap.of( split[1] ) );
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
            return value = getDefaultValue();
        }
        return value;
    }
    
    /** @return This config field's block state value, if it exists. */
    @Nullable
    private BlockState getBlockState() {
        return get().asValue();
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() {
        return new EntryViewWidgetProvider.Simple<>( this::getBlockState, EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.BLOCK_STATE ), ( s ) -> {
            ResourceLocation id = ResourceLocation.tryParse( BlockStatePropertyMap.split( s )[0] );
            return id != null && ForgeRegistries.BLOCKS.containsKey( id );
        } );
    }
}