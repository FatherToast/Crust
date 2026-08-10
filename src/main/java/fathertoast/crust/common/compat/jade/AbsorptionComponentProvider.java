package fathertoast.crust.common.compat.jade;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.compat.jade.element.AbsorptionElement;
import fathertoast.crust.common.compat.naturalabsorption.NaturalAbsorptionPlugin;
import fathertoast.crust.common.core.Crust;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/**
 * An entity component that displays the entity's current absorption amount.
 */
public class AbsorptionComponentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    
    /** NBT keys used by this provider to read and write custom data. */
    private static final String TAG_CRUST_DATA = "CrustData";
    private static final String TAG_ABSORPTION = "Absorption";
    
    
    /**
     * Callback used to add render-able elements to the tooltip and modify existing elements to the tooltip.
     * <br><br>
     * Will only be called if the implementing class is registered via {@link IWailaClientRegistration#registerEntityComponent(IEntityComponentProvider, Class)}.</br>
     * <p>
     * This method is only called on the client side. If you require data from the server, you should also implement
     * {@link IServerDataProvider#appendServerData(CompoundTag, Accessor)}
     * and add the data to the {@link CompoundTag} there, which can then be read back using {@link Accessor#getServerData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     * </p>
     *
     * @param tooltip   Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
     * @param accessor  Contains most of the relevant information about the current environment.
     * @param cfgAccess Current configuration of Waila.
     */
    @Override
    public void appendTooltip( ITooltip tooltip, EntityAccessor accessor, IPluginConfig cfgAccess ) {
        if( accessor.getEntity() instanceof LivingEntity livingEntity ) {
            final CompoundTag tag = accessor.getServerData();
            
            if( NBTHelper.containsCompound( tag, TAG_CRUST_DATA ) && NBTHelper.containsNumber( tag.getCompound( TAG_CRUST_DATA ), TAG_ABSORPTION ) ) {
                final float absorptionAmount = tag.getCompound( TAG_CRUST_DATA ).getFloat( TAG_ABSORPTION );
                final float absorptionCapacity;
                
                // If Natural Absorption is installed, we fetch the entity's absorption capacity
                // from the API. Otherwise, we just set the capacity to the same value as current absorption.
                if( Crust.NA_INSTALLED && cfgAccess.get( CrustJadePlugin.Config.ENTITY_ABSORPTION_SHOW_CAPACITY ) ) {
                    absorptionCapacity = (float) NaturalAbsorptionPlugin.getMaxAbsorption( livingEntity );
                }
                else {
                    absorptionCapacity = tag.getCompound( TAG_CRUST_DATA ).getFloat( TAG_ABSORPTION );
                }
                tooltip.add( new AbsorptionElement( cfgAccess, absorptionAmount, absorptionCapacity ) );
            }
        }
    }
    
    /**
     * Callback used server side to send custom NBT to the client.
     * <br><br>
     * Will only be called if the implementing class is registered via {@link IWailaCommonRegistration#registerBlockDataProvider}
     * or {@link IWailaCommonRegistration#registerEntityDataProvider}.</br>
     *
     * @param data     Current synchronization tag (might have been processed by other providers and might be processed by other providers).
     * @param accessor Contains the relevant information about the current environment.
     */
    @Override
    public void appendServerData( CompoundTag data, EntityAccessor accessor ) {
        if( accessor.getEntity() instanceof LivingEntity livingEntity ) {
            if( livingEntity.getAbsorptionAmount() > 0.0 ) {
                CompoundTag modData = NBTHelper.getOrCreateCompound( data, TAG_CRUST_DATA );
                modData.putFloat( TAG_ABSORPTION, livingEntity.getAbsorptionAmount() );
            }
        }
    }
    
    /** @return The unique id of this provider. Providers from different registries can have the same id. */
    @Override
    public ResourceLocation getUid() {
        return CrustJadePlugin.Config.ENTITY_ABSORPTION;
    }
    
    /** @return Whether this component is enabled by default. */
    @Override
    public boolean enabledByDefault() {
        return Crust.NA_INSTALLED;
    }
    
    /**
     * Affects the display order showing in the tooltip.
     * <br><br>
     * If you want to show your tooltip a bit to the bottom, you should return a value greater than 0, and less than 5000.
     * If it is greater than 5000, the content will not be collapsed in lite mode.
     */
    @Override
    public int getDefaultPriority() {
        // Jade's health provider's default priority is -4501.
        return -4502;
    }
}
