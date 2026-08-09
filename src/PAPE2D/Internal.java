package PAPE2D;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a method, field, or type is public only for
 * cross-package use within the engine, and is not part of the
 * supported external API. May change or be removed without notice.
 */
@Retention(RetentionPolicy.SOURCE) // Stays in code, doesn't affect the compiled file
public @interface Internal {
    String value() default "";
}