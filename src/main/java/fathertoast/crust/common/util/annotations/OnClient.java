package fathertoast.crust.common.util.annotations;

/**
 * Decorative annotation used to mark classes/fields/methods as only being referenced on client.
 * <p>
 * (This does not strip away code from ANY logical sides)
 */
public @interface OnClient { }