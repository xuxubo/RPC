package edu.guidian.yurpc.bootstrap;

import edu.guidian.yurpc.RpcApplication;

public class ConsumerBootstrap {

    public static void  init(){
        //RPC框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
