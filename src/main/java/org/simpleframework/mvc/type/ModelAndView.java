package org.simpleframework.mvc.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ModelAndView
 * @Description 存储处理完后的结果数据，以及显示该数据的视图
 * @Author lh
 * @Date 2023/2/28
 **/
public class ModelAndView {
    //页面所在的位置
    @Getter
    private String view;

    //页面的data数据
    @Getter
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView setView(String view){
        this.view = view;
        return this;
    }

    //modelAndView.setView("addheadline.jsp").addViewData("aaa", "bbb")
    public ModelAndView addViewData(String attributeName, Object attributeValue){
        model.put(attributeName, attributeValue);
        return this;
    }
}
