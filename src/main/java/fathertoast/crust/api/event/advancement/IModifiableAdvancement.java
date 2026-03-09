package fathertoast.crust.api.event.advancement;


import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Helper interface for modifying advancements when
 * they are loaded from JSON.
 * <br>
 * Used in {@link AdvancementLoadEvent}.
 */
public interface IModifiableAdvancement {
    
    /**
     * @return True if the advancement should send a telemetry data event.
     * Usually indicates that the advancement is originally a recipe-unlock advancement.
     */
    boolean sendsTelemetryEvent();
    
    /** @return The ID of the underlying advancement's parent, or null if no parent exists. */
    @Nullable
    ResourceLocation getParentId();
    
    /** Sets the parent for the advancement being built. Setting this to null is safe. */
    void setParentId( @Nullable ResourceLocation parentId );
    
    /**
     * @return A modifiable view of the display info for the advancement being built, if it exists.
     * Creates and returns fresh, blank display info otherwise.
     */
    IModifiableDisplayInfo getOrCreateDisplayInfo();
    
    /** Sets this advancement's display info to null. */
    void noDisplayInfo();
    
    /** @return A modifiable view of the reward properties for the advancement being built. */
    IModifiableReward getReward();
    
    /**
     * Adds the specified criterion to the underlying advancement's map of criteria.
     *
     * @param key      The identifying key for the criterion trigger instance.
     * @param trigger  The criterion trigger instance to add.
     * @param override If true and a criterion with the same key already exists in the map,
     *                 it will be replaced with this one.
     */
    void addCriterion( String key, CriterionTriggerInstance trigger, boolean override );
    
    /**
     * Removes the criterion mapped to the specified key from the underlying advancement's map of criteria, if it exists.
     */
    void removeCriterion( String key );
    
    /**
     * @return An unmodifiable view of the underlying advancement's criteria.
     * @see IModifiableAdvancement#addCriterion(String, CriterionTriggerInstance, boolean)
     * @see IModifiableAdvancement#removeCriterion(String)
     */
    Map<String, Criterion> getCriteria();
    
    /**
     * @return The requirements strategy type to be used when building
     * the underlying advancement's requirements.
     */
    RequirementsStrategy getRequirementsStrategy();
    
    /**
     * Sets the requirements strategy type to be used when building
     * the underlying advancement's requirements. Must be either
     * {@link RequirementsStrategy#AND} or {@link RequirementsStrategy#OR}.
     */
    void setRequirementsStrategy( RequirementsStrategy strategy );
}
