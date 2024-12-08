package druyaned.modifyast;

import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import druyaned.modifyast.anno.Enexrint;
import druyaned.modifyast.anno.EnexrintAll;
import druyaned.modifyast.anno.Getter;
import druyaned.modifyast.anno.GetterAll;
import druyaned.modifyast.handle.HandleEnexrint;
import druyaned.modifyast.handle.HandleEnexrintAll;
import druyaned.modifyast.handle.HandleGetter;
import druyaned.modifyast.handle.HandleGetterAll;
import druyaned.modifyast.util.NameGeneratorEnexrinter;
import druyaned.modifyast.util.InsertField;
import druyaned.modifyast.util.InsertMethod;
import druyaned.modifyast.util.InsertStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Annotations from package {@code druyaned.modifyast.anno} are handled
 * by this annotation processor, which delegates each annotation handling
 * to corresponding handler.
 * A handler uses an internal API of module {@code jdk.compiler}.
 * 
 * @author druyaned
 */
@SupportedAnnotationTypes("druyaned.modifyast.anno.*")
public class ModificationProcessor extends AbstractProcessor {
    
    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
    
    @Override public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv
    ) {
        JavacProcessingEnvironment jcProcEnv = (JavacProcessingEnvironment)processingEnv;
        Trees trees = Trees.instance(jcProcEnv);
        handleGetter(roundEnv, jcProcEnv, trees);
        handleEnexrint(roundEnv, jcProcEnv, trees);
        return true;
    }
    
    private void handleGetter(
            RoundEnvironment roundEnv,
            JavacProcessingEnvironment jcProcEnv,
            Trees trees
    ) {
        // @GetterAll
        InsertMethod methodInserter = new InsertMethod(jcProcEnv);
        HandleGetterAll handleGetterAll = new HandleGetterAll(methodInserter);
        for (Element elem : roundEnv.getElementsAnnotatedWith(GetterAll.class)) {
            Tree leaf = trees.getPath(elem).getLeaf();
            if (leaf instanceof JCClassDecl classDecl) {
                handleGetterAll.setClassDecl(classDecl).handle();
            }
        }
        // @Getter
        HandleGetter handleGetter = new HandleGetter(methodInserter);
        for (Element elem : roundEnv.getElementsAnnotatedWith(Getter.class)) {
            Tree parent = trees.getPath(elem).getParentPath().getLeaf();
            Tree leaf = trees.getPath(elem).getLeaf();
            if (
                    parent instanceof JCClassDecl classDecl
                    && leaf instanceof JCVariableDecl varDecl
            ) {
                handleGetter
                        .setClassDecl(classDecl)
                        .setVarDecl(varDecl)
                        .handle();
            }
        }
    }
    
    private void handleEnexrint(
            RoundEnvironment roundEnv,
            JavacProcessingEnvironment jcProcEnv,
            Trees trees
    ) {
        // @EnexrintAll
        InsertField fieldInserter = new InsertField(jcProcEnv);
        InsertStatement statInserter = new InsertStatement(jcProcEnv);
        HandleEnexrintAll handleEnexrintAll = new HandleEnexrintAll(fieldInserter, statInserter);
        for (Element elem : roundEnv.getElementsAnnotatedWith(EnexrintAll.class)) {
            Tree leaf = trees.getPath(elem).getLeaf();
            if (leaf instanceof JCClassDecl classDecl) {
                handleEnexrintAll.setClassDecl(classDecl).handle();
            }
        }
        // @Enexrint
        Map<JCClassDecl, NameGeneratorEnexrinter> classToNameGen = new HashMap<>();
        HandleEnexrint handleEnexrint = new HandleEnexrint(
                fieldInserter,
                statInserter,
                classToNameGen
        );
        for (Element elem : roundEnv.getElementsAnnotatedWith(Enexrint.class)) {
            Tree parent = trees.getPath(elem).getParentPath().getLeaf();
            Tree leaf = trees.getPath(elem).getLeaf();
            if (
                    parent instanceof JCClassDecl classDecl
                    && leaf instanceof JCMethodDecl methodDecl
            ) {
                handleEnexrint
                    .setClassDecl(classDecl)
                    .setMethodDecl(methodDecl)
                    .handle();
            }
        }
    }
    
}
