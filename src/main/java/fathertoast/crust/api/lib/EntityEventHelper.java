package fathertoast.crust.api.lib;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Entity events. Sent from the server using {@link Level#broadcastEntityEvent(Entity, byte)}, then
 * executed on the client via {@link Entity#handleEntityEvent(byte)}.
 * <p>
 * This only contains event codes for Entity and LivingEntity. Any entity subclass can introduce its own
 * event codes; mod-added entities may even completely change or overwrite superclass events.
 *
 * @see LivingEntity#handleEntityEvent(byte)
 */
// Note: There's no need to go through MobEntity, as its sole event is already nicely abstracted (#spawnAnim())
//      If we want to go deeper than living entity, it may be wise to make this generic to only allow an appropriate subclass (would be messy).
@SuppressWarnings( "unused" )
public enum EntityEventHelper {
    
    // The various "hurt animations" only differ by sound effect played
    /**
     * Previously played the hurt animation and sound. Triggered the hurt event with {@link net.minecraft.world.damagesource.DamageSources#generic()} and 0 damage.
     * Now, does nothing. Instead, use {@link Level#broadcastDamageEvent(Entity, DamageSource)}.
     */
    @Deprecated( forRemoval = true )
    HURT_ANIM( 2 ),
    /**
     * Previously played the hurt animation and sound, plus thorns sound. Triggered the hurt event with {@link net.minecraft.world.damagesource.DamageSources#generic()} and 0 damage.
     * Now, does nothing. Instead, use {@link Level#broadcastDamageEvent(Entity, DamageSource)}.
     */
    @Deprecated( forRemoval = true )
    HURT_ANIM_THORNS( 33 ),
    /**
     * Previously played the hurt animation and drown sound. Triggered the hurt event with {@link net.minecraft.world.damagesource.DamageSources#generic()} 0 damage.
     * Now, does nothing. Instead, use {@link Level#broadcastDamageEvent(Entity, DamageSource)}.
     */
    @Deprecated( forRemoval = true )
    HURT_ANIM_DROWN( 36 ),
    /**
     * Previously played the hurt animation and burn sound. Triggered the hurt event with {@link net.minecraft.world.damagesource.DamageSources#generic()} and 0 damage.
     * Now, does nothing. Instead, use {@link Level#broadcastDamageEvent(Entity, DamageSource)}.
     */
    @Deprecated( forRemoval = true )
    HURT_ANIM_BURNING( 37 ),
    /**
     * Previously played the hurt animation and berry bush sound. Triggered the hurt event with {@link net.minecraft.world.damagesource.DamageSources#generic()} and 0 damage.
     * Now, does nothing. Instead, use {@link Level#broadcastDamageEvent(Entity, DamageSource)}.
     */
    @Deprecated( forRemoval = true )
    HURT_ANIM_SWEET_BERRY_BUSH( 44 ),
    
    /**
     * Plays the death sound, if any. For non-player entities, also sets health to 0 (which starts the death animation)
     * and triggers the die event with {@link net.minecraft.world.damagesource.DamageSources#generic()}.
     */
    DEATH_ANIM( 3 ),
    
    /** Plays the "shield block" sound effect. */
    SHIELD_BLOCK_SOUND( 29 ),
    /** Plays the "shield break" sound effect. Does NOT play the break animation. */
    SHIELD_BREAK_SOUND( 30 ),
    
    /** Spawns portal particles in a line between current position and last tick position. */
    TELEPORT_TRAIL_PARTICLES( 46 ),
    /** Spawns honey block particles via {@link net.minecraft.world.level.block.HoneyBlock#showSlideParticles(Entity)}. */
    HONEY_SLIDE_PARTICLES( 53 ), // Note: This is actually the only event defined in Entity
    /** Spawns honey block particles via {@link net.minecraft.world.level.block.HoneyBlock#showJumpParticles(Entity)}. */
    HONEY_JUMP_PARTICLES( 54 ),
    /** Spawns poof particles via {@link LivingEntity#makePoofParticles()} (called at the end of the death animation). */
    POOF_PARTICLES( 60 ),
    
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)} or {@link LivingEntity#broadcastBreakEvent(InteractionHand)}.
     */
    ITEM_BREAK_FX_MAIN_HAND( 47 ),
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)} or {@link LivingEntity#broadcastBreakEvent(InteractionHand)}.
     */
    ITEM_BREAK_FX_OFF_HAND( 48 ),
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)}.
     */
    ITEM_BREAK_FX_HEAD( 49 ),
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)}.
     */
    ITEM_BREAK_FX_CHEST( 50 ),
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)}.
     */
    ITEM_BREAK_FX_LEGS( 51 ),
    /**
     * Spawns item particles and plays the break sound as applicable. Consider using the built-in hook
     * {@link LivingEntity#broadcastBreakEvent(EquipmentSlot)}.
     */
    ITEM_BREAK_FX_FEET( 52 ),
    
    /** Swaps the items equipped in the main and offhand. Appears to be unused; equipment is already tracked by the client. */
    SWAP_HAND_ITEMS( 55 );
    
    
    public final byte ID;
    
    EntityEventHelper( int id ) { ID = (byte) id; }
    
    /** Sends this event from the given server entity to its client-sided counterpart. */
    public void broadcast( LivingEntity entity ) {
        // noinspection resource
        entity.level().broadcastEntityEvent( entity, ID );
    }
}