package org.simpleframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @ClassName DefaultAspect
 * @Description TODO
 * @Author lh
 * @Date 2023/2/24
 **/
public abstract class DefaultAspect {

    //模板模式下的钩子方法，可以不做实现。实现与否由用户决定
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable{

    }

    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable{
        return returnValue;
    }

    public void afterThrowing(Class<?> targetClass, Method method, Object[] args, Throwable e) throws Throwable{

    }
}
