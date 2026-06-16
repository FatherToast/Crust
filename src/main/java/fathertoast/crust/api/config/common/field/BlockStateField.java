package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
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
import java.util.function.Supplier;

/**
 * Represents a config field with a block state value.
 */
@SuppressWarnings( "unused" )
@ApiStatus.Experimental
public class BlockStateField extends GenericField<BlockState> implements Supplier<BlockState> {
    
    /** The default block's resource location. */
    protected final ResourceLocation blockResLocDefault;
    /** The properties defining the default block state. */
    protected final BlockStatePropertyMap blockStatePropsDefault;
    
    /** The actual default block state, once it is loaded. */
    protected BlockState actualDefault;
    
    /** The default block's resource location. */
    protected ResourceLocation blockResLoc;
    /** The properties defining the default block state. */
    protected BlockStatePropertyMap blockStateProps;
    
    /** Creates a new field using a string-described block state. */
    public BlockStateField( String key, String defaultValue, @Nullable String... description ) {
        super( key, Blocks.AIR.defaultBlockState(), description );
        final String[] split = BlockStatePropertyMap.split( defaultValue );
        blockResLocDefault = ResourceLocation.parse( split[0] );
        blockStatePropsDefault = BlockStatePropertyMap.of( split[1] );
        actualDefault = blockStatePropsDefault.stateForNullable( getBlock( blockResLocDefault ) );
        
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, ResourceLocation defaultResLoc, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        super( key, Blocks.AIR.defaultBlockState(), description );
        blockResLocDefault = defaultResLoc;
        blockStatePropsDefault = defaultProperties;
    }
    
    /** Creates a new field. */
    public BlockStateField( String key, RegistryObject<? extends Block> defaultBlock, BlockStatePropertyMap defaultProperties, @Nullable String... description ) {
        //noinspection DataFlowIssue
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
        super( key, defaultValue, description );
        blockResLocDefault = Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( defaultValue.getBlock() ) );
        blockStatePropsDefault = BlockStatePropertyMap.of( defaultValue );
        actualDefault = defaultValue;
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
        value = blockStateProps.stateForNullable( getBlock( blockResLoc ) );
    }
    
    /** @return Returns the config field's value. */
    @Override
    public BlockState get() {
        BlockState blockState = getValue();
        return blockState == null ? Blocks.AIR.defaultBlockState() : blockState;
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public BlockState getValue() {
        return value == null ? value = blockStateProps.stateForNullable( getBlock( blockResLoc ) ) : value;
    }
    
    /** @return The default value of this field. */
    @Override
    public BlockState getDefaultValue() {
        if( actualDefault == null ) {
            actualDefault = blockStatePropsDefault.stateForNullable( getBlock( blockResLocDefault ) );
            return actualDefault == null ? valueDefault : actualDefault;
        }
        return actualDefault;
    }
    
    /** @return Tries to get a registered block and returns it, or null if is fails. */
    @Nullable
    private Block getBlock( ResourceLocation resLoc ) {
        Block b = ForgeRegistries.BLOCKS.getValue( resLoc );
        return b == null || b.defaultBlockState().isAir() ? null : b;
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeLine( TomlHelper.toLiteral( blockResLoc + blockStateProps.toString() ), output );
    }
    
    //    /** @return This field's gui component provider. */ TODO
    //    @Override
    //    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringFieldWidgetProvider( this ); }
}