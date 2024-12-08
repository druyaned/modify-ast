package druyaned.modifyast.enexrint;

import druyaned.modifyast.anno.Enexrint;
import druyaned.modifyast.anno.EnexrintAll;

/**
 * Serves to store the data of printing format for enter and exit of a method.
 * @author druyaned
 * @see FormatPrint
 */
public class Enexrinter {
    
    private final FormatPrint enterPrinter;
    private final FormatPrint exitPrinter;
    
    public Enexrinter(String enterFormat, String exitFormat, String outputFile) {
        enterPrinter = new FormatPrint(enterFormat, outputFile);
        exitPrinter = new FormatPrint(exitFormat, outputFile);
    }
    
    /**
     * Prints message according to the given {@link Enexrint#enterFormat enter format}
     * at the beginning of method's body
     * into the {@link Enexrint#outputFile given location}.
     * 
     * @param target instance of annotated class (EnexrintAll) or
     *      instance of class of annotated method, can be null in case of
     *      constructor or static method
     * @param methodName name of method where printing performs
     * @see Enexrint
     * @see EnexrintAll
     */
    public void enterPrint(Object target, String methodName) {
        enterPrinter.run(target, methodName);
    }
    
    /**
     * Prints message according to the given {@link Enexrint#exitFormat exit format}
     * before each {@code return statement}
     * into the {@link Enexrint#outputFile given location}.
     * 
     * @param target instance of annotated class (EnexrintAll) or
     *      instance of class of annotated method, can be null in case of
     *      constructor or static method
     * @param methodName name of method where printing performs
     * @see Enexrint
     * @see EnexrintAll
     */
    public void exitPrint(Object target, String methodName) {
        exitPrinter.run(target, methodName);
    }
    
}
