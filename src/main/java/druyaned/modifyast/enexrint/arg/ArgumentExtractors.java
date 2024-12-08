package druyaned.modifyast.enexrint.arg;

import java.util.HashMap;
import java.util.Map;

public class ArgumentExtractors {
    
    private static final Map<String, ArgumentExtractorConstructor>
            parentToConstructor = new HashMap<>();
    
    static {
        parentToConstructor.put("${methodName}", ExtractMethodName::new);
        parentToConstructor.put("${target}", ExtractTarget::new);
        parentToConstructor.put("${thread}", ExtractThread::new);
        parentToConstructor.put("${time}", ExtractTime::new);
    }
    
    public static ArgumentExtractor create(String parent, String child) {
        return parentToConstructor.get(parent).create(parent, child);
    }
    
    public static boolean contains(String parent) {
        return parentToConstructor.containsKey(parent);
    }
    
}
