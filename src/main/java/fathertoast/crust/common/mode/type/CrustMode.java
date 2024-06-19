package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class CrustMode<T> {
    
    /** The unique mode identifier. */
    public final String ID;
    /** Permission level required to control this mode. */
    public final Supplier<Integer> OP_LEVEL;
    
    /**
     * Command handler used to validate requests. A simple default implementation for this validation
     * is provided by {@link #validate(ServerPlayer, T)}.
     */
    protected final ICommandHandler<T> VALIDATOR;
    
    /** Creates a new auto-registered mode. */
    public CrustMode( String id, Supplier<Integer> opLevel ) { this( id, opLevel, null ); }
    
    /** Creates a new auto-registered mode. */
    public CrustMode( String id, Supplier<Integer> opLevel, @Nullable ICommandHandler<T> validator ) {
        ID = id;
        OP_LEVEL = opLevel;
        VALIDATOR = validator;
        CrustModes.register( this );
    }
    
    /**
     * @return True if the mode is enabled; that is, if any save data for the mode exists.
     * This is a shortcut method handy when we don't need to do anything else with the mode data.
     * @see CrustModesData#enabled(CrustMode)
     */
    public final boolean enabled( @Nullable Player player ) { return player != null && CrustModesData.of( player ).enabled( this ); }
    
    /** @return True if any save data for this mode exists. */
    public boolean enabled( CompoundTag tag ) { return NBTHelper.containsNumber( tag, ID ); }
    
    /** @return This mode's saved data, or its non-null default value if no save data exists. */
    public abstract T get( CompoundTag tag );
    
    /** Saves this mode's data. */
    public abstract void enable( CompoundTag tag, T value );
    
    /** Disables this mode by deleting any existing save data. */
    public void disable( CompoundTag tag ) { tag.remove( ID ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    public abstract RequiredArgumentBuilder<CommandSourceStack, ?> commandArgument(String arg );
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value for an 'enable' command. Null for a 'disable' command.
     */
    public abstract void onCommand(CommandContext<CommandSourceStack> context, @Nullable String arg, ServerPlayer player );
    
    /** Validates and applies a command set request. */
    protected void validate( ServerPlayer player, @Nullable T value ) {
        if( VALIDATOR != null ) value = VALIDATOR.validate( player, value );
        if( value == null ) CrustModesData.of( player ).disable( this );
        else CrustModesData.of( player ).enable( this, value );
    }
    
    /** Provides validation for command input. This allows simple management of the mode's save data. */
    public interface ICommandHandler<T> {
        /** @return The value, corrected or bounded if necessary. */
        @Nullable
        T validate( ServerPlayer player, @Nullable T value );
    }
}