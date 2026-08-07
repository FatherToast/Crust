package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a config field containing a block state value
 * wrapped in a {@link BlockStateKey.Basic} key.
 */
@SuppressWarnings( "unused" )
public class BlockStateField extends AbstractConfigField<BlockStateKey.Basic> {
    
    /** Creates a new field using a string-described block state. */
    public BlockStateField( String key, BlockStateKey.Basic defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
        
        // Sanity checks
        if( defaultValue.isBlacklist() ) {
            throw new IllegalArgumentException( "Block state field cannot use a blacklist key! Invalid field: " + getKey() );
        }
    }
    
    /** Creates a new field using a string-described block state. */
    public BlockStateField( String key, String defaultValue, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultValue, false ), description );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, String defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultBlock, defaultProperties, false ), description );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceLocation defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultBlock, defaultProperties, false ), description );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, RegistryObject<? extends Block> defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultBlock, defaultProperties, false ), description );
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceKey<? extends Block> defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultBlock, defaultProperties, false ), description );
    }
    
    /**
     * Creates a new field using a block and block state properties. This only works for vanilla
     * blocks unless you hold off config initialization until after the blocks registry is populated.
     */
    public BlockStateField( String key, Block defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultBlock, defaultProperties, false ), description );
    }
    
    /**
     * Creates a new field using a pre-existing block state. This only works for vanilla blocks
     * unless you hold off config initialization until after the blocks registry is populated.
     */
    public BlockStateField( String key, BlockState defaultValue, @Nullable String... description ) {
        this( key, BlockStateKey.of( defaultValue, false ), description );
    }
    
    /** @return This config field's block state value, or air if it does not exist. */
    public BlockState getBlockState() {
        return Objects.requireNonNullElse( get().asValue(), Blocks.AIR.defaultBlockState() );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Block State", getDefaultValue(),
                "\"namespace:path[property1=value1,property2=value2,...]\"" ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public BlockStateKey.Basic parse( Object raw ) {
        String s = raw.toString();
        BlockStateKey<?> value = BlockStateKey.parse( null, // Suppresses parser's warnings; we'll do our own
                s, s, false );
        if( !(value instanceof BlockStateKey.Basic key) ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid block state! Must follow the pattern \"namespace:path[property1=value1,property2=value2,...]\". Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        return key;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( BlockStateKey.Basic value, FriendlyByteBuf buffer ) { buffer.writeUtf( value.toTomlString() ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public BlockStateKey.Basic deserialize( FriendlyByteBuf buffer ) { return parse( buffer.readUtf() ); }
    
    // Could do this for serialization to reduce data transfer, but it does force all state props to be explicitly defined
    //buffer.writeId( Block.BLOCK_STATE_REGISTRY, value.getBlockState() );
    //BlockStateKey.of( buffer.readById( Block.BLOCK_STATE_REGISTRY ), false );
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<BlockStateKey.Basic> getWidgetProvider() {
        return new EntryViewWidgetProvider.SimpleMapped<>( BlockStateKey.Basic::asValue,
                EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.BLOCK_STATE ),
                ( string ) -> {
                    ResourceLocation id = ResourceLocation.tryParse( BlockStatePropertyMap.split( string )[0] );
                    return id != null && ForgeRegistries.BLOCKS.containsKey( id );
                } );
    }
}