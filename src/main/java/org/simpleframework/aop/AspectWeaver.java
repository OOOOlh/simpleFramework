package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @ClassName AspectWeaver
 * @Description 筛选出两种Bean：一种是被代理的Bean，一种是切面Bean
 * @Author lh
 * @Date 2023/2/24
 **/
public class AspectWeaver {
    private BeanContainer beanContainer;
    public AspectWeaver(){
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop(){
        //1、获取所有切面类
        //每一个切面类代表一个切入对象
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) return;
        //2、拼装AspectInfoList
        List<AspectInfo> aspectInfoList = packAspectInfoList(aspectSet);
        //3、遍历容器里的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        for (Class<?> targetClass: classSet){
            //排除AspectClass自身
            if (targetClass.isAnnotationPresent(Aspect.class)){
                continue;
            }
            //4、粗筛符合条件的Aspect
            //根据pointLocator的解析来粗筛类
            List<AspectInfo> roughMatchedAspectList = collectRoughMatchedAspectListForSpecificClass(aspectInfoList, targetClass);

            //5、尝试进行Aspect的织入
            wrapIfNecessary(roughMatchedAspectList, targetClass);
        }

        /**
         * sprint aop1.0
         */
//        //2、将切面类按照不同的组织目标进行切分
//        Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap = new HashMap<>();
//        if(ValidationUtil.isEmpty(aspectSet)){return;}
//        for (Class<?> aspectClass: aspectSet){
//            if(verifyAspect(aspectClass)){
//                categorizeAspect(categorizedMap, aspectClass);
//            }else {
//                throw new RuntimeException("@Aspect and @Order have not been added to the Aspect.class");
//            }
//        }
//        //3、按照不同的组织目标分别去按序织入Aspect的逻辑
//        if (ValidationUtil.isEmpty(categorizedMap)){return;}
//        for (Class<? extends Annotation> category: categorizedMap.keySet()){
//            weaveByCategory(category, categorizedMap.get(category));
//        }
    }

    private void wrapIfNecessary(List<AspectInfo> aspectInfoList, Class<?> targetClass){
        //创建动态代理对象
        AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfoList);
        Object proxyObject = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        beanContainer.addBean(targetClass, proxyObject);
    }


    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList = new ArrayList<>();
        for (AspectInfo aspectInfo: aspectInfoList){
            //粗筛
            if (aspectInfo.getPointcutLocator().roughMatches(targetClass)){
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }

    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        for (Class<?> aspectClass: aspectSet){
            DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);
            Order orderTag = aspectClass.getAnnotation(Order.class);
            Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
            PointcutLocator pointcutLocator = new PointcutLocator(aspectTag.pointcut());
            AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect, pointcutLocator);
            aspectInfoList.add(aspectInfo);
        }
        return aspectInfoList;
    }



    private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfos) {
        //1、获取被代理类的集合
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
        if (ValidationUtil.isEmpty(classSet)) return;
        //2、遍历被代理类，分别为每个被代理类生成动态代理实例
        for (Class<?> targetClass: classSet){
            //创建动态代理对象
            AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfos);
            Object proxyObject = ProxyCreator.createProxy(targetClass, aspectListExecutor);
            //3、将动态代理对象添加到容器中，取代未被代理前的类实例
            beanContainer.addBean(targetClass, proxyObject);
        }
    }

//    private void categorizeAspect(Map<Class<? extends Annotation> , List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
//        Order orderTag = aspectClass.getAnnotation(Order.class);
//        Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
//
//        DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);
//        AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect);
//        if (!categorizedMap.containsKey(aspectTag.value())){
//            //如果第一次出现
//            List<AspectInfo> aspectInfoList = new ArrayList<>();
//            aspectInfoList.add(aspectInfo);
//            categorizedMap.put(aspectTag.value(), aspectInfoList);
//        }else {
//            //如果不是，往jointpoint对应的value里添加新的Aspect逻辑
//            List<AspectInfo> aspectInfoList = categorizedMap.get(aspectTag.value());
//            aspectInfoList.add(aspectInfo);
//        }
//    }
//
//    //框架中一定要遵守给Aspect类添加@Aspect和@Order标签的规范，同时，必须继承自DefaultAspect.class
//    //此外，@Aspect属性的值不能是它本身
//    private boolean verifyAspect(Class<?> aspectClass) {
//        return aspectClass.isAnnotationPresent(Aspect.class) &&
//                aspectClass.isAnnotationPresent(Order.class) &&
//                DefaultAspect.class.isAssignableFrom(aspectClass) &&
//                aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
//    }
}
