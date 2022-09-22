package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.core.Crust;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class CrustRecoverCommand {
    
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        LiteralArgumentBuilder<CommandSource> argBuilder = CommandUtil.literal( Crust.MOD_ID + "recover" )
                .requires( CommandUtil::isOP )
                .executes( ( context ) -> recover( context.getSource(), Mode.ALL, CommandUtil.targetSelf( context ) ) )
                
                .then( CommandUtil.argument( "targets", EntityArgument.entities() )
                        .executes( ( context ) -> recover( context.getSource(), Mode.ALL, CommandUtil.targets( context, "targets" ) ) ) );
        
        for( Mode mode : Mode.values() ) {
            argBuilder.then( CommandUtil.literal( mode.name().toLowerCase( Locale.ROOT ) )
                    .executes( ( context ) -> recover( context.getSource(), mode, CommandUtil.targetSelf( context ) ) )
                    
                    .then( CommandUtil.argument( "targets", EntityArgument.entities() )
                            .executes( ( context ) -> recover( context.getSource(), mode, CommandUtil.targets( context, "targets" ) ) ) )
            );
        }
        
        dispatcher.register( argBuilder );
    }
    
    private static int recover( CommandSource source, Mode mode, Collection<? extends Entity> targets ) {
        for( Entity target : targets ) {
            if( target instanceof LivingEntity ) recover( (LivingEntity) target, mode );
        }
        
        if( targets.size() > 0 ) {
            CommandUtil.sendSuccess( source, "recover", targets.size() );
        }
        return targets.size();
    }
    
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
    
    private static void recoverHunger( PlayerEntity player ) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt( "foodLevel", 20 );
        tag.putFloat( "foodSaturationLevel", 20.0F );
        player.getFoodData().readAdditionalSaveData( tag );
    }
    
    private static void clearNegativeEffects( LivingEntity target ) {
        ArrayList<Effect> negativeEffects = new ArrayList<>();
        for( EffectInstance effect : target.getActiveEffects() ) {
            if( effect.getEffect().getCategory() == EffectType.HARMFUL ) negativeEffects.add( effect.getEffect() );
        }
        
        for( Effect effect : negativeEffects ) target.removeEffect( effect );
        target.setAirSupply( target.getMaxAirSupply() );
        target.clearFire();
    }
    
    public enum Mode { ALL, HEALTH, HUNGER, EFFECTS }
}