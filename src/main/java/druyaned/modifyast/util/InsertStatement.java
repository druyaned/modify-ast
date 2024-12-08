package druyaned.modifyast.util;

import static com.sun.source.tree.CaseTree.CaseKind.RULE;
import static com.sun.source.tree.CaseTree.CaseKind.STATEMENT;
import static com.sun.tools.javac.code.Flags.STATIC;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement ;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCSynchronized ;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;

public class InsertStatement {
    
    private final TreeMaker treeMaker;
    private final Names names;
    private final JCLiteral NULL_LITERAL;
    
    public InsertStatement(JavacProcessingEnvironment jcProcEnv) {
        Context context = jcProcEnv.getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        NULL_LITERAL = treeMaker.Literal("null");
    }
    
    public JCExpressionStatement insertEnterPrint(
            JCClassDecl classDecl,
            JCMethodDecl methodDecl,
            JCVariableDecl enexrinterDecl
    ) {
        JCExpressionStatement enterPrintStat = getPrintStat(
                methodDecl,
                enexrinterDecl,
                "enterPrint"
        );
        List<JCStatement> stats = methodDecl.body.stats;
        JCStatement[] newStats = new JCStatement[stats.size() + 1];
        if (
                methodDecl.sym.getKind() == CONSTRUCTOR
                && !stats.isEmpty()
                && stats.get(0) instanceof JCExpressionStatement jcExprStat
                && jcExprStat.expr instanceof JCMethodInvocation jcMethInvoc
                && jcMethInvoc.meth instanceof JCIdent jcIdent
                && jcIdent.name.toString().equals("super")
        ) {
            newStats[0] = stats.get(0);
            newStats[1] = enterPrintStat;
            for (int i = 1; i < stats.size(); i++) {
                newStats[i + 1] = stats.get(i);
            }
        } else {
            newStats[0] = enterPrintStat;
            for (int i = 0; i < stats.size(); i++) {
                newStats[i + 1] = stats.get(i);
            }
        }
        methodDecl.body.stats = List.from(newStats);
        return enterPrintStat;
    }
    
    public JCStatement insertExitPrint(
            JCClassDecl classDecl,
            JCMethodDecl methodDecl,
            JCVariableDecl enexrinterDecl
    ) {
        JCExpressionStatement exitPrintStat = getPrintStat(
                methodDecl,
                enexrinterDecl,
                "exitPrint"
        );
        if (methodDecl.body.stats.isEmpty()) {
            methodDecl.body.stats = List.<JCStatement>of(exitPrintStat);
            return exitPrintStat;
        } // now stats.size() > 0
        breadthBypassForJCReturn(methodDecl, exitPrintStat);
        return exitPrintStat;
    }
    
    private void breadthBypassForJCReturn(
            JCMethodDecl methodDecl,
            JCExpressionStatement exitPrintStat
    ) {
        class Node {
            final JCTree tree;
            final JCTree parent;
            Node(JCTree tree, JCTree parent) {
                this.tree = tree;
                this.parent = parent;
            }
        }
        Deque<Node> deque = new Deque<>();
        for (JCStatement stat : methodDecl.body.stats) {
            deque.pushLast(new Node(stat, methodDecl.body));
        }
        while (!deque.isEmpty()) {
            Node node = deque.popFirst();
            JCTree tree = node.tree;
            JCTree parent = node.parent;
            if (tree instanceof JCBlock jcBlock) {
                for (JCStatement subtree : jcBlock.stats) {
                    deque.pushLast(new Node(subtree, tree));
                }
            }
            if (tree instanceof JCDoWhileLoop jcDoWhileLoop) {
                deque.pushLast(new Node(jcDoWhileLoop.body, tree));
            }
            if (tree instanceof JCWhileLoop jcWhileLoop) {
                deque.pushLast(new Node(jcWhileLoop.body, tree));
            }
            if (tree instanceof JCForLoop jcForLoop) {
                deque.pushLast(new Node(jcForLoop.body, tree));
            }
            if (tree instanceof JCEnhancedForLoop jcEnhancedForLoop) {
                deque.pushLast(new Node(jcEnhancedForLoop.body, tree));
            }
            if (tree instanceof JCLabeledStatement jcLabeledStat) {
                deque.pushLast(new Node(jcLabeledStat.body, tree));
            }
            if (tree instanceof JCSwitch jcSwitch) {
                for (JCCase jcCase : jcSwitch.cases) {
                    deque.pushLast(new Node(jcCase, tree));
                }
            }
            if (tree instanceof JCCase jcCase) {
                if (jcCase.caseKind == STATEMENT) {
                    for (JCStatement subtree : jcCase.stats) {
                        deque.pushLast(new Node(subtree, tree));
                    }
                }
                if (jcCase.caseKind == RULE) {
                    deque.pushLast(new Node(jcCase.body, tree));
                }
            }
            if (tree instanceof JCSynchronized jcSynchronized) {
                deque.pushLast(new Node(jcSynchronized.body, tree));
            }
            if (tree instanceof JCTry jcTry) {
                deque.pushLast(new Node(jcTry.body, tree));
                for (JCCatch jcCatch : jcTry.catchers) {
                    deque.pushLast(new Node(jcCatch, tree));
                }
                if (jcTry.finalizer != null) {
                    deque.pushLast(new Node(jcTry.finalizer, tree));
                }
            }
            if (tree instanceof JCCatch jcCatch) {
                deque.pushLast(new Node(jcCatch.body, tree));
            }
            if (tree instanceof JCIf jcIf) {
                deque.pushLast(new Node(jcIf.thenpart, tree));
                if (jcIf.elsepart != null) {
                    deque.pushLast(new Node(jcIf.elsepart, tree));
                }
            }
            if (tree instanceof JCReturn jcReturn) {
                insertBeforeReturn(exitPrintStat, jcReturn, parent);
            }
        }
    }
    
