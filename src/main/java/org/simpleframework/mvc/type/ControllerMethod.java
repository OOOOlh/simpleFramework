package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 待执行的Controller及其方法实例和参数映射
 * @ClassName ControllerMethod
 * @Description TODO
 * @Author lh
 * @Date 2023/2/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerMethod {
    //Controller对应的Class对象
    private Class<?> controllerClass;

    //执行的Controller方法实例
    private Method invokedMethod;

    //方法参数名称以及对应的参数类型
    private Map<String, Class<?>> methodParameters;
}
