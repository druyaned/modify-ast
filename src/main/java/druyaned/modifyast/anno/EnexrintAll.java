package druyaned.modifyast.anno;

import druyaned.modifyast.ModificationProcessor;
import druyaned.modifyast.enexrint.Enexrinter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If this annotation is applied to {@link ElementType#TYPE type} then
 * {@link Enexrinter enexrinter} will be inserted into type's declaration,
 * all its methods (also static) with body and constructors will have
 * {@link Enexrinter#enterPrint enterPrint} at the beginning
 * of the body and {@link Enexrinter#exitPrint exitPrint} before each
 * {@code return statement}. Note, if it's necessary
 * exitPrint invocation to be inserted into void method, than
 * return statement should be written explicitly.
 * 
 * <P>Basically, all methods of {@link Enexrint} are stronger
 * than the same methods of {@code EnexrintAll}.<br>
 * Lets consider the example.
   *<pre>
   *&#47;&#47; Comments describe places of enexrinter insertion.
   *&#64;EnexrintAll class MyClass {
   *    // public static final Enexrinter enexrinter_default_0 = ...
   *    // public static final Enexrinter enexrinter_method3_1 = ...
   *                                  void method1() {}
   *    &#64;Enexrint(ignore=true)        void method2() {}
   *    &#64;Enexrint(outputFile="f.txt") void method3() {}
   *}</pre>
 * {@code method1} will be handled according to the {@code @EnexrintAll}
 * annotation, {@code method2} will not be handled and {@code method3}
 * will be handled according to its own {@code @Enexrint} annotation.
 * 
 * @author druyaned
 * @see Enexrint
 * @see ModificationProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EnexrintAll {
    
    /**
     * Describes format for {@link Enexrinter#enterPrint enterPrint},
     * where default value is {@code [ENTER ${methodName}]}.
     * 
     * <P>Format represents a string with some {@code parent.child}:<ol>
     *      <li>${methodName}</li>
     *      <li>${target}</li><ol>
     *          <li>${target}.&lt;any_field_name&gt;</li></ol>
     *      <li>${thread}</li><ol>
     *          <li>${thread}.getName</li>
     *          <li>${thread}.getPriority</li>
     *          <li>${thread}.getState</li></ol>
     *      <li>${time}</li></ol>
     * 
     * <P><i>Examples</i><ol>
     *      <li><code>"[ENTER ${methodName}] name=${target}.name time=${time}"</code></li>
     *      <li><code>"Here comes an enter of '${method}'"</code></li>
     *      <li><code>"In '${methodName}' threadState=${thread}.getState
     *          time=${time}"</code></li></ol>
     * 
     * @return format for {@link Enexrinter#enterPrint enterPrint}
     */
    public String enterFormat() default "[ENTER ${methodName}]";
    
    /**
     * Describes format for {@link Enexrinter#exitPrint exitPrint},
     * where default value is {@code [EXIT ${methodName}]}.
     * 
     * <P>Format represents a string with some {@code parent.child}:<ol>
     *      <li>${methodName}</li>
     *      <li>${target}</li><ol>
     *          <li>${target}.&lt;any_field_name&gt;</li></ol>
     *      <li>${thread}</li><ol>
     *          <li>${thread}.getName</li>
     *          <li>${thread}.getPriority</li>
     *          <li>${thread}.getState</li></ol>
     *      <li>${time}</li></ol>
     * 
     * <P><i>Examples</i><ol>
     *      <li><code>"[EXIT ${methodName}] name=${target}.name time=${time}"</code></li>
     *      <li><code>"Here comes an exit of '${method}'"</code></li>
     *      <li><code>"Out of '${methodName}' threadState=${thread}.getState
     *          time=${time}"</code></li></ol>
     * 
     * @return format for {@link Enexrinter#exitPrint exitPrint}
     */
    public String exitFormat() default "[EXIT ${methodName}]";
    
    /**
     * Describes name of file into which {@link Enexrinter#enterPrint enterPrint}
     * and {@link Enexrinter#exitPrint exitPrint} will be appended;
     * default value is {@code empty line}, which means
     * standard output is being used (System.out).
     * 
     * @return name of file to append {@link Enexrinter#enterPrint enterPrint}
     *      and {@link Enexrinter#exitPrint exitPrint}; default value is
     *      {@code empty line}, which means standard output is being used
     */
    public String outputFile() default "";
    
}
