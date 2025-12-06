package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Wraps the various registry types so that reg obj keys can ignore their implementation differences.
 *
 * @param <T> The type of values registered.
 * @see net.minecraft.core.registries.Registries
 */
@ApiStatus.Experimental
public interface IRegWrapper<T> {
    /**
     * @return A wrapper we can use to refer to the registry identified in an agnostic way.
     * If the registry is not currently available, a "lazy" wrapper is returned, which acts
     * like an empty registry until the registry becomes available.
     */
    static <T> IRegWrapper<T> forKey( ResourceKey<? extends Registry<T>> key ) { return Pool.get( key ); }
    
    /** @return A wrapper we can use to refer to the registry in an agnostic way. */
    static <T> IRegWrapper<T> of( Registry<T> registry ) { return forKey( registry.key() ); }
    
    /** @return A wrapper we can use to refer to the registry in an agnostic way. */
    static <T> IRegWrapper<T> of( IForgeRegistry<T> registry ) { return forKey( registry.getRegistryKey() ); }
    
    
    // ---- Wrapper Implementations ---- //
    
    /** @return The resource key that identifies the registry itself. */
    ResourceKey<? extends Registry<T>> registryKey();
    
    /** @return The registry name. */
    default ResourceLocation registryName() { return registryKey().location(); }
    
    /** @return The registry's fuzzy key parser. */
    default IFuzzyKeyParser<T> getParser() { return RegObjKey.parser( registryKey() ); }
    
    /** @return True if the registry is present. */
    default boolean isPresent() { return true; }
    
    /** @return True if the registry is dynamic - that is, its contents may vary. */
    default boolean isDynamic() { return false; }
    
    /** @return The underlying Forge registry, or null if the registry is unavailable or vanilla. */
    @Nullable
    default IForgeRegistry<T> asForgeRegistry() { return null; }
    
    /** @return The underlying vanilla registry, or null if the registry is unavailable or non-vanilla. */
    @Nullable
    default Registry<T> asVanillaRegistry() { return null; }
    
    /** @return The registry key for the object. */
    @Nullable
    ResourceLocation getKey( T target );
    
    /** @return The object registered to the provided key. */
    @Nullable
    T get( ResourceLocation key );
    
    /** @return True if the registry supports tags. */
    default boolean supportsTags() { return true; }
    
    /** @return True if the tag contains a particular object. */
    boolean tagContains( TagKey<T> tag, T target );
    
    /** @return A random object contained by the tag, or null if the tag is empty. */
    @Nullable
    default T nextOfTag( TagKey<T> tag, Random random ) { return nextOfTag( tag, JavaRandomSource.of( random ) ); }
    
    /** @return A random object contained by the tag, or null if the tag is empty. */
    @Nullable
    T nextOfTag( TagKey<T> tag, RandomSource random );
    
    /** @return An iterator over the objects contained by the tag, or null if anything goes wrong. */
    @Nullable
    Iterator<T> tagIterator( TagKey<T> tag );
    
    
    /** A registry wrapper for a standard registry. */
    class Forge<T> implements IRegWrapper<T> {
        private final IForgeRegistry<T> registry;
        
        Forge( IForgeRegistry<T> reg ) { registry = reg; }
        
        public IForgeRegistry<T> getRegistry() { return registry; }
        
        /** @return The resource key that identifies the registry itself. */
        @Override
        public ResourceKey<? extends Registry<T>> registryKey() { return getRegistry().getRegistryKey(); }
        
        /** @return The underlying Forge registry, or null if the registry is unavailable or vanilla. */
        @Override
        public IForgeRegistry<T> asForgeRegistry() { return getRegistry(); }
        
        /** @return The registry key for the object. */
        @Override
        @Nullable
        public ResourceLocation getKey( T target ) { return getRegistry().getKey( target ); }
        
        /** @return The object registered to the provided key. */
        @Override
        @Nullable
        public T get( ResourceLocation key ) { return getRegistry().getValue( key ); }
        
