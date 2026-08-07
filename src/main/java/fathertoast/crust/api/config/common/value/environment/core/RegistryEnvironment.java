package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @see RegObjKey
 * @see IRegWrapper
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraft.core.registries.BuiltInRegistries
 */
public abstract class RegistryEnvironment<T> extends InvertibleEnvironment {
    
    /** The registry object key for this environment. */
    protected final RegObjKey<T> KEY;
    
    public RegistryEnvironment( RegObjKey<T> regKey, boolean invert ) {
        super( invert );
        KEY = regKey;
        if( regKey.isBlacklist() )
            throw new IllegalArgumentException( "Key cannot be a blacklist type!" );
        if( !getRegistry().equals( regKey.registry() ) )
            throw new IllegalArgumentException( "Key must belong to the environment's used registry!" );
    }
    
    public RegistryEnvironment( @Nullable IConfigField<?> field, String value ) {
        super( value );
        FuzzyKey<T> loadedKey = getRegistry().getParser().parseKeyString( field, value, valueCleaned( value ), false );
        KEY = loadedKey instanceof RegObjKey<T> rok ? rok :
                getDefaultValue(); // Warnings handled decently enough by parser
    }
    
    /** @return The registry used. */
    public abstract IRegWrapper<T> getRegistry();
    
    /** @return The fallback registry key to use if parsing fails. */
    public RegObjKey<T> getDefaultValue() {
        return RegObjKey.of( getRegistry(), ResourceLocation.withDefaultNamespace( "parse_error" ), false );
    }
    
    /** @return The string value of this environment, without the invert code. */
    @Override
    protected String cleanValue() { return KEY.toTomlString(); }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    @Override
    protected boolean cleanTest( EnvironmentContext context ) {
        T actual = getActual( context );
        return actual != null && KEY.matches( actual );
    }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Nullable
    protected abstract T getActual( EnvironmentContext context );
}