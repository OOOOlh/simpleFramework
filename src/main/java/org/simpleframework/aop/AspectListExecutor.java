package org.simpleframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName AspectListExecutor
 * @Description TODO
 * @Author lh
 * @Date 2023/2/24
 **/
public class AspectListExecutor implements MethodInterceptor {

    //被代理的类
    private Class<?> targetObject;

    @Getter
    private List<AspectInfo> sortedAspectInfoList;

    public AspectListExecutor(Class<?> targetObject, List<AspectInfo> aspectInfoList) {
        this.targetObject = targetObject;
        this.sortedAspectInfoList = sortAspectInfoList(aspectInfoList);
    }

    //按照order的值进行升序排序，确保order小的值先被织入
    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        Collections.sort(aspectInfoList, new Comparator<AspectInfo>() {
            @Override
            public int compare(AspectInfo o1, AspectInfo o2) {
                return o1.getOrderIndex() - o2.getOrderIndex();
            }
        });
        return aspectInfoList;
    }

    //这里应该是会遍历目标类的所有方法
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue = null;
        //将目标方法不是该method的切面删除
        //粗筛只能筛选出类，精筛才能筛选出方法
        collectAccurateMatchedAspectList(method);
        if (ValidationUtil.isEmpty(sortedAspectInfoList)) return null;
        //1、按照order升序的顺序执行完Aspect的before方法
        invokeBeforeAspects(method, args);
        //2、执行被代理类的方法
        try {
            returnValue = methodProxy.invokeSuper(proxy, args);
            //3、如果被代理方法正常返回，则按照order的顺序降序执行完所有Aspect的afterReturning方法
            returnValue = invokeAfterReturningAspects(method, args, returnValue);
        }catch (Exception e){
            e.printStackTrace();
            //4、如果被代理方法抛出异常，则按照order的顺序降序执行完所有Aspect的afterThrowing方法
            invokeAfterThrowingAspects(method, args, e);
        }
        return returnValue;
    }

    private void collectAccurateMatchedAspectList(Method method) {
        if (ValidationUtil.isEmpty(sortedAspectInfoList)) return;
        sortedAspectInfoList.removeIf(aspectInfo -> !aspectInfo.getPointcutLocator().accurateMatches(method));
    }

    private void invokeBeforeAspects(Method method, Object[] args) throws Throwable {
        for (AspectInfo a: sortedAspectInfoList){
            a.getAspectObject().before(targetObject, method, args);
        }
    }

    private Object invokeAfterReturningAspects(Method method, Object[] args, Object returnValue) throws Throwable {
        Object result = null;
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--){
            result = sortedAspectInfoList.get(i).getAspectObject().afterReturning(targetObject, method, args, returnValue);
        }
        return result;
    }

    private void invokeAfterThrowingAspects(Method method, Object[] args, Exception e) throws Throwable {
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--){
            sortedAspectInfoList.get(i).getAspectObject().afterThrowing(targetObject, method, args, e);
        }
    }
}