        /** @return True if the registry supports tags. */
        @Override
        public boolean supportsTags() { return getRegistry().tags() != null; }
        
        /** @return True if the tag contains a particular object. */
        @Override
        public boolean tagContains( TagKey<T> tag, T target ) {
            ITagManager<T> tags = getRegistry().tags();
            return tags != null && tags.getReverseTag( target ).map( ( reverseTag ) ->
                    reverseTag.containsTag( tag ) ).orElse( false );
        }
        
        /** @return A random object contained by the tag, or null if the tag is empty. */
        @Override
        @Nullable
        public T nextOfTag( TagKey<T> tag, RandomSource random ) {
            ITagManager<T> tags = getRegistry().tags();
            return tags != null && tags.isKnownTagName( tag ) ?
                    tags.getTag( tag ).getRandomElement( random ).orElse( null ) : null;
        }
        
        /** @return An iterator over the objects contained by the tag, or null if anything goes wrong. */
        @Override
        @Nullable
        public Iterator<T> tagIterator( TagKey<T> tag ) {
            ITagManager<T> tags = getRegistry().tags();
            return tags != null && tags.isKnownTagName( tag ) ?
                    tags.getTag( tag ).iterator() : null;
        }
    }
    
    /** A registry wrapper for a vanilla registry that is not wrapped by Forge. */
    class Vanilla<T> implements IRegWrapper<T> {
        private final Registry<T> registry;
        
        Vanilla( Registry<T> reg ) { registry = reg; }
        
        public Registry<T> getRegistry() { return registry; }
        
        /** @return The resource key that identifies the registry itself. */
        @Override
        public ResourceKey<? extends Registry<T>> registryKey() { return getRegistry().key(); }
        
        /** @return The underlying vanilla registry, or null if the registry is unavailable or non-vanilla. */
        @Override
        public Registry<T> asVanillaRegistry() { return getRegistry(); }
        
        /** @return The registry key for the object. */
        @Override
        @Nullable
        public ResourceLocation getKey( T target ) { return getRegistry().getKey( target ); }
        
        /** @return The object registered to the provided key. */
        @Override
        @Nullable
        public T get( ResourceLocation key ) { return getRegistry().get( key ); }
        
        /** @return True if the tag contains a particular object. */
        @Override
        public boolean tagContains( TagKey<T> tag, T target ) {
            return getRegistry().wrapAsHolder( target ).is( tag );
        }
        
        /** @return A random object contained by the tag, or null if the tag is empty. */
        @Override
        @Nullable
        public T nextOfTag( TagKey<T> tag, RandomSource random ) {
            return getRegistry().getTag( tag ).flatMap( ( tagSet ) ->
                    tagSet.getRandomElement( random ) ).map( Holder::value ).orElse( null );
        }
        
        /** @return An iterator over the objects contained by the tag, or null if anything goes wrong. */
        @Override
        @Nullable
        public Iterator<T> tagIterator( TagKey<T> tag ) {
            return getRegistry().getTag( tag ).map( ( tagSet ) ->
                    new HolderIterator<>( tagSet.iterator() ) ).orElse( null );
        }
    }
    
    /** A registry wrapper for a dynamic registry. Is generally empty prior to world load, and may vary over time. */
    class Dynamic<T> implements IRegWrapper<T> {
        private final ConfigManager manager = ConfigManager.getRequired( ICrustApi.MOD_ID );
        private final ResourceKey<? extends Registry<T>> registryKey;
        
        @Nullable
        private Registry<T> cachedRegistry;
        private int version = manager.getDynamicRegVersion();
        
        Dynamic( Registry<T> reg ) {
            registryKey = reg.key();
            cachedRegistry = reg;
        }
        
        @Nullable
        public Registry<T> getRegistry() {
            if( version != manager.getDynamicRegVersion() ) {
                version = manager.getDynamicRegVersion();
                cachedRegistry = getDynamicRegistry( registryKey );
            }
            return cachedRegistry;
        }
        
