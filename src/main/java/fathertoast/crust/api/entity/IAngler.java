package fathertoast.crust.api.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

/**
 * Represents something that exists in a world/level (e.g., and entity or block entity)
 * that can cast Crust fishhook projectiles.
 * <p>
 * You may need to handle some client data synchronization yourself.
 *
 * @see CrustFishingHook
 */
public interface IAngler {
    
    /** @return This angler's active fishing hook. */
    @Nullable
    CrustFishingHook getHook();
    
    /** Sets this angler's active fishing hook. */
    void setHook( @Nullable CrustFishingHook newHook );
    
    /** @return The entity this angler represents, or null if there is none. */
    @Nullable
    Entity asEntity();
    
    
    /** @return Whether this angler's line is out. */
    default boolean isLineOut() { return getHook() != null; } // Used to animate held fishing rod model on client
    
    /**
     * @return The position of this angler, or null if there is none.
     * It is recommended that you override this and always provide a non-null value.
     */
    @Nullable
    default Vec3 getAnglerPos() {
        Entity entityAngler = asEntity();
        if( entityAngler != null ) {
            return entityAngler.getEyePosition();
        }
        if( this instanceof BlockEntity blockEntityAngler ) {
            return Vec3.atCenterOf( blockEntityAngler.getBlockPos() );
        }
        return null;
    }
    
    /**
     * @return The position on this angler that the fishing line should attach to, or null if there is no line.
     * It is recommended that you override this and always provide a non-null value if you want a line rendered.
     */
    @Nullable
    default Vec3 getLinePos( float partialTick ) {
        Entity entityAngler = asEntity();
        if( entityAngler != null ) {
            return entityAngler instanceof LivingEntity livingEntityAngler ? getBipedLinePos( livingEntityAngler, partialTick ) :
                    entityAngler.getEyePosition( partialTick );
        }
        if( this instanceof BlockEntity blockEntityAngler ) {
            return Vec3.atCenterOf( blockEntityAngler.getBlockPos() );
        }
        return null;
    }
    
    /** @return True if the active fishhook can be replaced by a new one. */
    default boolean canReplaceHookWith( @Nullable CrustFishingHook newHook ) { return true; }
    
    /** Called when the hook thinks the rod should be damaged (e.g., after pulling a target). */
    default void damageRod( int damage ) { } // By default, does not damage any item
    
    /**
     * Called each tick on the server side while {@link #getHook()} is non-null.
     *
     * @return True if the active fishhook should be destroyed, false otherwise.
     */
    default boolean shouldStopFishing() {
        if( getHook() == null ) return true;
        Entity entityAngler = asEntity();
        if( entityAngler != null && !entityAngler.isAlive() ) return true;
        if( this instanceof BlockEntity blockEntityAngler && blockEntityAngler.isRemoved() ) return true;
        Vec3 linePos = getLinePos( 1.0F );
        return linePos != null && (maxLineDistSqr() <= 0.0F || getHook().distanceToSqr( linePos ) > maxLineDistSqr());
    }
    
    /** @return The base initial speed for fish hooks cast by this angler. */
    default float baseHookSpeed() { return 1.0F; }
    
    /** @return The base pull strength for fish hooks cast by this angler. */
    default float baseHookPull() { return 0.32F; }
    
    /** @return The maximum fishing line distance, or <= 0 if unlimited. */
    default float maxLineDistSqr() { return 1024.0F; /* 32^2 */ }
    
    
    // ---- Static Helper Methods ---- //
    
    /** @return The hand that should be used to fish with, or null if neither fits. */
    @Nullable
    static InteractionHand tryGetRodHand( LivingEntity angler ) {
        ItemStack mainHandItem = angler.getMainHandItem();
        if( mainHandItem.canPerformAction( ToolActions.FISHING_ROD_CAST ) ) return InteractionHand.MAIN_HAND;
        ItemStack offHandItem = angler.getOffhandItem();
        if( offHandItem.canPerformAction( ToolActions.FISHING_ROD_CAST ) ) return InteractionHand.OFF_HAND;
        return null;
    }
    
    /** @return The hand that should be used to fish with. */
    static InteractionHand getRodHand( LivingEntity angler ) {
        InteractionHand hand = tryGetRodHand( angler );
        return hand == null ? InteractionHand.MAIN_HAND : hand;
    }
    
    /** @return The standard position for a bipedal angler's fishing rod tip. */
    static Vec3 getBipedLinePos( LivingEntity angler, float partialTick ) {
        // Decide which hand is holding a rod, if any
        final InteractionHand rodHand = tryGetRodHand( angler );
        final int handedness = rodHand == null ? 0 :
                (rodHand == InteractionHand.MAIN_HAND ? 1 : -1) * (angler.getMainArm() == HumanoidArm.RIGHT ? 1 : -1);
        
        // Local space offsets - Note: might be some room to make this work better on scaled entities
        final double forwardOffset = 0.5;
        final double rightwardOffset = handedness * 0.35;
        final double upwardOffset = angler.getEyeHeight() + 0.44 - (angler.isCrouching() ? 0.1875F : 0.0F);
        
        // Conversions for local space to global space
        final float yRot = Mth.lerp( partialTick, angler.yBodyRotO, angler.yBodyRot ) * Mth.DEG_TO_RAD;
        final double forwardX = Mth.sin( yRot );
        final double forwardZ = Mth.cos( yRot );
        
        return new Vec3(
                Mth.lerp( partialTick, angler.xo, angler.getX() ) - forwardX * forwardOffset - forwardZ * rightwardOffset,
                Mth.lerp( partialTick, angler.yo, angler.getY() ) + upwardOffset,
                Mth.lerp( partialTick, angler.zo, angler.getZ() ) + forwardZ * forwardOffset - forwardX * rightwardOffset
        );
    }
}