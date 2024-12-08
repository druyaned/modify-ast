package druyaned.modifyast.anno;

import druyaned.modifyast.ModificationProcessor;
import druyaned.modifyast.enexrint.Enexrinter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If this annotation is applied to {@link ElementType#METHOD method} (also static)
 * with body or to {@link ElementType#CONSTRUCTOR constructor} then
 * {@link Enexrinter enexrinter} will be inserted into owner class declaration,
 * {@link Enexrinter#enterPrint enterPrint} will be inserted at the beginning
 * of the body and {@link Enexrinter#exitPrint exitPrint} will be inserted
 * before each {@code return statement}. Note, if it's necessary
 * exitPrint invocation to be inserted into void method, than
 * return statement should be written explicitly.
 * 
 * <P>Usage example.
   *<pre>
   *&#47;&#47; Comments describe places of insertion.
   *&#47;&#47; public static final enexrinter_someMethod_1 = ...
   *&#64;Enexrint(
   *    enterFormat = "[ENTER ${methodName}] thisToString=${target}",
   *    exitFormat = "[EXIT ${methodName}] state=${thread}.getState"
   *)
   *public void someMethod(String... args) {
   *    // enexrinter_someMethod_1.enterPrint(this, "someMethod");
   *    int returnValue = doSomething(args);
   *    boolean returnCondition = doSomethingElse(returnValue);
   *    if (returnCondition) {
   *        // enexrinter_someMethod_1.exitPrint(this, "someMethod");
   *        return;
   *    }
   *    // enexrinter_someMethod_1.exitPrint(this, "someMethod");
   *    return;
   *}</pre>
 * 
 * @author druyaned
 * @see EnexrintAll
 * @see ModificationProcessor
 */
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface Enexrint {
    
    /**
     * Allows or forbids annotation handling, where default value is false,
     * which means the handling permission.
     * 
     * @return whether this annotation handling to be ignored (true) or not (false, default)
     * @see EnexrintAll
     */
    public boolean ignore() default false;
    
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
