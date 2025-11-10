package fathertoast.crust.test.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class TestRangedAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private boolean isShooting;
    
    public TestRangedAttackGoal( T entity, double walkSpeed, int minInterval, float range ) {
        mob = entity;
        speedModifier = walkSpeed;
        attackIntervalMin = minInterval;
        attackRadiusSqr = range * range;
        setFlags( EnumSet.of( Goal.Flag.MOVE, Goal.Flag.LOOK ) );
    }
    
    public void setMinAttackInterval( int minInterval ) { attackIntervalMin = minInterval; }
    
    public boolean canUse() { return mob.getTarget() != null; }
    
    public boolean canContinueToUse() { return canUse() || !mob.getNavigation().isDone(); }
    
    public void start() {
        super.start();
        mob.setAggressive( true );
    }
    
    public void stop() {
        super.stop();
        mob.setAggressive( false );
        seeTime = 0;
        attackTime = -1;
        isShooting = false;
    }
    
    public boolean requiresUpdateEveryTick() { return true; }
    
    public void tick() {
        LivingEntity target = mob.getTarget();
        if( target != null ) {
            double sqrDistToTarget = mob.distanceToSqr( target.getX(), target.getY(), target.getZ() );
            boolean canSee = mob.getSensing().hasLineOfSight( target );
            boolean couldSeeLastTick = seeTime > 0;
            if( canSee != couldSeeLastTick ) {
                seeTime = 0;
            }
            
            if( canSee ) {
                ++seeTime;
            }
            else {
                --seeTime;
            }
            
            if( sqrDistToTarget <= attackRadiusSqr && seeTime >= 20 ) {
                mob.getNavigation().stop();
                ++strafingTime;
            }
            else {
                mob.getNavigation().moveTo( target, speedModifier );
                strafingTime = -1;
            }
            
            if( strafingTime >= 20 ) {
                if( mob.getRandom().nextFloat() < 0.3 ) {
                    strafingClockwise = !strafingClockwise;
                }
                
                if( mob.getRandom().nextFloat() < 0.3 ) {
                    strafingBackwards = !strafingBackwards;
                }
                
                strafingTime = 0;
            }
            
            if( strafingTime > -1 ) {
                if( sqrDistToTarget > attackRadiusSqr * 0.75F ) {
                    strafingBackwards = false;
                }
                else if( sqrDistToTarget < attackRadiusSqr * 0.25F ) {
                    strafingBackwards = true;
                }
                
                mob.getMoveControl().strafe( strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F );
                Entity vehicle = mob.getControlledVehicle();
                if( vehicle instanceof Mob mount ) {
                    mount.lookAt( target, 30.0F, 30.0F );
                }
                
                mob.lookAt( target, 30.0F, 30.0F );
            }
            else {
                mob.getLookControl().setLookAt( target, 30.0F, 30.0F );
            }
            
            if( isShooting ) {
                ++attackTime;
                if( !canSee && seeTime < -60 ) {
                    attackTime = 0;
                    isShooting = false;
                }
                else if( canSee ) {
                    if( attackTime >= 20 ) {
                        mob.performRangedAttack( target, 1.0F );
                        attackTime = attackIntervalMin;
                        isShooting = false;
                    }
                }
            }
            else if( --attackTime <= 0 && seeTime >= -60 ) {
                attackTime = 0;
                isShooting = true;
            }
            
        }
    }
}