package org.simpleframework.aop;

import com.lh.controller.frontend.MainPageController;
import com.lh.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

/**
 * @ClassName AspectWeaverTest
 * @Description TODO
 * @Author lh
 * @Date 2023/2/25
 **/
public class AspectWeaverTest {

    @DisplayName("AOP")
    @Test
    public void doAopTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.lh");
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();

        HeadLineOperationController headLineOperationController = (HeadLineOperationController) beanContainer.getBean(HeadLineOperationController.class);
        headLineOperationController.addHeadLine(null, null);
        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        mainPageController.getMainPageInfo(null, null);
        mainPageController.getTest(null, null);
    }

    static int i;
    int j;
    @Test
    public void test1(){
        i++;
        j++;
        System.out.println(i);
        System.out.println(j);
    }
}
