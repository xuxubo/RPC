package edu.guidian.yurpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MokeServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObejct(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObejct(Class<?> type) {
        if(type.isPrimitive()) {
            if(type == boolean.class) {
                return false;
            } else if(type == byte.class) {
                return (byte)0;
            } else if(type == char.class) {
                return (char)0;
            } else if(type == short.class) {
                return (short)0;
            } else if(type == int.class) {
                return 0;
            } else if(type == long.class) {
                return 0L;
            } else if(type == float.class) {
                return 0f;
            } else if(type == double.class) {
                return 0d;
            }
        }
        return null;
    }

}
