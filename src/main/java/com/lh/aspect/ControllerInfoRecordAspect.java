package com.lh.aspect;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.DefaultAspect;

import java.lang.reflect.Method;

/**
 * @ClassName ControllerInfoRecordAspect
 * @Description TODO
 * @Author lh
 * @Date 2023/2/27
 **/
@Aspect(pointcut = "execution(* com.lh.controller.frontend..*.*(..))")
@Order(0)
@Slf4j
public class ControllerInfoRecordAspect extends DefaultAspect {
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("方法开始执行，执行的类是[{}]，执行的方法是[{}]，参数是[{}]", targetClass.getName(), method, args);
    }

    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        log.info("方法执行结束，执行的类是[{}]，执行的方法是[{}]，参数是[{}], 返回的值是[{}]", targetClass.getName(), method, args, returnValue);
        return returnValue;
    }

    @Override
    public void afterThrowing(Class<?> targetClass, Method method, Object[] args, Throwable e) throws Throwable {
        super.afterThrowing(targetClass, method, args, e);
    }
}
