package fathertoast.crust.common.compat.naturalabsorption;

import fathertoast.naturalabsorption.api.INaturalAbsorption;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class NaturalAbsorptionPlugin {
    
    public static final Function<INaturalAbsorption, Void> RECEIVER = ( apiInstance ) -> {
        API_INSTANCE = apiInstance;
        return null;
    };
    
    public static INaturalAbsorption API_INSTANCE;
    
    /**
     * @return The entity's max absorption, from all sources combined.
     * In other words, the actual limit on absorption recovery.
     */
    public static double getMaxAbsorption( LivingEntity entity ) {
        return API_INSTANCE.getAbsorptionAccessor().getMaxAbsorption( entity );
    }
}
