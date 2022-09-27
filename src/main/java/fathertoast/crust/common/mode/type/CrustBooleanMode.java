package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CrustBooleanMode extends CrustMode<Boolean> {
    
    /** Creates a new auto-registered mode. */
    public CrustBooleanMode( String id, Supplier<Integer> opLevel ) { super( id, opLevel ); }
    
    /** Creates a new auto-registered mode. */
    public CrustBooleanMode( String id, Supplier<Integer> opLevel, @Nullable ICommandHandler<Boolean> validator ) { super( id, opLevel, validator ); }
    
    
    /** @return This mode's saved data. */
    @Override
    public Boolean get( CompoundNBT tag ) { return tag.getBoolean( ID ); }
    
    /** Saves this mode's data. */
    @Override
    public void enable( CompoundNBT tag, Boolean value ) { tag.putBoolean( ID, value ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    @Override
    public RequiredArgumentBuilder<CommandSource, Boolean> commandArgument( String arg ) {
        return CommandUtil.argument( arg, BoolArgumentType.bool() );
    }
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value. Null for a 'disable' command.
     */
    @Override
    public void onCommand( CommandContext<CommandSource> context, @Nullable String arg, ServerPlayerEntity player ) {
        validate( player, arg == null ? null : BoolArgumentType.getBool( context, arg ) );
    }
}