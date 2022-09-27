package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CrustFloatMode extends CrustMode<Float> {
    
    /** Creates a new auto-registered mode. */
    public CrustFloatMode( String id, Supplier<Integer> opLevel ) { super( id, opLevel ); }
    
    /** Creates a new auto-registered mode. */
    public CrustFloatMode( String id, Supplier<Integer> opLevel, @Nullable ICommandHandler<Float> validator ) { super( id, opLevel, validator ); }
    
    
    /** @return This mode's saved data. */
    @Override
    public Float get( CompoundNBT tag ) { return tag.getFloat( ID ); }
    
    /** Saves this mode's data. */
    @Override
    public void enable( CompoundNBT tag, Float value ) { tag.putFloat( ID, value ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    @Override
    public RequiredArgumentBuilder<CommandSource, Float> commandArgument( String arg ) {
        return CommandUtil.argument( arg, FloatArgumentType.floatArg() );
    }
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value. Null for a 'disable' command.
     */
    @Override
    public void onCommand( CommandContext<CommandSource> context, @Nullable String arg, ServerPlayerEntity player ) {
        validate( player, arg == null ? null : FloatArgumentType.getFloat( context, arg ) );
    }
}