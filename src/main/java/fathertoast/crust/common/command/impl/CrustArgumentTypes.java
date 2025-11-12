package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.api.impl.CrustApi;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class CrustArgumentTypes {
    
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_REGISTER = DeferredRegister.create( ForgeRegistries.COMMAND_ARGUMENT_TYPES, CrustApi.MOD_ID );
    
    static {
        register( CrustObjects.CommandArguments.PORTAL_TYPE,
                () -> ArgumentTypeInfos.registerByClass( PortalTypeArgument.class,
                        SingletonArgumentInfo.contextFree( PortalTypeArgument::portalType ) ) );
    }
    
    /** Registers an argument type to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends ArgumentType<?>> void register( RegistryObject<?> regObj, Supplier<ArgumentTypeInfo<T, ?>> supplier ) {
        ARGUMENT_TYPE_REGISTER.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus modBus ) {
        ARGUMENT_TYPE_REGISTER.register( modBus );
    }
}