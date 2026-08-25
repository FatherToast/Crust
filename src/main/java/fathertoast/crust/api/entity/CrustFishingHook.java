package fathertoast.crust.api.entity;

import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * A common fishhook projectile to support usage by non-players (entities, block entities, etc.).
 * <p>
 * Supports simple "pull on hit" behavior only, not any actual fishing.
 * <p>
 * This baseline implementation is suitable for use by entity anglers - you must extend this class
 * and manually sync the non-entity angler in order for it to properly function for other things.
 *
 * @see IAngler
 * @see net.minecraft.world.entity.projectile.FishingHook
 */
public class CrustFishingHook extends Projectile implements IEntityAdditionalSpawnData {
    protected static final float DRAG_FACTOR = 0.92F;
    protected static final float GRAVITY_ACCEL = 0.03F;
    
    /** Convenience method for shooting baseline Crust fish hooks. */
    public static <T extends Entity & IAngler> boolean performRangedAttackFor( T angler, LivingEntity target ) {
        return performRangedAttackFor( angler, target, 1.0F );
    }
    
    /** Convenience method for shooting baseline Crust fish hooks. */
    public static <T extends Entity & IAngler> boolean performRangedAttackFor( T angler, LivingEntity target, float power ) {
        // noinspection resource
        return performRangedAttackFor( angler, target, power, 18 - 4 * angler.level().getDifficulty().getId() );
    }
    
    /** Convenience method for shooting baseline Crust fish hooks. */
    public static <T extends Entity & IAngler> boolean performRangedAttackFor( T angler, LivingEntity target, float power, float spread ) {
        final Vec3 offset = angler.getViewVector( 1.0F ).scale( angler.getBbWidth() );
        return shootFishHook( new CrustFishingHook( angler.level(), angler ),
                new Vec3( offset.x(), -0.125, offset.z() ), target, power, spread );
    }
    
    /** Convenience method for shooting custom fish hooks. */
    public static boolean shootFishHook( CrustFishingHook hook, Vec3 offset, LivingEntity target, float power, float spread ) {
        return shootFishHook( hook, offset, target.position()
                .add( 0.0, target.getBbHeight() * 0.3333, 0.0 ), power, spread );
    }
    
    /** Convenience method for shooting custom fish hooks. */
    public static boolean shootFishHook( CrustFishingHook hook, Vec3 offset, Vec3 target, float power, float spread ) {
        // Identify shooting position
        IAngler angler = hook.getAngler();
        if( angler == null ) return false; // We need an angler for this method
        final Vec3 anglerPos = angler.getAnglerPos();
        if( anglerPos == null ) return false; // Angler needs to provide a position
        
        // Apply initial position, velocity, and rotation
        final Vec3 hookPos = anglerPos.add( offset );
        hook.setPos( hookPos );
        final Vec3 shootVec = target.subtract( hookPos );
        hook.shoot( shootVec.x(), shootVec.y() + shootVec.horizontalDistance() * 0.2, shootVec.z(),
                angler.baseHookSpeed() * power, spread );
        
        // Place the hook in the world
        // noinspection resource
        if( hook.level().addFreshEntity( hook ) ) {
            hook.playCastSound();
            return true;
        }
        return false;
    }
    
    
    @Nullable
    private IAngler owner;
    private float power = 1.0F;
    
    /** Constructor used for the entity factory. */
    public CrustFishingHook( EntityType<? extends CrustFishingHook> type, Level level ) {
        super( type, level );
        noCulling = true;
    }
    
    /** Constructor intended for extending classes to call. */
    protected CrustFishingHook( EntityType<? extends CrustFishingHook> type, Level level, IAngler angler ) {
        this( type, level );
        setAngler( angler );
    }
    
    /** Constructor for using the fishhook implementation provided by Crust. Only suitable for anglers that are entities. */
    public <T extends Entity & IAngler> CrustFishingHook( Level level, T angler ) {
        this( CrustObjects.Entities.FISH_HOOK.get(), level, angler );
    }
    
    /** Called from the {@link Entity Entity.class} constructor to define data watcher variables. */
    @Override
    protected void defineSynchedData() { }
    
    /** @return True if this fishhook should be discarded if it has no angler. */
    protected boolean requiresAngler() { return true; }
    
