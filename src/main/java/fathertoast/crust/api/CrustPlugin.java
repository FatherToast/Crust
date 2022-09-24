package fathertoast.crust.api;

/**
 * Marker annotation for classes that should be treated as plugins
 * for Crust. To make a plugin, simply decorate your plugin class with this
 * annotation. Crust will then attempt to find it at runtime, during
 * Intermod Comms.<br>
 * <br>
 * <strong>Note: your plugin class must also implement {@link ICrustPlugin}</strong>
 */
public @interface CrustPlugin {
}
