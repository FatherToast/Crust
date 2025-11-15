package fathertoast.crust.test.common.entity;

import fathertoast.crust.api.entity.CrustFishingHook;
import fathertoast.crust.api.entity.IAngler;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.api.util.shape.CircleShape;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.test.common.TestCrust;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestSkeleton extends AbstractSkeleton implements IAngler, IDebugShapeProvider {
    private final Goal fishGoal = new TestRangedAttackGoal<>( this, 1.0, 20, 15.0F );
    
    public TestSkeleton( EntityType<? extends TestSkeleton> type, Level level ) {
        super( type, level );
    }
    
    @Override
    protected SoundEvent getStepSound() { return SoundEvents.AMETHYST_BLOCK_STEP; } // lol
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Make it attack animals
        targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this,
                Animal.class, true ) );
    }
    
    @Override
    protected void populateDefaultEquipmentSlots( RandomSource random, DifficultyInstance difficulty ) {
        super.populateDefaultEquipmentSlots( random, difficulty );
        setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.FISHING_ROD ) );
    }
    
    @Override
    public void reassessWeaponGoal() {
        //noinspection ConstantValue
        if( level() != null && !level().isClientSide && fishGoal != null ) {
            // Just the fishing goal
            goalSelector.removeGoal( fishGoal );
            goalSelector.addGoal( 4, fishGoal );
        }
    }
    
    @Override // RangedAttackMob
    public void performRangedAttack( LivingEntity target, float power ) {
        if( CrustFishingHook.performRangedAttackFor( this, target ) ) {
            TestCrust.LOG.info( "shot!" );
        }
    }
    
    
    // ---- IAngler Implementation ---- //
    
    private CrustFishingHook fishHook;
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
    
    /** @return This angler's active fishing hook. */
    @Override // IAngler
    @Nullable
    public CrustFishingHook getHook() { return fishHook; }
    
    /** Sets this angler's active fishing hook. */
    @Override // IAngler
    public void setHook( @Nullable CrustFishingHook newHook ) { fishHook = newHook; }
    
    /** @return The entity this angler represents, or null if there is none. */
    @Override // IAngler
    public TestSkeleton asEntity() { return this; }
    
    /**
     * @return The position of this angler, or null if there is none.
     * It is recommended that you override this and always provide a non-null value.
     */
    @Override // IAngler
    public Vec3 getLinePos( float partialTick ) { return IAngler.getBipedLinePos( this, partialTick ); }
    
    
    // ---- IDebugShapeProvider Implementation ---- //
    
    @Nullable
    @Override // IDebugShapeProvider
    public List<IDebugShape> getDebugShapes() {
        List<IDebugShape> debugShapes = IDebugShapeProvider.fromBBs( 0x0000FF, getBoundingBox().inflate( 1.0 ) );
        //debugShapes.add( new BoxShape( this ).withPos( 0, 2, 0 ).withColor( 0xFFFF00 ) );
        debugShapes.add( new CircleShape( Direction.Axis.Y, 0.5F )
                .withPos( 0, 2.1, 0 ).withColor( 0xFFFF00 ) ); // Blessed be thy fishington
        return debugShapes;
    }
}