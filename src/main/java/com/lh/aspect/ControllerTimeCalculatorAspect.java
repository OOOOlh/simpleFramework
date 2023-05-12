package com.lh.aspect;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.annotation.Controller;

import java.lang.reflect.Method;

/**
 * @ClassName ControllerTimeCalculatorAspect
 * @Description TODO
 * @Author lh
 * @Date 2023/2/25
 **/
//@Aspect(pointcut = "within(com.lh.controller.superadmin.*)")
//@Aspect(value = Controller.class)
@Order(0)
@Slf4j
public class ControllerTimeCalculatorAspect extends DefaultAspect {
    private long timestampCache;
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("开始计时，执行的类是[{}]，执行的方法是[{}]，参数是[{}]", targetClass.getName(), method, args);
        timestampCache = System.currentTimeMillis();
    }

    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        long endTime = System.currentTimeMillis();
        long costTime = endTime - timestampCache;
        log.info("结束计时，执行的类是[{}]，执行的方法是[{}]，参数是[{}], 返回的值是[{}]，所用的时间是[{}]", targetClass.getName(), method, args, returnValue, costTime);
        return returnValue;
    }

    @Override
    public void afterThrowing(Class<?> targetClass, Method method, Object[] args, Throwable e) throws Throwable {
        super.afterThrowing(targetClass, method, args, e);
    }
}
