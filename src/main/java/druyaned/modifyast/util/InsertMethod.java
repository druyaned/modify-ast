package druyaned.modifyast.util;

import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Flags.STATIC;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InsertMethod {
    
    private final TreeMaker treeMaker;
    private final Names names;
    private final MemberEnter memberEnterInst;
    private final Enter enter;
    private final Method memberEnterMeth;
    
    public InsertMethod(JavacProcessingEnvironment jcProcEnv) {
        Context context = jcProcEnv.getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        memberEnterInst = MemberEnter.instance(context);
        enter = Enter.instance(context);
        try {
            memberEnterMeth = MemberEnter.class.getDeclaredMethod("memberEnter",
                    JCTree.class, Env.class);
            memberEnterMeth.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public JCMethodDecl insertGetter(
            JCClassDecl classDecl,
            JCVariableDecl varDecl,
            String name
    ) {
        JCMethodDecl getterDecl = findGetter(classDecl, name);
        if (getterDecl != null) {
            return null;
        }
        boolean isStatic = (varDecl.mods.flags & (long)STATIC) != 0;
        long flags = isStatic ? (PUBLIC | STATIC) : PUBLIC;
        getterDecl = treeMaker.MethodDef(
                treeMaker.Modifiers(flags), // mods
                names.fromString(name), // name
                varDecl.vartype, // restype
                List.<JCTree.JCTypeParameter>nil(), // typarams
                List.<JCVariableDecl>nil(), // params
                List.<JCTree.JCExpression>nil(), // thrown
                treeMaker.Block(
                        0, // flags
                        List.of(treeMaker.Return(treeMaker.Ident(varDecl))) // stats
                ), // body
                null // defaultValue
        );
        integration(getterDecl, classDecl);
        return getterDecl;
    }
    
    private JCMethodDecl findGetter(JCClassDecl classDecl, String getterNameString) {
        for (JCTree classDef : classDecl.defs) {
            if (
                    classDef instanceof JCMethodDecl methodDecl
                    && methodDecl.name.toString().equals(getterNameString)
                    && methodDecl.params.isEmpty()
            ) {
                return methodDecl;
            }
        }
        return null;
    }
    
    private void integration(JCTree tree, JCClassDecl parent) {
        parent.defs = parent.defs.append(tree);
        try {
            memberEnterMeth.invoke(memberEnterInst, tree, enter.getEnv(parent.sym));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
