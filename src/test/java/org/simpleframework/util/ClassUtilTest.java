package org.simpleframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @ClassName ClassUtilTest
 * @Description TODO
 * @Author lh
 * @Date 2023/2/11
 **/
public class ClassUtilTest {

    @DisplayName("提取目标类方法：extractPackageClass")
    @Test
    public void extractPackageClassTest(){
        Set<Class<?>> set = ClassUtil.extractPackageClass("com.lh.entity");
        System.out.println(set);
        Assertions.assertEquals(4, set.size());
    }

    @Test
    public void test1(){
        byte a = 127;
        System.out.println(a);
        String aa = "hh";
        aa += '1';
        dd(aa);
        System.out.println(aa);
    }

    public void dd(String aa){
        aa = "dd";
    }
}
