package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.core.annotation.*;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName BeanContainer
 * @Description TODO
 * @Author lh
 * @Date 2023/2/11
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {

    /**
     * 存放所有被配置标记的目标对象的Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION = Arrays.asList(Component.class, Controller.class, Reposity.class, Service.class, Configuration.class, Aspect.class);

    private enum ContainerHolder{
        HOLDER;
        private BeanContainer instance;
        ContainerHolder(){
            instance = new BeanContainer();
        }
    }

    /**
     * 获取存放Bean容器实例
     * @return
     */
    public static BeanContainer getInstance(){
        return ContainerHolder.HOLDER.instance;
    }

    /**
     * 容器是否被加载过
     */
    private boolean loaded = false;

    public boolean isLoaded(){
        return loaded;
    }

    public int mapSize(){
        return beanMap.size();
    }

    /**
     *
     * @param packageName
     */
    public synchronized void loadBeans(String packageName){
        if (isLoaded()){
            log.warn("BeanContainer has been loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if(ValidationUtil.isEmpty(classSet)){
            log.warn("extract nothing from packageName " + packageName);
            return;
        }
        for(Class<?> clazz:classSet){
            for(Class<? extends Annotation> annotation: BEAN_ANNOTATION){
                //如果类上面标记了定义的注解
                if (clazz.isAnnotationPresent(annotation)){
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
                ;
            }
        }
        loaded = true;
    }

    /**
     * 往容器添加bean
     * @param clazz
     * @param obj
     * @return
     */
    public Object addBean(Class<?> clazz, Object obj){
        return beanMap.put(clazz, obj);
    }

    /**
     * 删除bean
     * @param clazz
     * @return
     */
    public Object removeBean(Class<?> clazz){
        return beanMap.remove(clazz);
    }

    /**
     * 获得bean
     * @param clazz
     * @return
     */
    public Object getBean(Class<?> clazz){
        return beanMap.get(clazz);
    }

    /**
     * 获取所有Class
     * @return
     */
    public Set<Class<?>> getClasses(){
        return beanMap.keySet();
    }

    /**
     * 获取所有bean
     * @return
     */
    public Set<Object> getBeans(){
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解获取所有class对象
     * @param annotation
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation){
        //1、获取beanMap中的所有对象
        Set<Class<?>> keySet = getClasses();
        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2、通过注解筛选被注解标记的class对象，并添加到classSet里面
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz: keySet){
            //类是否有相关的注解标记
            if (clazz.isAnnotationPresent(annotation)){
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身
     * @param interfaceOrClass
     * @return
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        //1、获取beanMap中的所有对象
        Set<Class<?>> keySet = getClasses();
        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2、判断，并添加到classSet里面
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz: keySet){
            //判断keySet里的元素是否是传入的接口或者类的子类
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)){
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }
}
