package com.wkclz.server.util;

import cn.hutool.core.util.ReflectUtil;
import com.wkclz.server.annotation.NettyHandlerMethod;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象处理工具
 */
@Component
public final class NettyHandlerMethodUtil {

    @Resource
    private ApplicationContext context;

    private List<NettyHandlerMethodUtil.ObjectMethod<NettyHandlerMethod>> objectHandlers;

    @PostConstruct
    void init(){
        objectHandlers = getObjects(NettyHandlerMethod.class);
    }

    /**
     * 对象方法调用配置类
     * @param <T>
     */
    @Data
    @AllArgsConstructor
    public static class ObjectMethod<T>{
        /**
         * 调用方法对象
         */
        private MethodHandle methodHandle ;
        /**
         * 服务类对象
         */
        private Object object;
        /**
         * 注解配置信息
         */
        private T annotation;
    }
    public List<Object> invoke(Object data){
        try {
            List<Object> results = new ArrayList<Object>();
            for (ObjectMethod<NettyHandlerMethod> o : objectHandlers) {
                try {
                    Object input = getNewValue(data, o.annotation.inputConvert());
                    Object result = o.methodHandle.bindTo(o.object).invoke(input);
                    //Object result = o.method.invoke(o.object,input);
                    Object output = getNewValue(result, o.annotation.outputConvert());
                    results.add(output);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return results;
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    private Object getNewValue(Object oldValue,String methonName){
        Object v = oldValue;
        if(!methonName.isEmpty()){
            v = ReflectUtil.invoke(ConvertUtil.SINGLETON,methonName,oldValue);
        }
        return v;
    }

    public List<ObjectMethod<NettyHandlerMethod>> getObjects(Class annotationClass){
        try {
            List<ObjectMethod<NettyHandlerMethod>> objectHandlers = new ArrayList<>();
            String[] names = context.getBeanDefinitionNames();
            for (String name : names) {
                if (name.contains("Service") || name.contains("service")) {
                    Object obj = context.getBean(name);
                    addMethod(objectHandlers, obj, annotationClass);
                }
            }
            return objectHandlers;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void addMethod(List<ObjectMethod<NettyHandlerMethod>> objectHandlers,Object obj,Class annotationClass) throws Exception {
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                Class[] parameterTypes = method.getParameterTypes();
                Class returnTypes = method.getReturnType();
                MethodType signature = MethodType.methodType(returnTypes,parameterTypes);
                MethodHandle mh = MethodHandles.lookup().findVirtual(obj.getClass(), method.getName(), signature);
                objectHandlers.add(new ObjectMethod(mh,obj,method.getAnnotation(annotationClass)));
            }
        }
    }

}
