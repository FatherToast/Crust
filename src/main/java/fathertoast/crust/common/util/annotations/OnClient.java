package fathertoast.crust.common.util.annotations;

/**
 * Purely decorative annotation used to mark classes/fields/methods as only being referenced on client
 * even if they are outside an expected client package.
 */
public @interface OnClient { }