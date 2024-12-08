package druyaned.modifyast.handle;

import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import druyaned.modifyast.anno.Getter;
import druyaned.modifyast.util.InsertMethod;
import druyaned.modifyast.util.NameGeneratorGetter;

public class HandleGetter implements Handler {
    
    private final InsertMethod inserter;
    private JCClassDecl classDecl;
    private JCVariableDecl varDecl;
    
    public HandleGetter(InsertMethod inserter) {
        this.inserter = inserter;
    }
    
    public HandleGetter setClassDecl(JCClassDecl classDecl) {
        this.classDecl = classDecl;
        return this;
    }
    
    public HandleGetter setVarDecl(JCVariableDecl varDecl) {
        this.varDecl = varDecl;
        return this;
    }
    
    @Override public void handle() {
        Getter anno = varDecl.sym.getAnnotation(Getter.class);
        if (anno.ignore()) {
            return;
        }
        String name = anno.name().isEmpty()
                ? NameGeneratorGetter.defaultName(varDecl)
                : anno.name();
        inserter.insertGetter(classDecl, varDecl, name);
    }
    
}
