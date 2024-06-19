package fathertoast.crust.api.config.common.value.environment.biome;


import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

/**
 * Used to wrap the vanilla enum Biome.Category so that it can be safely used in configs.
 * The declared names should match the string passed into vanilla enums' constructors so that both enums serialize identically.
 */
@SuppressWarnings("ConstantConditions")
public enum BiomeCategory {

    NONE( null ),
    TAIGA( BiomeTags.IS_TAIGA ),
    HILLS( BiomeTags.IS_HILL ),
    JUNGLE( BiomeTags.IS_JUNGLE ),
    BADLANDS( BiomeTags.IS_BADLANDS ),
    PLAINS( Tags.Biomes.IS_PLAINS ),
    SAVANNA( BiomeTags.IS_SAVANNA ),
    ICY( Tags.Biomes.IS_COLD_OVERWORLD ),
    THE_END( BiomeTags.IS_END ),
    BEACH( BiomeTags.IS_BEACH ),
    FOREST( BiomeTags.IS_FOREST ),
    OCEAN( BiomeTags.IS_OCEAN ),
    DESERT( Tags.Biomes.IS_DESERT ),
    RIVER( BiomeTags.IS_RIVER ),
    SWAMP( Tags.Biomes.IS_SWAMP ),
    MUSHROOM( Tags.Biomes.IS_MUSHROOM ),
    NETHER( BiomeTags.IS_NETHER );

    public final TagKey<Biome> BIOME_TAG;
    
    BiomeCategory( TagKey<Biome> biomeTag ) { BIOME_TAG = biomeTag; }
}