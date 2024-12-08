package druyaned.modifyast.enexrint.arg;

import java.lang.reflect.Field;

public class ExtractTarget extends AbstractExtractor {
    
    public ExtractTarget(String parent, String child) {
        super(parent, child);
    }
    
    @Override public Object getArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        if (child == null) {
            return target;
        }
        try {
            Field field = findField(target);
            if (field != null) {
                field.setAccessible(true);
                return field.get(target);
            } else {
                return target;
            }
        } catch (IllegalArgumentException | ReflectiveOperationException ex) {
            return target;
        }
    }
    
    private Field findField(Object target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getName().equals(child)) {
                return field;
            }
        }
        return null;
    }
    
}
