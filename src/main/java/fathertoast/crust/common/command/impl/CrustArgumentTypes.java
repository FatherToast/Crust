package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import fathertoast.crust.common.api.impl.CrustApi;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CrustArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, CrustApi.MOD_ID);


    public static final RegistryObject<ArgumentTypeInfo<PortalTypeArgument, ?>> PORTAL_TYPE = register("portal_type",
            () -> ArgumentTypeInfos.registerByClass(PortalTypeArgument.class, SingletonArgumentInfo.contextFree(PortalTypeArgument::portalType)));


    /** Registers an argument type to the deferred register. */
    private static <T extends ArgumentType<?>> RegistryObject<ArgumentTypeInfo<T, ?>> register(String name, Supplier<ArgumentTypeInfo<T, ?>> supplier) {
        return ARGUMENT_TYPE_REGISTER.register(name, supplier);
    }

    /** Called to register this class. */
    public static void register( IEventBus modBus ) {
        ARGUMENT_TYPE_REGISTER.register( modBus );
    }
}
