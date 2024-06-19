package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CrustByteMode extends CrustMode<Byte> {
    
    /** Creates a new auto-registered mode. */
    public CrustByteMode( String id, Supplier<Integer> opLevel ) { super( id, opLevel ); }
    
    /** Creates a new auto-registered mode. */
    public CrustByteMode( String id, Supplier<Integer> opLevel, @Nullable ICommandHandler<Byte> validator ) { super( id, opLevel, validator ); }
    
    
    /** @return This mode's saved data. */
    @Override
    public Byte get( CompoundTag tag ) { return tag.getByte( ID ); }
    
    /** Saves this mode's data. */
    @Override
    public void enable( CompoundTag tag, Byte value ) { tag.putByte( ID, value ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Integer> commandArgument( String arg ) {
        return CommandUtil.argument( arg, IntegerArgumentType.integer( Byte.MIN_VALUE, Byte.MAX_VALUE ) );
    }
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value. Null for a 'disable' command.
     */
    @Override
    public void onCommand( CommandContext<CommandSourceStack> context, @Nullable String arg, ServerPlayer player ) {
        validate( player, arg == null ? null : (byte) IntegerArgumentType.getInteger( context, arg ) );
    }
}