package druyaned.modifyast.util;

import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class NameGeneratorGetter {
    
    public static String defaultName(JCVariableDecl varDecl) {
        String varName = varDecl.name.toString();
        return "get" + Character.toUpperCase(varName.charAt(0)) + varName.substring(1);
    }
    
}
