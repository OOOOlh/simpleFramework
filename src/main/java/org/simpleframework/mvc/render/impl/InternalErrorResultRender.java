package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * 内部异常渲染器
 * @ClassName InternalErrorResultRender
 * @Description TODO
 * @Author lh
 * @Date 2023/2/27
 **/
public class InternalErrorResultRender implements ResultRender {
    private String errMsg;

    public InternalErrorResultRender(String errMsg){
        this.errMsg = errMsg;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errMsg);
    }
}
