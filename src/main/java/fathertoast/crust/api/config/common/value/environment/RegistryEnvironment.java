package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

/**
 * Registries are contained in {@link net.minecraftforge.registries.ForgeRegistries}
 */
public abstract class RegistryEnvironment<T> extends AbstractEnvironment {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    /** The registry key for this environment. */
    private final ResourceLocation REGISTRY_KEY;
    
    private T registryEntry;
    
    public RegistryEnvironment( ResourceLocation registryKey, boolean invert ) {
        FIELD = null;
        INVERT = invert;
        REGISTRY_KEY = registryKey;
    }
    
    public RegistryEnvironment( AbstractConfigField field, String value ) {
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
    public abstract IForgeRegistry<T> getRegistry();
    
    /** @return The registry entry. */
    @Nullable
    protected final T getRegistryEntry() {
        if( registryEntry == null ) {
            if( !getRegistry().containsKey( REGISTRY_KEY ) ) {
                ConfigUtil.warnFor( FIELD );
                ConfigUtil.LOG.warn( "Environment entry has registry key not present in registry \"{}\". Entry: {}",
                        getRegistry().getRegistryName(), this );
            }
            registryEntry = getRegistry().getValue( REGISTRY_KEY );
        }
        return registryEntry;
    }
}