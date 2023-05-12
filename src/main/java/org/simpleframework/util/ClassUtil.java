package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 获取包内的类的集合
     * 使用Class泛型是为了避免问题
     * @param packageName
     * @return
     */
    public static Set<Class<?>> extractPackageClass(String packageName){
        //获取到类的加载器
        ClassLoader classLoader = getClassLoader();
        //通过类加载器获取到加载的资源信息
        //获取到绝对路径,URL表示统一资源定位，表示一个指向资源的指针
        URL url = classLoader.getResource(packageName.replace(".", "//"));
        if(url == null){
            log.warn("unable to retrieve anything from package: " + packageName);
            return null;
        }
        //根据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        //过滤出文件类型的资源
        if(url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)){
            classSet = new HashSet<Class<?>>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }


    /**
     * 递归获取目标文件夹下的所有类
     * @param emptyClassSet
     * @param fileSource  父目录
     * @param packageName
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if(!fileSource.isDirectory()){
            return;
        }

        //如果是一个文件夹，则调用其listFiles方法获取文件夹下的文件或文件夹
        //获取到当前路径下所有文件夹，只加载目录或者.class文件
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对路径
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(".class")) {
                        //如果是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            //根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absolutePath) {
                //从class文件的绝对值路径里提取类路径
                absolutePath = absolutePath.replace(File.separator, ".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                //从反射机制获取对应的Class并加入到classSet中
                Class targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
            }
        });

        //依次遍历并递归文件夹
        //这里如果files为空会异常
        if (files != null){
            for (File f: files){
                extractClassFile(emptyClassSet, f, packageName);
            }
        }

    }

    /**
     * 获取ClassLoader
     * @return
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取Class对象
     * @param className class全名=package+类名
     * @return
     */
    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param clazz
     * @param accessible 用户决定是否可访问
     * @param <T> class的类型
     * @return
     */
    public static <T>T newInstance(Class<?> clazz, boolean accessible){
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T)constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性值
     * @param field      成员变量
     * @param target     类实例
     * @param value      成员变量值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field, Object target, Object value, boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("set Field error", e);
            throw new RuntimeException(e);
        }
    }
}
