package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registries are contained in {@link net.minecraftforge.registries.ForgeRegistries}
 */
public abstract class RegistryGroupEnvironment<T> extends AbstractEnvironment {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    /** The namespace for this environment. */
    private final String NAMESPACE;
    
    private List<T> registryEntries;
    
    
    public RegistryGroupEnvironment( ResourceLocation regKey, boolean invert ) {
        FIELD = null;
        INVERT = invert;
        NAMESPACE = regKey.toString();
    }
    
    public RegistryGroupEnvironment( AbstractConfigField field, String value ) {
        FIELD = field;
        INVERT = value.startsWith( "!" );
        NAMESPACE = value.substring( INVERT ? 1 : 0, value.length() - 1 );
    }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public final String value() { return (INVERT ? "!" : "") + NAMESPACE + "*"; }
    
    /** @return The registry used. */
    public abstract IForgeRegistry<T> getRegistry();
    
    /** @return The registry entries. */
    protected final List<T> getRegistryEntries() {
        if( registryEntries == null ) {
            registryEntries = new ArrayList<>();
            for( ResourceLocation regKey : getRegistry().getKeys() ) {
                if( regKey.toString().startsWith( NAMESPACE ) ) {
                    final T entry = getRegistry().getValue( regKey );
                    if( entry != null ) registryEntries.add( entry );
                }
            }
            if( registryEntries.isEmpty() ) {
                ConfigUtil.warnFor( FIELD );
                ConfigUtil.LOG.warn( "Environment entry did not match any namespaces in registry \"{}\"! Entry: {}",
                        getRegistry().getRegistryName(), this );
            }
            registryEntries = Collections.unmodifiableList( registryEntries );
        }
        return registryEntries;
    }
}