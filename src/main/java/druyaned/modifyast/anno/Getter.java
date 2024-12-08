package druyaned.modifyast.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serves to create getter for field (also static).
 * 
 * @author druyaned
 * @see ModificationProcessor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Getter {
    
    /**
     * Allows or forbids annotation handling, where default value is false,
     * which means the handling permission.
     * 
     * @return whether this annotation handling to be ignored (true) or not (false, default)
     * @see GetterAll
     */
    public boolean ignore() default false;
    
    /**
     * Returns name of generated getter.
     * @return name of generated getter
     */
    public String name() default "";
    
}
