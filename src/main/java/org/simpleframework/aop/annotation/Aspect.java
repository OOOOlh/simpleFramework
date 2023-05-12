package org.simpleframework.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//表示切面类
public @interface Aspect {
    String pointcut();
//    //表示被当前注解标签标记的类，会被注入相关的横切逻辑。比如标签@Controller等
//    Class<? extends Annotation> value();
}
