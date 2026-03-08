package fathertoast.crust.common.api.impl.event;

import com.google.common.collect.ImmutableMap;
import fathertoast.crust.api.advancement.IModifiableAdvancement;
import fathertoast.crust.api.advancement.IModifiableDisplayInfo;
import fathertoast.crust.api.advancement.IModifiableReward;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/** The default implementation of {@link IModifiableAdvancement} */
public class ModifiableAdvancement implements IModifiableAdvancement {
    
    private final boolean sendsTelemetryEvent;
    private final ModifiableReward reward;
    private final Map<String, Criterion> criteria;
    
    @Nullable
    private ResourceLocation parentId;
    @Nullable
    private IModifiableDisplayInfo displayInfo;
    
    private RequirementsStrategy requirementsStrategy;
    
    
    /**
     * @return A new ModifiableAdvancement instance with all the
     * advancement data from the specified builder copied over.
     */
    public static ModifiableAdvancement copyFromBuilder( Advancement.Builder builder ) {
        return new ModifiableAdvancement( builder );
    }
    
    /**
     * Converts the given ModifiableAdvancement into a new
     * {@link Advancement.Builder} instance with the same data.
     */
    public static Advancement.Builder convertToVanilla( ModifiableAdvancement modifiable ) {
        // noinspection ConstantConditions
        Advancement.Builder builder = new Advancement.Builder(
                modifiable.getParentId(),
                ModifiableDisplayInfo.convertToVanilla( modifiable.getDisplayInfo() ),
                ModifiableReward.convertToVanilla( modifiable.getReward() ),
                modifiable.getCriteria(),
                null,
                modifiable.sendsTelemetryEvent()
        );
        return builder.requirements( modifiable.getRequirementsStrategy() );
    }
    
    
    private ModifiableAdvancement( boolean sendsTelemetryEvent, @Nullable ResourceLocation parentId,
                                   @Nullable DisplayInfo displayInfo, AdvancementRewards rewards,
                                   Map<String, Criterion> criteria, RequirementsStrategy requirementsStrategy ) {
        this.sendsTelemetryEvent = sendsTelemetryEvent;
        this.parentId = parentId;
        this.criteria = criteria;
        this.requirementsStrategy = requirementsStrategy;
        this.displayInfo = ModifiableDisplayInfo.copyFrom( displayInfo );
        this.reward = ModifiableReward.copyFrom( rewards );
    }
    
    private ModifiableAdvancement( Advancement.Builder builder ) {
        // noinspection ConstantConditions
        this( builder.sendsTelemetryEvent, builder.parentId, builder.display,
                builder.rewards, builder.criteria, getStrategyFor( builder.requirements ) );
    }
    
    
    @Override
    public boolean sendsTelemetryEvent() {
        return sendsTelemetryEvent;
    }
    
    @Override
    @Nullable
    public ResourceLocation getParentId() {
        return parentId;
    }
    
    @Override
    public void setParentId( @Nullable ResourceLocation parentId ) {
        this.parentId = parentId;
    }
    
    @Override
    public IModifiableDisplayInfo getOrCreateDisplayInfo() {
        if( displayInfo == null ) {
            displayInfo = new ModifiableDisplayInfo();
        }
        return displayInfo;
    }
    
    @Override
    public void noDisplayInfo() {
        displayInfo = null;
    }
    
    @Nullable
    public IModifiableDisplayInfo getDisplayInfo() {
        return displayInfo;
    }
    
    @Override
    public IModifiableReward getReward() {
        return reward;
    }
    
    @Override
    public void addCriterion( String key, CriterionTriggerInstance trigger, boolean override ) {
        Objects.requireNonNull( key );
        Objects.requireNonNull( trigger );
        
        if( override || !criteria.containsKey( key ) ) {
            criteria.put( key, new Criterion( trigger ) );
        }
        else {
            throw new IllegalArgumentException( "Duplicate criterion " + key );
        }
    }
    
    @Override
    public void removeCriterion( String key ) {
        criteria.remove( key );
    }
    
    @Override
    public Map<String, Criterion> getCriteria() {
        return ImmutableMap.copyOf( criteria );
    }
    
    @Override
    public RequirementsStrategy getRequirementsStrategy() {
        return requirementsStrategy;
    }
    
    @Override
    public void setRequirementsStrategy( RequirementsStrategy requirementsStrategy ) {
        Objects.requireNonNull( requirementsStrategy );
        this.requirementsStrategy = requirementsStrategy;
    }
    
    
    /**
     * Checks the array length(s) of the given requirements
     * to determine what type of {@link RequirementsStrategy} was
     * used.
     *
     * @return The likely fitting {@link RequirementsStrategy}.
     */
    private static RequirementsStrategy getStrategyFor( String[][] requirements ) {
        Objects.requireNonNull( requirements );
        // Single dimension array with more than one String
        // indicates OR strategy.
        if( requirements.length == 1 && requirements[0].length > 1 )
            return RequirementsStrategy.OR;
        return RequirementsStrategy.AND;
    }
}
