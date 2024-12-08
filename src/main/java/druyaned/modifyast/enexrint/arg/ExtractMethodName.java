package druyaned.modifyast.enexrint.arg;

public class ExtractMethodName extends AbstractExtractor {
    
    public ExtractMethodName(String parent, String child) {
        super(parent, child);
    }
    
    @Override public Object getArg(Object target, String methodName) {
        return methodName;
    }
    
}
