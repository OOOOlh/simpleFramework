package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.impl.JsonResultRender;
import org.simpleframework.mvc.render.impl.ResourceNotFoundResultRender;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.impl.ViewResultRender;
import org.simpleframework.mvc.type.ControllerMethod;
import org.simpleframework.mvc.type.RequestPathInfo;
import org.simpleframework.util.ConverterUtil;
import org.simpleframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ControllerRequestProcessor
 * @Description TODO
 * @Author lh
 * @Date 2023/2/26
 **/
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    //IOC容器
    private BeanContainer beanContainer;

    //请求和controller方法的映射集合
    //todo key最好不要为对象，且这里是有问题的
    //可以改为String，或者重写RequestPathInfo的hashcode和equals方法
    private Map<RequestPathInfo, ControllerMethod> pathControllerMethodMap = new ConcurrentHashMap<>();

    public ControllerRequestProcessor(){
        this.beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMethodMap(requestMappingSet);
    }

    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) return;
        //1、遍历所有被@RequestMapping标记的类，获取类上面该注解的属性值作为一级路径
        for (Class<?> requestMappingClass: requestMappingSet){
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            String basePath = requestMapping.value();
            if (!basePath.startsWith("/")){
                basePath += "/";
            }

            //2、遍历类里所有被@RequestMapping标记的方法，获取方法上面该注解的属性值作为二级路径
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (ValidationUtil.isEmpty(methods)) continue;
            for (Method method: methods){
                //保证该方式有注解
                if (method.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodAnnotation.value();
                    if (!methodPath.startsWith("/")){
                        methodPath += "/";
                    }
                    String url = basePath + methodPath;

                    //3、解析方法里被@RequestParam标记的参数
                    //获取该注解的属性值，作为参数名
                    //获取被标记的参数的数据类型，建立参数名和参数类型的映射
                    Map<String, Class<?>> methodParams = new HashMap<>();
                    Parameter[] parameters = method.getParameters();
//                    if (ValidationUtil.isEmpty(parameters)) continue;
                    for (Parameter parameter: parameters) {
                        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                        //目前暂定为Controller方法里面所有参数都需要@RequestParam注解
                        if (null == annotation) {
                            throw new RuntimeException("The parameter must have @RequestParam");
                        }
                        methodParams.put(parameter.getName(), parameter.getType());
                    }
                    //4、将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放置到映射表里
                    String httpMethod = String.valueOf(methodAnnotation.method());

                    //请求方法 + 请求路径
                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);
                    if (this.pathControllerMethodMap.containsKey(requestPathInfo)){
                        log.warn("duplicate url:{} registration, current class {} method{} will override the former one",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }
                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);
                    pathControllerMethodMap.put(requestPathInfo, controllerMethod);

                }
            }
        }




    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1、解析HttpServletRequest的请求方法，请求路径，获取对应的ControllerMethod实例
        String method = requestProcessorChain.getRequestMethod();
        String path = requestProcessorChain.getRequestPath();
        //todo 这里是无法拿到的，不能在key中new对象
        ControllerMethod controllerMethod = this.pathControllerMethodMap.get(new RequestPathInfo(method, path));
        //
        if (null == controllerMethod){
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method, path));
            return false;
        }

        //2、解析请求参数，并传递给获取到的ControllerMethod实例去执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());

        //3、根据解析的结果，选择对应的render进行渲染
        setResultRender(result, controllerMethod, requestProcessorChain);
        return true;
    }

    /**
     * 根据不同情况设置不同的渲染器
     * @param result
     * @param controllerMethod
     * @param requestProcessorChain
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if (result == null){
            return;
        }

        ResultRender resultRender;
        //如果加了@ResponseBody注解，会被以json的方式返回
        boolean isJson = controllerMethod.getInvokedMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson){
            resultRender = new JsonResultRender(result);
        }else {
            resultRender = new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    /**
     * 根据路径执行相应的方法
     * @param controllerMethod
     * @param request
     * @return
     */
    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        //1、从请求里获取GET或者POST的参数名及对应的值
        Map<String, String> requestParamMap = new HashMap<>();

        //GET、POST方法的请求参数获取方式
        //TODO getParameterMap()方法的返回值具体形式
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> parameter: parameterMap.entrySet()){
            if (!ValidationUtil.isEmpty(parameter.getValue())){
                //只支持一个参数对应一个值的形式
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }

        //2、根据获取到的请求参数名及其对应的值，以及controllerMethod里面的参数和类型的映射关系，去实例化出方法对应的参数
        List<Object> methodParams = new ArrayList<>();

        //该方法的所有参数(参数名：参数类别)
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();

        //根据参数值建立参数对象
        for (String paramName: methodParamMap.keySet()){

            Class<?> type = methodParamMap.get(paramName);
            String requestValue = requestParamMap.get(paramName);
            Object value;
            //只支持String以及基础类型及其包装类型
            if (null == requestValue){
                //将请求里的参数值转成适配于参数类型的空值
                value = ConverterUtil.primitiveNull(type);
            }else{
                value = ConverterUtil.convert(type, requestValue);
            }
            methodParams.add(value);
        }

        //3、执行Controller里面对应的方法并返回结果
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokedMethod = controllerMethod.getInvokedMethod();
        invokedMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0){
                result = invokedMethod.invoke(controller);
            }else {
                result = invokedMethod.invoke(controller, methodParams.toArray());
            }
        }catch (InvocationTargetException e){
            //如果是调用异常的话，需要通过e.getTargetException()去获取执行方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
        return result;
    }
}