        /** @return The resource key that identifies the registry itself. */
        @Override
        public ResourceKey<? extends Registry<T>> registryKey() { return registryKey; }
        
        /** @return True if the registry is present. */
        @Override
        public boolean isPresent() { return getRegistry() != null; }
        
        /** @return True if the registry is dynamic - that is, its contents may vary. */
        @Override
        public boolean isDynamic() { return true; }
        
        /** @return The underlying vanilla registry, or null if the registry is unavailable or non-vanilla. */
        @Override
        @Nullable
        public Registry<T> asVanillaRegistry() { return getRegistry(); }
        
        /** @return The registry key for the object. */
        @Override
        @Nullable
        public ResourceLocation getKey( T target ) {
            Registry<T> reg = getRegistry();
            return reg == null ? null : reg.getKey( target );
        }
        
        /** @return The object registered to the provided key. */
        @Override
        @Nullable
        public T get( ResourceLocation key ) {
            Registry<T> reg = getRegistry();
            return reg == null ? null : reg.get( key );
        }
        
        /** @return True if the tag contains a particular object. */
        @Override
        public boolean tagContains( TagKey<T> tag, T target ) {
            Registry<T> reg = getRegistry();
            return reg != null && reg.wrapAsHolder( target ).is( tag );
        }
        
        /** @return A random object contained by the tag, or null if the tag is empty. */
        @Override
        @Nullable
        public T nextOfTag( TagKey<T> tag, RandomSource random ) {
            Registry<T> reg = getRegistry();
            return reg == null ? null : reg.getTag( tag ).flatMap( ( tagSet ) ->
                    tagSet.getRandomElement( random ) ).map( Holder::value ).orElse( null );
        }
        
        /** @return An iterator over the objects contained by the tag, or null if anything goes wrong. */
        @Override
        @Nullable
        public Iterator<T> tagIterator( TagKey<T> tag ) {
            Registry<T> reg = getRegistry();
            return reg == null ? null : reg.getTag( tag ).map( ( tagSet ) ->
                    new HolderIterator<>( tagSet.iterator() ) ).orElse( null );
        }
    }
    
    /**
     * A registry wrapper for a registry that was unavailable at the time we wanted a wrapper.
     * Behaves like an empty registry (only has its name/key) until the underlying registry becomes available.
     */
    class Lazy<T> implements IRegWrapper<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;
        @Nullable
        private IRegWrapper<T> registry;
        
        Lazy( ResourceKey<? extends Registry<T>> key ) { registryKey = key; }
        
        @Nullable
        public IRegWrapper<T> getRegistry() {
            if( registry == null ) registry = Pool.tryGet( registryKey );
            return registry;
        }
        
        /** @return The resource key that identifies the registry itself. */
        @Override
        public ResourceKey<? extends Registry<T>> registryKey() { return registryKey; }
        
        /** @return True if the registry is present. */
        @Override
        public boolean isPresent() {
            IRegWrapper<T> reg = getRegistry();
            return reg != null && reg.isPresent();
        }
        
        /** @return True if the registry is dynamic - that is, its contents may vary. */
        @Override
        public boolean isDynamic() {
            IRegWrapper<T> reg = getRegistry();
            return reg == null || reg.isDynamic(); // Default to true if not present
        }
        
        /** @return The underlying Forge registry, or null if the registry is unavailable or vanilla. */
        @Override
        @Nullable
        public IForgeRegistry<T> asForgeRegistry() {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.asForgeRegistry();
        }
        
        /** @return The underlying vanilla registry, or null if the registry is unavailable or non-vanilla. */
        @Override
        @Nullable
        public Registry<T> asVanillaRegistry() {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.asVanillaRegistry();
        }
        