    public void playCastSound() { playCastSound( 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F) ); }
    
    public void playCastSound( float volume, float pitch ) {
        playSound( SoundEvents.FISHING_BOBBER_THROW, volume, pitch );
    }
    
    public void playRetrieveSound() { playRetrieveSound( 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F) ); }
    
    public void playRetrieveSound( float volume, float pitch ) {
        playSound( SoundEvents.FISHING_BOBBER_RETRIEVE, volume, pitch );
    }
    
    /** Sets velocity and then re-orients to face in the same direction. */
    public void setDeltaMovementAndHeading( Vec3 v ) {
        setDeltaMovement( v );
        setHeading( v );
    }
    
    /** Re-orients to face in the direction of this hook's current velocity. */
    public void setHeading() { setHeading( getDeltaMovement() ); }
    
    /** Re-orients to face in the given direction. */
    public void setHeading( Vec3 dir ) {
        setYRot( (float) Mth.atan2( dir.x, dir.z ) * Mth.RAD_TO_DEG );
        setXRot( (float) Mth.atan2( dir.y, dir.horizontalDistance() ) * Mth.RAD_TO_DEG );
        yRotO = getYRot();
        xRotO = getXRot();
    }
    
    /** Called each world tick on the both the server and client side. */
    @Override
    public void tick() {
        super.tick();
        
        if( canContinue() ) tickFishing();
        else discard();
    }
    
    /** @return True if this fishhook can continue to exist. */
    @SuppressWarnings( "resource" )
    protected boolean canContinue() {
        IAngler angler = getAngler();
        if( angler == null ) return !requiresAngler();
        
        else return level().isClientSide || !angler.shouldStopFishing();
    }
    
    /** Called each tick on the server and client to update logic and perform physics. */
    protected void tickFishing() {
        // Check if we're in water
        BlockPos pos = blockPosition();
        // noinspection resource
        FluidState fluidState = level().getFluidState( pos );
        float waterLevel = fluidState.is( FluidTags.WATER ) ?
                fluidState.getHeight( level(), pos ) : 0.0F;
        boolean inWater = waterLevel > 0.0F;
        
        // Slow down faster when in water
        if( inWater ) {
            setDeltaMovement( getDeltaMovement().scale( DRAG_FACTOR ) );
        }
        
        // noinspection resource
        if( !level().isClientSide() ) checkCollision();
        
        // Apply gravity or buoyancy
        if( !isNoGravity() ) {
            setDeltaMovement( getDeltaMovement()
                    .add( 0.0, inWater ? 2 * GRAVITY_ACCEL : -GRAVITY_ACCEL, 0.0 ) );
        }
        
        // Run physics
        move( MoverType.SELF, getDeltaMovement() );
        updateRotation();
        setDeltaMovement( getDeltaMovement().scale( DRAG_FACTOR ) );
        reapplyPosition();
    }
    
    /** Called each tick on the server side to perform collision detection. */
    protected void checkCollision() {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector( this, this::canHitEntity );
        if( hit.getType() == HitResult.Type.MISS || !ForgeEventFactory.onProjectileImpact( this, hit ) ) {
            onHit( hit );
        }
    }
    
    /** @return True if the entity can be hit by this fishhook. */
    @Override
    protected boolean canHitEntity( Entity entity ) {
        return !entity.isSpectator() && (super.canHitEntity( entity ) || entity.isAlive() && entity instanceof ItemEntity);
    }
    
    /**
     * Called on the server side when an entity is hit.
     * You should override {@link #onHookHitEntity(EntityHitResult)} instead, if possible.
     */
    @Override
    protected void onHitEntity( EntityHitResult context ) {
        super.onHitEntity( context );
        onHookHitEntity( context );
    }
    
    /** Called on the server side when an entity is hit. */
    protected void onHookHitEntity( EntityHitResult context ) {
        pullEntity( context.getEntity() );
        discard();
    }
    
    /**
     * Called on the server side when a block is hit.
     * You should override {@link #onHookHitBlock(BlockHitResult)} instead, if possible.
     */
    @Override
    protected void onHitBlock( BlockHitResult context ) {
        super.onHitBlock( context );
        onHookHitBlock( context );
    }
    
    /** Called on the server side when a block is hit. */
    protected void onHookHitBlock( BlockHitResult context ) { discard(); }
    
    /** Pulls the entity. */
    protected void pullEntity( Entity entity ) {
        IAngler angler = getAngler();
        Vec3 pullVec;
        if( angler == null ) {
            pullVec = getOwnerlessPullVec().scale( 0.32F );
        }
        else {
            pullVec = (angler.getAnglerPos() == null ? getOwnerlessPullVec() :
                    angler.getAnglerPos().subtract( position() )).scale( angler.baseHookPull() );
            
            angler.damageRod( entity instanceof ItemEntity ? 3 : 5 );
            if( angler.asEntity() instanceof LivingEntity livingAngler )
                livingAngler.swing( IAngler.getRodHand( livingAngler ) );
        }
        
        if( !(entity instanceof Player player) || !player.isCreative() || !player.getAbilities().flying ) {
            entity.setDeltaMovement( entity.getDeltaMovement().scale( 0.2 ).add( pullVec.scale( getPower() ) )
                    .add( 0.0, entity.onGround() ? 0.32 : 0.0, 0.0 ) );
            entity.hurtMarked = true;
        }
        
        playRetrieveSound();
    }
    
    /** @return A pull vector to use in case no angler is present. Should never be called for hooks that require an angler. */
    protected Vec3 getOwnerlessPullVec() {
        // Pull vec for an angler 10 blocks horizontally backward from this hook, based on its current movement direction
        return getDeltaMovement().multiply( 1.0, 0.0, 1.0 )
                .normalize().scale( -10.0F );
    }
    
    /** Sets this fishhook's angler. */
    public void setAngler( @Nullable IAngler newAngler ) {
        // Remove self from existing angler
        IAngler oldAngler = getAngler();
        if( oldAngler != null && !oldAngler.equals( newAngler ) && equals( oldAngler.getHook() ) ) {
            oldAngler.setHook( null );
        }
        
        // Handle angler removal
        if( newAngler == null ) {
            owner = null;
            setOwner( null );
            return;
        }
        
        // Deconflict multiple fish hooks for a single angler
        if( newAngler.getHook() != null && !equals( newAngler.getHook() ) ) {
            // noinspection resource
            if( level().isClientSide() || newAngler.canReplaceHookWith( this ) ) {
                newAngler.getHook().setAngler( null );
            }
            else {
                setAngler( null );
                return;
            }
        }
        
        // Finally, set angler
        owner = newAngler;
        setOwner( newAngler.asEntity() );
        newAngler.setHook( this );
    }
    
    /** @return This fishhook's angler. */
    @Nullable
    public IAngler getAngler() { return owner; }
    
    /** Sets this fishhook's pull strength. */
    public void setPower( float newPower ) { power = newPower; }
    
    /** @return This fishhook's pull strength. */
    public float getPower() { return power; }
    
    @Override
    public void remove( RemovalReason reason ) {
        setAngler( null );
        super.remove( reason );
    }
    
    @Override
    public void onClientRemoval() { setAngler( null ); }
    
    @Override
    protected MovementEmission getMovementEmission() { return MovementEmission.NONE; }
    
    @Override
    public SoundSource getSoundSource() { return SoundSource.HOSTILE; }
    
    @Override
    public boolean canChangeDimensions() { return false; }
    
    @Override
    public boolean shouldRenderAtSqrDistance( double sqrDistance ) { return sqrDistance < 4096.0; /* 64^2 */ }
    
    @Override
    public void lerpTo( double x, double y, double z, float yRot, float xRot, int ignoredSteps, boolean teleport ) { }
    
    
    // Default data synchronization; only works for anglers that are entities
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket( this ); }
    
    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param additionalData The packet data stream
     */
    @Override // IEntityAdditionalSpawnData
    public void writeSpawnData( FriendlyByteBuf additionalData ) {
        final Entity owner = getOwner();
        additionalData.writeInt( owner == null ? 0 : owner.getId() );
    }
    
    /**
     * Called by the client when it receives an entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    @Override // IEntityAdditionalSpawnData
    public void readSpawnData( FriendlyByteBuf additionalData ) {
        // noinspection resource
        final Entity entity = level().getEntity( additionalData.readInt() );
        if( entity instanceof IAngler angler ) setAngler( angler );
    }
}