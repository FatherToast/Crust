package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;

/**
 * A key for fuzzy collections that test against or contain item stacks. Very similar to an Item registry
 * object key, but allows specifying a data tag in addition to (or instead of) just registered items.
 * <p>
 * Note that this does not support a stack size parameter; you should choose a collection that supports
 * values if you want to allow users to specify stack size.
 */
@ApiStatus.Experimental
public abstract class ItemStackKey<K extends RegObjKey<Item>> extends FuzzyKey<ItemStack> {
    
    /** The parser for item stack keys. */
    public static final IFuzzyKeyParser<ItemStack> PARSER = new Parser();
    
    /** @return A new data-tag-only key based on the data tag. */
    public static DataTagOnly ofDataTag( String tag, boolean blacklist ) {
        return ofDataTag( Objects.requireNonNull( NBTHelper.toNBT( tag ) ), blacklist );
    }
    
    /** @return A new data-tag-only key based on the data tag. */
    public static DataTagOnly ofDataTag( CompoundTag tag, boolean blacklist ) {
        return new DataTagOnly( blacklist, tag );
    }
    
    /** @return A new key based on the resource location and data tag. */
    public static Basic of( String resLocAndTag, boolean blacklist ) {
        String[] keys = split( resLocAndTag );
        return of( RegObjKey.of( REGISTRY, keys[0], blacklist ), keys[1].isEmpty() ? null : NBTHelper.toNBT( keys[1] ) );
    }
    
