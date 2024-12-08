package druyaned.modifyast.handle;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import druyaned.modifyast.anno.Enexrint;
import druyaned.modifyast.anno.EnexrintAll;
import druyaned.modifyast.util.InsertField;
import druyaned.modifyast.util.InsertStatement;

public class HandleEnexrintAll implements Handler {
    
    private final InsertField fieldInserter;
    private final InsertStatement statInserter;
    private JCClassDecl classDecl;
    
    public HandleEnexrintAll(InsertField fieldInserter, InsertStatement statInserter) {
        this.fieldInserter = fieldInserter;
        this.statInserter = statInserter;
    }
    
    public HandleEnexrintAll setClassDecl(JCClassDecl classDecl) {
        this.classDecl = classDecl;
        return this;
    }
    
    @Override public void handle() {
        EnexrintAll anno = classDecl.sym.getAnnotation(EnexrintAll.class);
        JCVariableDecl enexrinterDecl = fieldInserter.insertEnexrinter(
                classDecl,
                anno.enterFormat(),
                anno.exitFormat(),
                anno.outputFile()
        );
        for (JCTree classDeclDef : classDecl.defs) {
            if (
                    classDeclDef instanceof JCMethodDecl methodDecl
                    && methodDecl.sym.getAnnotation(Enexrint.class) == null
                    && methodDecl.body != null
            ) {
                statInserter.insertEnterPrint(classDecl, methodDecl, enexrinterDecl);
                statInserter.insertExitPrint(classDecl, methodDecl, enexrinterDecl);
            }
        }
    }
    
}
