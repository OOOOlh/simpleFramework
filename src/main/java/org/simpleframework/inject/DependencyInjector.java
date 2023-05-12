package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.io.File;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @ClassName DependencyInjector
 * @Description TODO
 * @Author lh
 * @Date 2023/2/12
 **/
@Slf4j
public class DependencyInjector {
    /**
     * Bean容器
     */
    private BeanContainer beanContainer;
    public DependencyInjector(){
        beanContainer = BeanContainer.getInstance();
    }

    public void doIoc(){
        //1、遍历Bean容器中所有的Class对象
        if (ValidationUtil.isEmpty(beanContainer.getClasses())){
            log.warn("empty classes in BeanContainer");
            return;
        }
        for (Class<?> clazz: beanContainer.getClasses()){
            //2、遍历所有Class对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)){
                continue;
            }
            //3、找出被Autowired标记的成员变量
            for (Field field: fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    //4、获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    //5、获取这些成员变量的类型在容器里对应的实例
                    Object fieldValue = getFieldInstance(fieldClass, autowiredValue);
                    if (fieldValue == null){
                        throw new RuntimeException("unable to inject relevant type, target fieldClass is: "+ fieldClass.getName());
                    }
                    //6、通过反射将对应的成员变量实例注入到成员变量所在类的实例
                    Object targetBean = beanContainer.getBean(clazz);
                    ClassUtil.setField(field, targetBean, fieldValue, true);
                }
            }

        }

    }

    /**
     * 根据Class在beanContainer里获取其实例或实现类
     * @param fieldClass
     * @return
     */
    private Object getFieldInstance(Class<?> fieldClass, String autowiredValue) {
        Object fieldValue = beanContainer.getBean(fieldClass);
        if (fieldValue != null){
            return fieldValue;
        }else {
            //成员变量是父类或接口，那么根据父类或接口找到子类
            Class<?> implementedClass = getImplementClass(fieldClass, autowiredValue);
            if (implementedClass != null){
                return beanContainer.getBean(implementedClass);
            }else {
                return null;
            }
        }
    }

    /**
     * 获取接口的实现类
     * @param fieldClass
     * @return
     */
    private Class<?> getImplementClass(Class<?> fieldClass, String autowiredValue) {
        //获取fieldClass的所有子类
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(fieldClass);
        //如果有子类
        if (!ValidationUtil.isEmpty(classSet)){
            //如果没有指定具体的实现类
            if (ValidationUtil.isEmpty(autowiredValue)){
                if (classSet.size() == 1){
                    return classSet.iterator().next();
                }else {
                    //如果多于两个实现类且用户未指定其中一个实现类，则抛出异常
                    throw new RuntimeException("multiple implemented classes for " + fieldClass.getName() + " please set @Autowired's value");
                }
            }else {
                for (Class<?> clazz: classSet){
                    if (autowiredValue.equals(clazz.getSimpleName())){
                        return clazz;
                    }else {
                        throw new RuntimeException("can not find class " + autowiredValue + ", please check it");
                    }
                }
            }
        }
        return null;
    }
}