    /** @return A new key based on the resource location and data tag. */
    public static Basic of( String resLoc, @Nullable CompoundTag tag, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ), tag );
    }
    
    /** @return A new key based on the resource location and data tag. */
    public static Basic of( ResourceLocation resLoc, @Nullable CompoundTag tag, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ), tag );
    }
    
    /** @return A new key based on the registry object and data tag. */
    public static Basic of( RegistryObject<? extends Item> regObj, @Nullable CompoundTag tag, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, regObj, blacklist ), tag );
    }
    
    /** @return A new key based on the resource key and data tag. */
    public static Basic of( ResourceKey<? extends Item> resKey, @Nullable CompoundTag tag, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resKey, blacklist ), tag );
    }
    
    /**
     * @return A new key based on the item and data tag.
     * When building default config values, this is only suitable for vanilla items unless you
     * hold off config initialization until after the items registry is populated.
     */
    public static Basic of( Item item, @Nullable CompoundTag tag, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, item, blacklist ), tag );
    }
    
    /**
     * @return A new key based on the item and data tag.
     * When building default config values, this is only suitable for vanilla items unless you
     * hold off config initialization until after the items registry is populated.
     */
    public static Basic of( ItemStack itemStack, boolean blacklist ) {
        return of( itemStack.getItem(), itemStack.getTag(), blacklist );
    }
    
    /** @return A new key based on the block registry object key and data tag. */
    public static Basic of( RegObjKey.Basic<Item> key, @Nullable CompoundTag tag ) {
        return new Basic( key, tag );
    }
    
    /** @return A new wildcard key, based on the partial resource location and data tag. */
    public static Wildcard ofWildcard( ResourceLocation partialResLoc, @Nullable CompoundTag tag, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, partialResLoc, blacklist ), tag );
    }
    
    /** @return A new wildcard key, based on the namespace and data tag. */
    public static Wildcard ofWildcard( String namespace, @Nullable CompoundTag tag, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, namespace, blacklist ), tag );
    }
    
    /** @return A new wildcard key, based on the namespace, partial path, and data tag. */
    public static Wildcard ofWildcard( String namespace, String partialPath, @Nullable CompoundTag tag, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, blacklist ), tag );
    }
    
    /** @return A new wildcard key, based on the item registry object key and data tag. */
    public static Wildcard ofWildcard( RegObjKey.Wildcard<Item> key, @Nullable CompoundTag tag ) {
        return new Wildcard( key, tag );
    }
    
    /** @return A new tag key based on the tag resource location and data tag. */
    public static Tag ofTag( String resLocAndTag, boolean blacklist ) {
        String[] keys = split( resLocAndTag );
        return ofTag( RegObjKey.ofTag( REGISTRY, keys[0], blacklist ), NBTHelper.toNBT( keys[1] ) );
    }
    
    /** @return A new tag key based on the tag resource location and data tag. */
    public static Tag ofTag( ResourceLocation resLoc, @Nullable CompoundTag tag, boolean blacklist ) {
        return ofTag( RegObjKey.ofTag( REGISTRY, resLoc, blacklist ), tag );
    }
    
    /** @return A new tag key based on the tag key (well, different kind of tag key) and data tag. */
    public static Tag ofTag( TagKey<? extends Item> tag, @Nullable CompoundTag dTag, boolean blacklist ) {
        return ofTag( RegObjKey.ofTag( REGISTRY, tag, blacklist ), dTag );
    }
    
    /** @return A new tag key based on the item registry object key and data tag. */
    public static Tag ofTag( RegObjKey.Tag<Item> key, @Nullable CompoundTag tag ) {
        return new Tag( key, tag );
    }
    
    /** @return A new key based on the item registry object key and data tag. */
    public static ItemStackKey<?> ofRegObj( RegObjKey<Item> key, @Nullable CompoundTag tag ) {
        if( key instanceof RegObjKey.Basic<Item> k ) return new Basic( k, tag );
        if( key instanceof RegObjKey.Wildcard<Item> k ) return new Wildcard( k, tag );
        if( key instanceof RegObjKey.Tag<Item> k ) return new Tag( k, tag );
        throw new IllegalArgumentException( "Invalid registry object key!" );
    }
    
    /** @return A new key, parsed from a key string, or null if the key was invalid. */
    @Nullable
    public static ItemStackKey<?> parse( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
        String[] keyAndProps = split( key );
        CompoundTag tag = keyAndProps[1].isEmpty() ? null : NBTHelper.toNBT( keyAndProps[1] );
        if( keyAndProps[0].isEmpty() ) return tag == null ? null : new DataTagOnly( blacklist, tag );
        FuzzyKey<Item> loadedKey = REG_PARSER.parseKeyString( field, line, keyAndProps[0], blacklist );
        return loadedKey == null ? null :
                loadedKey instanceof RegObjKey.Basic<Item> k ? new Basic( k, tag ) :
                        loadedKey instanceof RegObjKey.Wildcard<Item> k ? new Wildcard( k, tag ) :
                                loadedKey instanceof RegObjKey.Tag<Item> k ? new Tag( k, tag ) : null;
    }
    
    /**
     * @return Splits an item stack string into a registry object string (index 0) and a data tag
     * string (index 1). The returned array always contains 2 non-null strings.
     */
    private static String[] split( String s ) {
        int startIndex = s.indexOf( '{' );
        if( startIndex < 0 ) return new String[] { s, "" };
        return new String[] { s.substring( 0, startIndex ), s.substring( startIndex ) };
    }
    
    
    // ---- Key Implementations ---- //
    
    protected static final IRegWrapper<Item> REGISTRY = IRegWrapper.of( ForgeRegistries.ITEMS );
    protected static final IFuzzyKeyParser<Item> REG_PARSER = REGISTRY.getParser();
    
    /** This is null for PropsOnly keys. */
    protected final K regObjKey;
    protected final @Nullable CompoundTag dataTag;
    protected final String dataTagString;
    
    protected ItemStackKey( @Nullable K k, boolean blacklist, @Nullable CompoundTag t ) {
        super( blacklist );
        regObjKey = k;
        dataTag = t;
        dataTagString = t == null ? "" : NBTHelper.toSNBT( t );
    }
    
    /** @return The item, converted to an item stack based on this key's properties. */
    @Nullable
    protected ItemStack convertToStack( @Nullable Item item ) {
        if( item == null ) return null;
        ItemStack itemStack = new ItemStack( item, 1 );
        if( dataTag != null ) itemStack.setTag( dataTag.copy() );
        return itemStack;
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return regObjKey.keyString() + dataTagString; }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( ItemStack target ) {
        return regObjKey.matches( target.getItem() ) && NBTHelper.matches( target.getTag(), dataTag );
    }
    
    
    /**
     * A key that matches any item with appropriate data tag.
     */
    @ApiStatus.Experimental
    public static class DataTagOnly extends ItemStackKey<RegObjKey<Item>> {
        
        protected DataTagOnly( boolean blacklist, CompoundTag t ) { super( null, blacklist, t ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return dataTagString; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( ItemStack target ) { return NBTHelper.matches( target.getTag(), dataTag ); }
    }
    
    
    /**
     * A key that matches one specific item with appropriate data tag.
     */
    @ApiStatus.Experimental
    public static class Basic extends ItemStackKey<RegObjKey.Basic<Item>> implements IReverseKey<ItemStack> {
        
        protected Basic( RegObjKey.Basic<Item> k, @Nullable CompoundTag t ) { super( k, k.isBlacklist(), t ); }
        
        /** @return The value that matches this key, or null if anything goes wrong. */
        @Override // IReverseKey
        @Nullable // Should return an air item most of the time if the item was missing, but not guaranteed
        public ItemStack asValue() { return convertToStack( regObjKey.asValue() ); }
    }
    
    
    /**
     * A key that matches all items in a namespace that have a path starting with a specific string
     * with appropriate data tag.
     */
    @ApiStatus.Experimental
    public static class Wildcard extends ItemStackKey<RegObjKey.Wildcard<Item>> {
        
        protected Wildcard( RegObjKey.Wildcard<Item> k, @Nullable CompoundTag t ) { super( k, k.isBlacklist(), t ); }
    }
    
    
    /**
     * A key that matches all items contained by a specific tag with appropriate data tag.
     */
    @ApiStatus.Experimental
    public static class Tag extends ItemStackKey<RegObjKey.Tag<Item>> implements IMultiKey<ItemStack> {
        
        protected Tag( RegObjKey.Tag<Item> k, @Nullable CompoundTag t ) { super( k, k.isBlacklist(), t ); }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Override // IRandomKey
        @Nullable
        public ItemStack nextValue( RandomSource random ) { return convertToStack( regObjKey.nextValue( random ) ); }
        
        /** @return An iterator over all values that match this key, or null if anything goes wrong. */
        @Override // IMultiKey
        @Nullable
        public Iterator<ItemStack> getValueIterator() {
            Iterator<Item> itr = regObjKey.getValueIterator();
            return itr == null ? null : new ConverterIterator<>( itr, this::convertToStack );
        }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private record Parser( ) implements IFuzzyKeyParser<ItemStack> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() { return "Item Stack"; }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return REG_PARSER.getPatterns( usage ) +
                    ", \"namespace:path{tag1:value1,tag2:value2,...}\" (Note: {data_tags} is allowed on any key)";
        }
        
        /**
         * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
         * and if the config field is not null, provide useful feedback and identify the field.
         *
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param key   The key string to parse from.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Override
        @Nullable
        public FuzzyKey<ItemStack> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            return parse( field, line, key, blacklist );
        }
    }
}