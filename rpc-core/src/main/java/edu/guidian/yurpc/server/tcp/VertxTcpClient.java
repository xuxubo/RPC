package edu.guidian.yurpc.server.tcp;

import io.vertx.core.Vertx;

public class VertxTcpClient {

    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", result -> {
            if(result.succeeded()){
                System.out.println("Connected to server");
                io.vertx.core.net.NetSocket socket = result.result();
                //发送数据
                socket.write("Hello from client");
                //响应
                socket.handler(buffer -> {
                    System.out.println("response: " + buffer.toString());
                });
            }else {
                System.err.println("Failed to connect to TCP server ");

            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
