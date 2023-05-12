package org.simpleframework.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.mock.mock1;
import org.simpleframework.aop.mock.mock2;
import org.simpleframework.aop.mock.mock3;
import org.simpleframework.aop.mock.mock4;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AopTest
 * @Description TODO
 * @Author lh
 * @Date 2023/2/24
 **/
public class AspectListExecutorTest {

//    @DisplayName("Aspect排序：sortAspectTest")
//    @Test
//    public void orderTest(){
//        List<AspectInfo> aspectInfoList = new ArrayList<>();
//        aspectInfoList.add(new AspectInfo(0, new mock2()));
//        aspectInfoList.add(new AspectInfo(4, new mock1()));
//        aspectInfoList.add(new AspectInfo(2, new mock3()));
//        aspectInfoList.add(new AspectInfo(3, new mock4()));
//
//        AspectListExecutor aspectListExecutor = new AspectListExecutor(AspectListExecutorTest.class, aspectInfoList);
//        List<AspectInfo> resultAspectInfoList = aspectListExecutor.getSortedAspectInfoList();
//        for (AspectInfo a: resultAspectInfoList){
//            System.out.println(a.getOrderIndex());
//        }
//    }
}
