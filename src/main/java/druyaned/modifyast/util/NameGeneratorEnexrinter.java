package druyaned.modifyast.util;

import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import javax.lang.model.element.ElementKind;

public class NameGeneratorEnexrinter {
    
    public static final String BASE_NAME = "enexrinter";
    public static final String DEFAULT_ENEXRINTER_NAME = BASE_NAME + "_default_0";
    
    private int counter = 1;
    
    public String nextName(JCMethodDecl methodDecl) {
        return methodDecl.sym.getKind().equals(ElementKind.CONSTRUCTOR)
                ? BASE_NAME + "__init__" + (counter++)
                : BASE_NAME + '_' + methodDecl.name + '_' + (counter++);
    }
    
}
