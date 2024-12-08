package druyaned.modifyast.enexrint.arg;

import java.time.LocalDateTime;

public class ExtractTime extends AbstractExtractor {
    
    public ExtractTime(String parent, String child) {
        super(parent, child);
    }
    
    @Override public Object getArg(Object target, String methodName) {
        return LocalDateTime.now();
    }
    
}
