package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class DynamicRegistryEnvironment<T> extends AbstractEnvironment {
    
    /** The config manager responsible for this entry. */
    private final ConfigManager MANAGER;
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    /** The registry key for this environment. */
    protected final ResourceLocation REGISTRY_KEY;
    
    private T registryEntry;
    /** The value of {@link ConfigManager#getDynamicRegVersion()} at the time of last poll. */
    private byte version = -1;
    
    public DynamicRegistryEnvironment( ConfigManager cfgManager, ResourceKey<T> regKey, boolean invert ) {
        this( cfgManager, regKey.location(), invert );
    }
    
    public DynamicRegistryEnvironment( ConfigManager cfgManager, ResourceLocation regKey, boolean invert ) {
        MANAGER = cfgManager;
        FIELD = null;
        INVERT = invert;
        REGISTRY_KEY = regKey;
    }
    
    public DynamicRegistryEnvironment( AbstractConfigField field, String value ) {
        MANAGER = field.getSpec().MANAGER;
        FIELD = field;
        INVERT = value.startsWith( "!" );
        ResourceLocation resLoc = ResourceLocation.tryParse( INVERT ? value.substring( 1 ) : value );
        if( resLoc == null ) {
            REGISTRY_KEY = ResourceLocation.withDefaultNamespace( "" );
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry has invalid resource location! Ignoring. Entry: {}", name() + " " + value );
        }
        else {
            REGISTRY_KEY = resLoc;
        }
    }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public final String value() { return (INVERT ? "!" : "") + REGISTRY_KEY.toString(); }
    
    /** @return The registry used. */
    public abstract ResourceKey<Registry<T>> getRegistry();
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        if( level instanceof ServerLevel serverLevel )
            return matches( serverLevel, pos ); // These don't work on the client :(
        return INVERT;
    }
    
    /** @return Returns true if this environment matches the provided environment. */
    public abstract boolean matches( ServerLevel level, @Nullable BlockPos pos );
    
    /** @return The target registry object. */
    @Nullable
    public final T getRegistryEntry( ServerLevel level ) {
        if( version != MANAGER.getDynamicRegVersion() ) {
            version = MANAGER.getDynamicRegVersion();
            
            final Registry<T> registry = level.getServer().registryAccess().registryOrThrow( getRegistry() );
            registryEntry = registry.get( REGISTRY_KEY );
            if( registryEntry == null ) {
                ConfigUtil.warnFor( FIELD );
                ConfigUtil.LOG.warn( "Environment entry has registry key not present in dynamic registry \"{}\". Entry: {}",
                        getRegistry().location(), this );
            }
        }
        return registryEntry;
    }
}