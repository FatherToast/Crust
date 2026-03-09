package fathertoast.crust.api.event.advancement;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helper interface for modifying an advancement's rewards.
 * <br><br>
 * Used in {@link IModifiableAdvancement}.
 */
public interface IModifiableReward {
    
    /** @return The total amount of experience granted by this reward. */
    int getExp();
    
    /** Sets the total amount of experience granted by this reward. */
    void setExp( int exp );
    
    /** Adds the specified amount of experience to this reward. */
    void addExp( int exp );
    
    
    /** @return A modifiable list of IDs for the loot tables that will be dropped by this reward. */
    List<ResourceLocation> getLootTables();
    
    
    /** @return A modifiable list of IDs for the recipes that will be unlocked by this reward. */
    List<ResourceLocation> getUnlockedRecipes();
    
    
    /**
     * @return The ID of the command function to be executed when this reward is obtained.
     */
    @Nullable
    ResourceLocation getFunctionId();
    
    /**
     * Sets the ID of the command function to be executed when this reward is obtained.
     * Setting this to null is safe.
     */
    void setFunctionId( @Nullable ResourceLocation id );
}
