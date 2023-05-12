package org.simpleframework.mvc.processor.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * jsp请求资源处理
 * @ClassName JspRequestProcessor
 * @Description TODO
 * @Author lh
 * @Date 2023/2/26
 **/
public class JspRequestProcessor implements RequestProcessor {
    //jsp请求的RequestDispatcher的名称
    public static final String JSP_SERVLET = "jsp";
    //jsp请求资源的路径
    public static final String JSP_RESOURCE_PREFIX = "/templates/";

    /**
     * jsp的RequestServlet，处理jsp资源
     */
    private RequestDispatcher jspServlet;

    public JspRequestProcessor(ServletContext servletContext){
        this.jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (this.jspServlet == null){
            throw new RuntimeException("There is no jsp servlet");
        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1、通过请求路径判断是否是请求的静态资源，webapp/static
        if (isJspResource(requestProcessorChain.getRequestPath())){
            //2、如果是静态资源，则将请求转发给default servlet处理
            jspServlet.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            return false;
        }
        return true;
    }

    private boolean isJspResource(String path) {
        return path.startsWith(JSP_RESOURCE_PREFIX);
    }
}
