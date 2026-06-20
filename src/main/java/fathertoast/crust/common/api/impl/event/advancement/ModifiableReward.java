package fathertoast.crust.common.api.impl.event.advancement;

import fathertoast.crust.api.event.advancement.IModifiableReward;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The default implementation of {@link IModifiableReward} */
public final class ModifiableReward implements IModifiableReward {
    
    private int exp;
    private final List<ResourceLocation> lootTables;
    private final List<ResourceLocation> unlockedRecipes;
    private ResourceLocation functionId;
    
    
    /**
     * @return A new ModifiableReward instance with all the
     * reward data from the specified AdvancementRewards object copied over.
     */
    public static ModifiableReward copyFrom( AdvancementRewards rewards ) {
        return new ModifiableReward( rewards );
    }
    
    /**
     * @return A new AdvancementRewards instance with all the
     * reward data from the specified ModifiableReward object copied over.
     */
    public static AdvancementRewards convertToVanilla( IModifiableReward modifiableReward ) {
        return new AdvancementRewards(
                modifiableReward.getExp(),
                modifiableReward.getLootTables().toArray( new ResourceLocation[0] ),
                modifiableReward.getUnlockedRecipes().toArray( new ResourceLocation[0] ),
                new CommandFunction.CacheableFunction( modifiableReward.getFunctionId() )
        );
    }
    
    private ModifiableReward( AdvancementRewards rewards ) {
        this( rewards.experience, rewards.loot, rewards.recipes, rewards.function.getId() );
    }
    
    private ModifiableReward( int exp, ResourceLocation[] lootTables, ResourceLocation[] unlockedRecipes, @Nullable ResourceLocation functionId ) {
        this.exp = exp;
        this.lootTables = new ArrayList<>( Arrays.asList( lootTables ) );
        this.unlockedRecipes = new ArrayList<>( Arrays.asList( unlockedRecipes ) );
        this.functionId = functionId;
    }
    
    @Override
    public int getExp() {
        return exp;
    }
    
    @Override
    public void setExp( int exp ) {
        this.exp = exp;
    }
    
    @Override
    public void addExp( int exp ) {
        this.exp += exp;
    }
    
    @Override
    public List<ResourceLocation> getLootTables() {
        return lootTables;
    }
    
    @Override
    public List<ResourceLocation> getUnlockedRecipes() {
        return unlockedRecipes;
    }
    
    @Override
    @Nullable
    public ResourceLocation getFunctionId() {
        return functionId;
    }
    
    @Override
    public void setFunctionId( @Nullable ResourceLocation functionId ) {
        this.functionId = functionId;
    }
}
