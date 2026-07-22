package fathertoast.crust.api.lib;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@SuppressWarnings( "unused" )
public final class CrustEntityHelper {
    
    /** @return An entity type builder populated with the default settings for a monster. */
    public static <T extends Entity> EntityType.Builder<T> monsterType( EntityType.EntityFactory<T> factory, float width, float height ) {
        return mobType( factory, MobCategory.MONSTER, width, height );
    }
    
    /** @return An entity type builder populated with the default settings for a creature. (Note: many have a tracking range of 10) */
    public static <T extends Entity> EntityType.Builder<T> creatureType( EntityType.EntityFactory<T> factory, float width, float height ) {
        return mobType( factory, MobCategory.CREATURE, width, height );
    }
    
    /** @return An entity type builder populated with the default settings for a water creature. */
    public static <T extends Entity> EntityType.Builder<T> waterCreatureType( EntityType.EntityFactory<T> factory, float width, float height ) {
        return mobType( factory, MobCategory.WATER_CREATURE, width, height );
    }
    
    /** @return An entity type builder populated with the default settings for an ambient mob. */
    public static <T extends Entity> EntityType.Builder<T> ambientType( EntityType.EntityFactory<T> factory, float width, float height ) {
        return mobType( factory, MobCategory.AMBIENT, width, height ).clientTrackingRange( 5 );
    }
    
    /** @return An entity type builder populated with the default settings for an ambient water mob. */
    public static <T extends Entity> EntityType.Builder<T> waterAmbientType( EntityType.EntityFactory<T> factory, float width, float height ) {
        return mobType( factory, MobCategory.WATER_AMBIENT, width, height ).clientTrackingRange( 4 );
    }
    
    /** @return An entity type builder populated with the default settings for a mob. */
    public static <T extends Entity> EntityType.Builder<T> mobType( EntityType.EntityFactory<T> factory, MobCategory category, float width, float height ) {
        return EntityType.Builder.of( factory, category )
                .sized( width, height ).clientTrackingRange( 8 );
    }
    
    /** @return An entity type builder populated with the default settings for a fish hook. */
    public static <T extends Entity> EntityType.Builder<T> fishHookType( EntityType.EntityFactory<T> factory ) {
        return EntityType.Builder.of( factory, MobCategory.MISC ).noSave().noSummon()
                .sized( 0.25F, 0.25F ).clientTrackingRange( 4 ).updateInterval( 5 );
    }
    
    
    // Utility class
    private CrustEntityHelper() { }
}