        /** @return The registry key for the object. */
        @Override
        @Nullable
        public ResourceLocation getKey( T target ) {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.getKey( target );
        }
        
        /** @return The object registered to the provided key. */
        @Override
        @Nullable
        public T get( ResourceLocation key ) {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.get( key );
        }
        
        /** @return True if the registry supports tags. */
        @Override
        public boolean supportsTags() {
            IRegWrapper<T> reg = getRegistry();
            return reg != null && reg.supportsTags();
        }
        
        /** @return True if the tag contains a particular object. */
        @Override
        public boolean tagContains( TagKey<T> tag, T target ) {
            IRegWrapper<T> reg = getRegistry();
            return reg != null && reg.tagContains( tag, target );
        }
        
        /** @return A random object contained by the tag, or null if the tag is empty. */
        @Override
        @Nullable
        public T nextOfTag( TagKey<T> tag, RandomSource random ) {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.nextOfTag( tag, random );
        }
        
        /** @return An iterator over the objects contained by the tag, or null if anything goes wrong. */
        @Override
        @Nullable
        public Iterator<T> tagIterator( TagKey<T> tag ) {
            IRegWrapper<T> reg = getRegistry();
            return reg == null ? null : reg.tagIterator( tag );
        }
    }
    
    
    /** Holds all registry wrappers that have been created, since we don't need multiple wrappers per registry. */
    class Pool {
        private static final Map<ResourceLocation, IRegWrapper<?>> POOL = new HashMap<>();
        
        /** @return The wrapper, or null if the registry has not yet been wrapped. */
        public static <T> IRegWrapper<T> get( ResourceKey<? extends Registry<T>> key ) {
            IRegWrapper<T> regWrapper = tryGet( key );
            return regWrapper == null ? new Lazy<>( key ) : regWrapper;
        }
        
        /**
         * @return A wrapper we can use to refer to the registry identified.
         * If the registry is not (yet) available, returns null.
         */
        @Nullable
        public static <T> IRegWrapper<T> tryGet( ResourceKey<? extends Registry<T>> key ) {
            @SuppressWarnings( "unchecked" )
            IRegWrapper<T> pooledWrapper = (IRegWrapper<T>) POOL.get( key.location() );
            if( pooledWrapper != null ) return pooledWrapper;
            
            @SuppressWarnings( "UnstableApiUsage" )
            IForgeRegistry<T> forgeReg = RegistryManager.ACTIVE.getRegistry( key );
            if( forgeReg != null ) return Pool.pop( new Forge<>( forgeReg ) );
            
            Registry<?> vanillaReg = BuiltInRegistries.REGISTRY.get( key.location() );
            if( vanillaReg != null ) try {
                //noinspection unchecked
                return Pool.pop( new Vanilla<>( (Registry<T>) vanillaReg ) );
            }
            catch( Exception ignored ) { return null; } // A registry exists, but is invalid, let's just ignore it
            
            Registry<T> dynamicReg = getDynamicRegistry( key );
            if( dynamicReg != null ) return Pool.pop( new Dynamic<>( dynamicReg ) );
            
            return null;
        }
        
        /** @return Pools the wrapper and returns it. */
        private static <T> IRegWrapper<T> pop( IRegWrapper<T> newWrapper ) {
            POOL.put( newWrapper.registryName(), newWrapper );
            return newWrapper;
        }
    }
    
    
    /** @return The current dynamic registry, if available. */
    @Nullable
    static <T> Registry<T> getDynamicRegistry( ResourceKey<? extends Registry<T>> key ) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server != null ) {
            Optional<Registry<T>> dynamicReg = server.registryAccess().registry( key );
            if( dynamicReg.isPresent() ) return dynamicReg.get();
        }
        return null;
    }
    
    record HolderIterator<T>( Iterator<Holder<T>> itr ) implements Iterator<T> {
        @Override
        public boolean hasNext() { return itr.hasNext(); }
        
        @Override
        public T next() { return itr.next().get(); }
    }
}