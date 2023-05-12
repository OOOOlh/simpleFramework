package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储http请求路径和请求方法
 * @ClassName RequestPathInfo
 * @Description TODO
 * @Author lh
 * @Date 2023/2/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPathInfo {
    //http请求方法
    private String httpMethod;

    //http请求路径
    private String httpPath;
}
