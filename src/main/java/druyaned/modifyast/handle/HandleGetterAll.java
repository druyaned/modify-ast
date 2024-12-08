package druyaned.modifyast.handle;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import druyaned.modifyast.anno.Getter;
import druyaned.modifyast.anno.GetterAll;
import druyaned.modifyast.util.InsertMethod;
import druyaned.modifyast.util.NameGeneratorGetter;

public class HandleGetterAll implements Handler {
    
    private final InsertMethod inserter;
    private JCClassDecl classDecl;
    
    public HandleGetterAll(InsertMethod inserter) {
        this.inserter = inserter;
    }
    
    public HandleGetterAll setClassDecl(JCClassDecl classDecl) {
        this.classDecl = classDecl;
        return this;
    }
    
    @Override public void handle() {
        if (classDecl.sym.getAnnotation(GetterAll.class) == null) {
            throw new IllegalArgumentException(classDecl.name.toString()
                    + " is not annotated with @GetterAll");
        }
        for (JCTree classDeclDef : classDecl.defs) {
            if (
                    classDeclDef instanceof JCVariableDecl varDecl
                    && varDecl.sym.getAnnotation(Getter.class) == null
            ) {
                String name = NameGeneratorGetter.defaultName(varDecl);
                inserter.insertGetter(classDecl, varDecl, name);
            }
        }
    }
    
}
