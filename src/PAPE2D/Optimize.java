package PAPE2D;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a method implementation can be
 * optimized later in development, if need be.
 */
@Retention(RetentionPolicy.SOURCE) // Stays in code, doesn't affect the compiled file
public @interface Optimize {
    String value() default "";
}