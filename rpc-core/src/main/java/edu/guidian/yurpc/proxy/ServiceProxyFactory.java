package edu.guidian.yurpc.proxy;

import edu.guidian.yurpc.RpcApplication;
import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    public static <T> T getProxy(Class<T> serviceClass) {

        // serviceClass: moke userService
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    private static <T> T getMockProxy(Class<T> serviceClass) {
        /**
         * Proxy.newProxyInstance 是 Java 动态代理机制的核心方法，
         * 用于在运行时生成代理对象。它位于 java.lang.reflect.Proxy 类中，
         * 通过实现指定接口的代理类，拦截方法调用并将其交由 InvocationHandler 处理。
         * 参数说明
         * loader:
         *
         * 类型: ClassLoader
         * 作用: 用于加载代理类的类加载器。通常传递目标类的类加载器。
         * 推荐值: target.getClass().getClassLoader()，其中 target 是被代理对象。
         * interfaces:
         *
         * 类型: Class<?>[]
         * 作用: 代理类需要实现的接口数组。必须是接口类型，不能是类。
         * 推荐值: target.getClass().getInterfaces()，或明确列出接口类。
         * h:
         *
         * 类型: InvocationHandler
         * 作用: 代理对象的处理器，所有方法调用都会被转发到 h.invoke。
         */
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MokeServiceProxy()
        );
    }

}
