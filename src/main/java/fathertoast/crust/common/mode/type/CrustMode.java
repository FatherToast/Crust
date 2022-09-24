package fathertoast.crust.common.mode.type;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public abstract class CrustMode<T> {
    
    /** The unique mode identifier. */
    public final String ID;
    /** Permission level required to control this mode. */
    public final int OP_LEVEL;
    /** Command handler used to validate requests. */
    private final ICommandHandler<T> VALIDATOR;
    
    /** Creates a new auto-registered mode. */
    public CrustMode( String id, int opLevel ) { this( id, opLevel, null ); }
    
    /** Creates a new auto-registered mode. */
    public CrustMode( String id, int opLevel, @Nullable ICommandHandler<T> validator ) {
        ID = id;
        OP_LEVEL = opLevel;
        VALIDATOR = validator;
        CrustModes.register( this );
    }
    
    
    /** @return True if any save data for this mode exists. */
    public boolean enabled( CompoundNBT tag ) { return tag.contains( ID, NBTHelper.ID_NUMERICAL ); }
    
    /** @return This mode's saved data, or its non-null default value if no save data exists. */
    public abstract T get( CompoundNBT tag );
    
    /** Saves this mode's data. */
    public abstract void enable( CompoundNBT tag, T value );
    
    /** Disables this mode by deleting any existing save data. */
    public void disable( CompoundNBT tag ) { tag.remove( ID ); }
    
    
    /** @return The argument for this mode's value when referenced by the crustmode command. */
    public abstract RequiredArgumentBuilder<CommandSource, ?> commandArgument( String arg );
    
    /**
     * Updates this mode's data based on command input.
     * This command input is a request from the client and should be validated before applying anything.
     *
     * @param arg The argument corresponding to the value for an 'enable' command. Null for a 'disable' command.
     */
    public abstract void onCommand( CommandContext<CommandSource> context, @Nullable String arg, ServerPlayerEntity player );
    
    /** Validates and applies a command set request. */
    protected void validate( ServerPlayerEntity player, @Nullable T value ) {
        if( VALIDATOR != null ) value = VALIDATOR.validate( player, value );
        CrustModesData playerModes = new CrustModesData( player );
        if( value == null ) playerModes.disable( this );
        else playerModes.enable( this, value );
    }
    
    public interface ICommandHandler<T> {
        @Nullable
        T validate( ServerPlayerEntity player, @Nullable T value );
    }
}