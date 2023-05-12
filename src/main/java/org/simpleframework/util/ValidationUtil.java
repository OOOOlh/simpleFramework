package org.simpleframework.util;

import java.util.Collection;
import java.util.Map;

/**
 * @ClassName Validation
 * @Description TODO
 * @Author lh
 * @Date 2023/2/11
 **/
public class ValidationUtil {

    /**
     * String判空
     * @param obj
     * @return
     */
    public static boolean isEmpty(String obj){
        return obj == null || "".equals(obj);
    }

    /**
     * Array 判空
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object[] obj){
        return obj == null || obj.length == 0;
    }

    /**
     * Collection判空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    /**
     * Map判空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map){
        return map == null || map.isEmpty();
    }
}
