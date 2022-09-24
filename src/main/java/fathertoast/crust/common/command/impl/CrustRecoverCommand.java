package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.core.Crust;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.Collection;

public class CrustRecoverCommand {
    
    public enum Mode { ALL, HEALTH, HUNGER, EFFECTS }
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        // crustrecover [all|health|hunger|effects] [<targets>]
        LiteralArgumentBuilder<CommandSource> argBuilder = CommandUtil.literal( Crust.MOD_ID + "recover" )
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
    private static int run( CommandSource source, Mode mode, Collection<? extends Entity> targets ) {
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
                if( target instanceof PlayerEntity ) recoverHunger( (PlayerEntity) target );
                if( mode != Mode.ALL ) break;
            case EFFECTS:
                clearNegativeEffects( target );
                if( mode != Mode.ALL ) break;
        }
    }
    
    /** Sets the player's hunger to the max. */
    private static void recoverHunger( PlayerEntity player ) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt( "foodLevel", 20 );
        tag.putFloat( "foodSaturationLevel", 20.0F );
        player.getFoodData().readAdditionalSaveData( tag );
    }
    
    /** Clears all negative effects, removes burning, and restores air supply. */
    private static void clearNegativeEffects( LivingEntity target ) {
        ArrayList<Effect> negativeEffects = new ArrayList<>();
        for( EffectInstance effect : target.getActiveEffects() ) {
            if( effect.getEffect().getCategory() == EffectType.HARMFUL ) negativeEffects.add( effect.getEffect() );
        }
        
        for( Effect effect : negativeEffects ) target.removeEffect( effect );
        target.setAirSupply( target.getMaxAirSupply() );
        target.clearFire();
    }
}