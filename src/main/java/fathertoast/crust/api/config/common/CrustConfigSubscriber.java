package fathertoast.crust.api.config.common;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@ApiStatus.Experimental
public @interface CrustConfigSubscriber { } // TODO do not use yet