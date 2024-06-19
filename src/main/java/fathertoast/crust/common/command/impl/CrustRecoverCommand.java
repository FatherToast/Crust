package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CrustRecoverCommand {
    
    public enum Mode { ALL, HEALTH, HUNGER, EFFECTS }
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        // crustrecover [all|health|hunger|effects] [<targets>]
        LiteralArgumentBuilder<CommandSourceStack> argBuilder = CommandUtil.literal( ICrustApi.MOD_ID + "recover" )
                .requires( CommandUtil::canCheat )
                .executes( ( context ) -> run( context.getSource(), Mode.ALL, CommandUtil.targets( context ) ) )
                
                .then( CommandUtil.argumentTargets( "targets" )
                        .executes( ( context ) -> run( context.getSource(), Mode.ALL, CommandUtil.targets( context, "targets" ) ) ) );
        
        for( Mode mode : Mode.values() ) {
            argBuilder.then( CommandUtil.literal( mode )
                    .executes( ( context ) -> run( context.getSource(), mode, CommandUtil.targets( context ) ) )
                    
                    .then( CommandUtil.argumentTargets( "targets" )
                            .executes( ( context ) -> run( context.getSource(), mode, CommandUtil.targets( context, "targets" ) ) ) )
            );
        }
        
        dispatcher.register( argBuilder );
    }
    
    /** Command implementation. */
    private static int run( CommandSourceStack source, Mode mode, Collection<? extends Entity> targets ) {
        for( Entity target : targets ) {
            if( target instanceof LivingEntity ) recover( (LivingEntity) target, mode );
        }
        
        if( targets.size() == 1 ) {
            CommandUtil.sendSuccess( source, "recover.single." + CommandUtil.toString( mode ),
                    targets.iterator().next().getDisplayName() );
        }
        else {
            CommandUtil.sendSuccess( source, "recover.multiple." + CommandUtil.toString( mode ), targets.size() );
        }
        return targets.size();
    }
    
    /** Recovers the target. */
    private static void recover( LivingEntity target, Mode mode ) {
        switch( mode ) {
            case ALL:
                // Just let this fall through all labels
            case HEALTH:
                target.heal( target.getMaxHealth() );
                if( mode != Mode.ALL ) break;
            case HUNGER:
                if( target instanceof Player player ) recoverHunger( player );
                if( mode != Mode.ALL ) break;
            case EFFECTS:
                clearNegativeEffects( target );
                if( mode != Mode.ALL ) break;
        }
    }
    
    /** Sets the player's hunger to the max. */
    private static void recoverHunger( Player player ) {
        CompoundTag tag = new CompoundTag();
        tag.putInt( "foodLevel", 20 );
        tag.putFloat( "foodSaturationLevel", 20.0F );
        player.getFoodData().readAdditionalSaveData( tag );
    }
    
    /** Clears all negative effects, removes burning, and restores air supply. */
    private static void clearNegativeEffects( LivingEntity target ) {
        ArrayList<MobEffect> negativeEffects = new ArrayList<>();
        for( MobEffectInstance effect : target.getActiveEffects() ) {
            if( effect.getEffect().getCategory() == MobEffectCategory.HARMFUL ) negativeEffects.add( effect.getEffect() );
        }
        
        for( MobEffect effect : negativeEffects ) target.removeEffect( effect );
        target.setAirSupply( target.getMaxAirSupply() );
        target.clearFire();
    }
}