package druyaned.modifyast.handle;

import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import druyaned.modifyast.anno.Enexrint;
import druyaned.modifyast.util.NameGeneratorEnexrinter;
import druyaned.modifyast.util.InsertField;
import druyaned.modifyast.util.InsertStatement;
import java.util.Map;

public class HandleEnexrint implements Handler {
    
    private final InsertField fieldInserter;
    private final InsertStatement statInserter;
    private final Map<JCClassDecl, NameGeneratorEnexrinter> classToNameGen;
    private JCClassDecl classDecl;
    private JCMethodDecl methodDecl;
    
    public HandleEnexrint(
            InsertField fieldInserter,
            InsertStatement statInserter,
            Map<JCClassDecl, NameGeneratorEnexrinter> classToNameGen
    ) {
        this.fieldInserter = fieldInserter;
        this.statInserter = statInserter;
        this.classToNameGen = classToNameGen;
    }
    
    public HandleEnexrint setClassDecl(JCClassDecl classDecl) {
        this.classDecl = classDecl;
        return this;
    }
    
    public HandleEnexrint setMethodDecl(JCMethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        return this;
    }
    
    @Override public void handle() {
        Enexrint anno = methodDecl.sym.getAnnotation(Enexrint.class);
        if (anno.ignore() || methodDecl.body == null) {
            return;
        }
        NameGeneratorEnexrinter nameGen = classToNameGen.get(classDecl);
        if (nameGen == null) {
            nameGen = new NameGeneratorEnexrinter();
            classToNameGen.put(classDecl, nameGen);
        }
        String name = nameGen.nextName(methodDecl);
        JCVariableDecl enexrinterDecl = fieldInserter.insertEnexrinter(
                classDecl,
                anno.enterFormat(),
                anno.exitFormat(),
                anno.outputFile(),
                name
        );
        statInserter.insertEnterPrint(classDecl, methodDecl, enexrinterDecl);
        statInserter.insertExitPrint(classDecl, methodDecl, enexrinterDecl);
    }
    
}