    private void insertBeforeReturn(
            JCExpressionStatement exitPrintStat,
            JCReturn jcReturn,
            JCTree parent
    ) {
        if (parent instanceof JCBlock jcBlock) {
            List<JCStatement> blockStats = jcBlock.stats;
            JCStatement[] newStats = new JCStatement[blockStats.size() + 1];
            int i = 0;
            for (; i < blockStats.size() && blockStats.get(i) != jcReturn; i++) {
                newStats[i] = blockStats.get(i);
            }
            newStats[i] = exitPrintStat;
            for (; i < blockStats.size(); i++) {
                newStats[i + 1] = blockStats.get(i);
            }
            jcBlock.stats = List.from(newStats);
        }
        if (parent instanceof JCDoWhileLoop jcDoWhileLoop) {
            jcDoWhileLoop.body = blockFrom(exitPrintStat, jcReturn);
        }
        if (parent instanceof JCWhileLoop jcWhileLoop) {
            jcWhileLoop.body = blockFrom(exitPrintStat, jcReturn);
        }
        if (parent instanceof JCForLoop jcForLoop) {
            jcForLoop.body = blockFrom(exitPrintStat, jcReturn);
        }
        if (parent instanceof JCEnhancedForLoop jcEnhancedForLoop) {
            jcEnhancedForLoop.body = blockFrom(exitPrintStat, jcReturn);
        }
        if (parent instanceof JCLabeledStatement jcLabeledStat) {
            jcLabeledStat.body = blockFrom(exitPrintStat, jcReturn);
        }
        if (parent instanceof JCCase jcCase) {
            // jcCase.caseKind = STATEMENT; jcCase.body = null
            List<JCStatement> caseStats = jcCase.stats;
            JCStatement[] newStats = new JCStatement[caseStats.size() + 1];
            int i = 0;
            for (; i < caseStats.size() && caseStats.get(i) != jcReturn; i++) {
                newStats[i] = caseStats.get(i);
            }
            newStats[i] = exitPrintStat;
            for (; i < caseStats.size(); i++) {
                newStats[i + 1] = caseStats.get(i);
            }
            jcCase.stats = List.from(newStats);
        }
        if (parent instanceof JCIf jcIf) {
            if (jcIf.thenpart == jcReturn) {
                jcIf.thenpart = blockFrom(exitPrintStat, jcReturn);
            }
            if (jcIf.elsepart == jcReturn) {
                jcIf.elsepart = blockFrom(exitPrintStat, jcReturn);
            }
        }
    }
    
    private JCBlock blockFrom(JCExpressionStatement exitPrintStat, JCReturn jcReturn) {
        JCStatement[] newStats = new JCStatement[2];
        newStats[0] = exitPrintStat;
        newStats[1] = jcReturn;
        return treeMaker.Block(0, List.from(newStats));
    }
    
    private JCExpressionStatement getPrintStat(
            JCMethodDecl methodDecl,
            JCVariableDecl enexrinterDecl,
            String printMethodName
    ) {
        boolean isConstructor = methodDecl.sym.getKind() == CONSTRUCTOR;
        boolean isStatic = (methodDecl.mods.flags & (long)STATIC) != 0;
        String methodName
                = isConstructor
                ? methodDecl.sym.owner.name.toString()
                : methodDecl.name.toString();
        List<JCExpression> args
                = isConstructor || isStatic
                ? List.of(NULL_LITERAL, treeMaker.Literal(methodName))
                : List.of(
                        treeMaker.Ident(names.fromString("this")),
                        treeMaker.Literal(methodName)
                );
        List<JCExpression> typeargs = List.nil();
        JCFieldAccess select = treeMaker.Select(
                treeMaker.Ident(enexrinterDecl.name),
                names.fromString(printMethodName)
        );
        JCMethodInvocation methInvoc = treeMaker.Apply(typeargs, select, args);
        return treeMaker.Exec(methInvoc);
    }
    
}
