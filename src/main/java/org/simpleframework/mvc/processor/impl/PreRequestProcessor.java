package org.simpleframework.mvc.processor.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

/**
 * 请求预处理，包括编码以及路径处理
 * @ClassName PreRequestProcessor
 * @Description TODO
 * @Author lh
 * @Date 2023/2/26
 **/
public class PreRequestProcessor implements RequestProcessor {
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        return false;
    }
}
