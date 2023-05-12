package org.simpleframework.util;

/**
 * @ClassName ConverterUtil
 * @Description TODO
 * @Author lh
 * @Date 2023/2/27
 **/
public class ConverterUtil {

    /**
     * 返回基本数据类型的空值
     * 需要特殊处理的基本类型即int/long/boolean/byte/float/double/short
     * @param type
     * @return
     */
    public static Object primitiveNull(Class<?> type) {
        if (type == int.class || type == double.class ||
                type == short.class || type == long.class ||
                type == byte.class || type == float.class){
            return 0;
        }else if (type == boolean.class){
            return false;
        }
        return null;
    }

    /**
     * String类型转换成对应的参数类型
     * @param type
     * @param requestValue
     * @return
     */
    public static Object convert(Class<?> type, String requestValue) {
        if (isPrimitive(type)){
            if (ValidationUtil.isEmpty(requestValue)){
                return primitiveNull(type);
            }
            if (type.equals(int.class) || type.equals(Integer.class)){
                return Integer.parseInt(requestValue);
            } else if (type.equals(double.class) || type.equals(Double.class)){
                return Double.parseDouble(requestValue);
            } else if (type.equals(float.class) || type.equals(Float.class)){
                return Float.parseFloat(requestValue);
            } else if (type.equals(long.class) || type.equals(Long.class)){
                return Long.parseLong(requestValue);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)){
                return Boolean.parseBoolean(requestValue);
            } else if (type.equals(short.class) || type.equals(Short.class)){
                return Short.parseShort(requestValue);
            } else if (type.equals(byte.class) || type.equals(Byte.class)){
                return Byte.parseByte(requestValue);
            }
            return requestValue;
        }else {
            throw new RuntimeException("can not support non primitive type conversion yet");
        }
    }

    /**
     * 判定是否是基本数据类型（包括包装类以及String）
     * @param type
     * @return
     */
    private static boolean isPrimitive(Class<?> type) {
        return type == boolean.class
                || type == Boolean.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == String.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class;
    }


}
