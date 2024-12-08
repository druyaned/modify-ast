package druyaned.modifyast.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serves to create getters for all fields (also static) of the annotated
 * {@link ElementType.TYPE type}, except fields annotated with {@link Getter @Getter}.
 * 
 * @author druyaned
 * @see ModificationProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GetterAll {
}
