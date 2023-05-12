package org.simpleframework.core;

import com.lh.entity.bo.HeadLine;
import com.lh.entity.bo.ShopCategory;
import org.junit.jupiter.api.*;
import org.simpleframework.core.annotation.Component;

/**
 * @ClassName BeanContainerTest
 * @Description TODO
 * @Author lh
 * @Date 2023/2/11
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {
    private static BeanContainer beanContainer;
    @BeforeAll
    //所有UT开始之前初始化
    static void init(){
        beanContainer = BeanContainer.getInstance();
    }

    @Test
    @Order(1)
    public void loadBeansTest(){
        Assertions.assertEquals(false, beanContainer.isLoaded());
        beanContainer.loadBeans("com.lh.entity");
        Assertions.assertEquals(2, beanContainer.mapSize());
    }

    @Test
    @Order(2)
    public void getBeanTest(){
        HeadLine headLine = (HeadLine)beanContainer.getBean(HeadLine.class);
        Assertions.assertEquals(true, headLine instanceof HeadLine);

        ShopCategory shopCategory = (ShopCategory)beanContainer.getBean(ShopCategory.class);
        Assertions.assertEquals(null, shopCategory);
    }

    @Order(3)
    @Test
    public void getClassesByAnnotationTest(){
        Assertions.assertEquals(true, beanContainer.isLoaded());
        Assertions.assertEquals(2, beanContainer.getClassesByAnnotation(Component.class).size());
    }
}
