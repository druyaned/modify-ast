package druyaned.modifyast.util;

import static com.sun.tools.javac.code.Flags.FINAL;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Flags.STATIC;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import static druyaned.modifyast.util.NameGeneratorEnexrinter.DEFAULT_ENEXRINTER_NAME;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InsertField {
    
    private final TreeMaker treeMaker;
    private final Names names;
    private final MemberEnter memberEnterInst;
    private final Enter enter;
    private final Method memberEnterMeth;
    private final JCFieldAccess enexrinterType;
    
    public InsertField(JavacProcessingEnvironment jcProcEnv) {
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
        JCExpression druyanedIdent = treeMaker.Ident(names.fromString("druyaned"));
        JCFieldAccess select = treeMaker.Select(druyanedIdent, names.fromString("modifyast"));
        select = treeMaker.Select(select, names.fromString("enexrint"));
        select = treeMaker.Select(select, names.fromString("Enexrinter"));
        enexrinterType = select;
    }
    
    public JCVariableDecl insertEnexrinter(
            JCClassDecl classDecl,
            String enterFormat,
            String exitFormat,
            String outputFile
    ) {
        return insertEnexrinter(classDecl, enterFormat, exitFormat,
                outputFile, DEFAULT_ENEXRINTER_NAME);
    }
    
    public JCVariableDecl insertEnexrinter(
            JCClassDecl classDecl,
            String enterFormat,
            String exitFormat,
            String outputFile,
            String name
    ) {
        long flags = PUBLIC | STATIC | FINAL;
        List<JCExpression> args = List.<JCExpression>of(
                treeMaker.Literal(enterFormat),
                treeMaker.Literal(exitFormat),
                treeMaker.Literal(outputFile)
        );
        JCExpression init = treeMaker.NewClass(
                null, // encl
                List.<JCExpression>nil(), // typeargs
                enexrinterType, // clazz
                args, // List<JCExpression> args
                null // JCClassDecl def
        );
        JCVariableDecl enexrinterDecl = treeMaker.VarDef(
                treeMaker.Modifiers(flags), // mods
                names.fromString(name), // name
                enexrinterType, // vartype
                init // init
        );
        integration(enexrinterDecl, classDecl);
        return enexrinterDecl;
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
