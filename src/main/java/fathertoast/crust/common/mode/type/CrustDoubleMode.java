package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class CrustDoubleMode extends CrustMode<Double> {
    
    /** Creates a new auto-registered mode. */
    public CrustDoubleMode( String id, int opLevel ) { super( id, opLevel ); }
    
    /** Creates a new auto-registered mode. */
    public CrustDoubleMode( String id, int opLevel, @Nullable ICommandHandler<Double> validator ) { super( id, opLevel, validator ); }
    
    
    /** @return This mode's saved data. */
    @Override
    public Double get( CompoundNBT tag ) { return tag.getDouble( ID ); }
    
    /** Saves this mode's data. */
    @Override
    public void enable( CompoundNBT tag, Double value ) { tag.putDouble( ID, value ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    @Override
    public RequiredArgumentBuilder<CommandSource, Double> commandArgument( String arg ) {
        return CommandUtil.argument( arg, DoubleArgumentType.doubleArg() );
    }
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value. Null for a 'disable' command.
     */
    @Override
    public void onCommand( CommandContext<CommandSource> context, @Nullable String arg, ServerPlayerEntity player ) {
        validate( player, arg == null ? null : DoubleArgumentType.getDouble( context, arg ) );
    }
}