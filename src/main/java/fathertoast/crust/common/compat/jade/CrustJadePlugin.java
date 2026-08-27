package fathertoast.crust.common.compat.jade;

import fathertoast.crust.common.compat.jade.provider.AbsorptionComponentProvider;
import fathertoast.crust.common.core.Crust;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class CrustJadePlugin implements IWailaPlugin {
    
    private static final AbsorptionComponentProvider ABSORPTION_PROVIDER = new AbsorptionComponentProvider();
    
    
    @Override
    public void register( IWailaCommonRegistration reg ) {
        reg.registerEntityDataProvider( ABSORPTION_PROVIDER, LivingEntity.class );
    }
    
    @Override
    public void registerClient( IWailaClientRegistration reg ) {
        reg.registerEntityComponent( ABSORPTION_PROVIDER, LivingEntity.class );
        addConfigEntries( reg );
    }
    
    /** Defines this plugin's Jade config entries. */
    private void addConfigEntries( IWailaClientRegistration reg ) {
        reg.addConfig( Config.ENTITY_MAX_ABSORPTION_FOR_RENDER, 20.0F, 1.0F, 40.0F, false );
        reg.addConfig( Config.ENTITY_ABSORPTION_ICONS_PER_LINE, 10, 1, 10, false );
        reg.addConfig( Config.ENTITY_ABSORPTION_SHOW_CAPACITY, true );
    }
    
    
    /** Contains the IDs of every Jade config entry added by the Crust plugin. */
    public interface Config {
        ResourceLocation ENTITY_ABSORPTION = id( "entity_absorption" );
        ResourceLocation ENTITY_MAX_ABSORPTION_FOR_RENDER = id( ENTITY_ABSORPTION, "max_for_render" );
        ResourceLocation ENTITY_ABSORPTION_ICONS_PER_LINE = id( ENTITY_ABSORPTION, "icons_per_line" );
        ResourceLocation ENTITY_ABSORPTION_SHOW_CAPACITY = id( ENTITY_ABSORPTION, "show_capacity" );
        
        
        /** Convenience method for creating a resource location with the Crust namespace. */
        static ResourceLocation id( String path ) {
            return Crust.rl( path );
        }
        
        /**
         * Convenience method for creating a resource location with the Crust namespace.
         *
         * @param parent A resource location whose path should be used as the base path.
         */
        static ResourceLocation id( ResourceLocation parent, String path ) {
            return id( parent.getPath() + "." + path );
        }
    }
}
