package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.common.command.CommandUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CrustLongMode extends CrustMode<Long> {
    
    /** Creates a new auto-registered mode. */
    public CrustLongMode( String id, Supplier<Integer> opLevel ) { super( id, opLevel ); }
    
    /** Creates a new auto-registered mode. */
    public CrustLongMode( String id, Supplier<Integer> opLevel, @Nullable ICommandHandler<Long> validator ) { super( id, opLevel, validator ); }
    
    
    /** @return This mode's saved data. */
    @Override
    public Long get( CompoundTag tag ) { return tag.getLong( ID ); }
    
    /** Saves this mode's data. */
    @Override
    public void enable( CompoundTag tag, Long value ) { tag.putLong( ID, value ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Long> commandArgument( String arg ) {
        return CommandUtil.argument( arg, LongArgumentType.longArg() );
    }
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value. Null for a 'disable' command.
     */
    @Override
    public void onCommand( CommandContext<CommandSourceStack> context, @Nullable String arg, ServerPlayer player ) {
        validate( player, arg == null ? null : LongArgumentType.getLong( context, arg ) );
    }
}