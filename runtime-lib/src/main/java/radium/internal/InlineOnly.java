package radium.internal;

import java.lang.annotation.*;

/**
 * Specifies that this function should not be called directly without inlining
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface InlineOnly {
}
