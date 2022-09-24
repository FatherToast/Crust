package fathertoast.crust.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for classes that should be treated as plugins
 * for Crust. To make a plugin, simply decorate your plugin class with this
 * annotation. Crust will then attempt to find it at runtime, during
 * Intermod Comms.<br>
 * <br>
 * <strong>Note: your plugin class must also implement {@link ICrustPlugin}</strong>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface CrustPlugin { }