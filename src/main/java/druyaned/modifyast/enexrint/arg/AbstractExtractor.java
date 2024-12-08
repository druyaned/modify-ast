package druyaned.modifyast.enexrint.arg;

public abstract class AbstractExtractor implements ArgumentExtractor {
    
    protected final String parent;
    protected final String child;
    
    protected AbstractExtractor(String parent, String child) {
        this.parent = parent;
        this.child = child;
    }
    
}
