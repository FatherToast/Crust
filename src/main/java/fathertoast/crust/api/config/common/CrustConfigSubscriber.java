package fathertoast.crust.api.config.common;

import fathertoast.crust.api.ICrustPlugin;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for classes that should be treated as plugins
 * for Crust. To make a plugin, simply decorate your plugin class with this
 * annotation. Crust will then attempt to find it at runtime, during
 * the {@link FMLCommonSetupEvent}.<br>
 * <br>
 * <strong>Note: your plugin class must also implement {@link ICrustPlugin}</strong>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@ApiStatus.Experimental
public @interface CrustConfigSubscriber { } // TODO do not use yet