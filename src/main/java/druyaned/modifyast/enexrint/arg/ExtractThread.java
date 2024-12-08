package druyaned.modifyast.enexrint.arg;

import static java.lang.Thread.currentThread;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ExtractThread extends AbstractExtractor {
    
    private static final Map<String, Method> CHILD_TO_THREAD_GETTER = new HashMap<>();
    
    private static void putThreadChild(String getterName) throws NoSuchMethodException {
        CHILD_TO_THREAD_GETTER.put(getterName, Thread.class.getDeclaredMethod(getterName));
    }
    
    static {
        try {
            putThreadChild("getName");
            putThreadChild("getPriority");
            putThreadChild("getState");
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private final Method threadGetter;
    
    public ExtractThread(String parent, String child) {
        super(parent, child);
        threadGetter = child == null ? null : CHILD_TO_THREAD_GETTER.get(child);
    }
    
    @Override public Object getArg(Object target, String methodName) {
        try {
            return threadGetter == null ? currentThread() : threadGetter.invoke(currentThread());
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return currentThread();
        }
    }
    
}
