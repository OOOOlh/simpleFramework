package org.simpleframework.mvc.processor;

import org.simpleframework.mvc.RequestProcessorChain;

/**
 * 请求执行器
 * @ClassName RequestProcessor
 * @Description TODO
 * @Author lh
 * @Date 2023/2/26
 **/
public interface RequestProcessor {
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}
