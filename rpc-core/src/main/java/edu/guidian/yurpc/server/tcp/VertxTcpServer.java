package edu.guidian.yurpc.server.tcp;

import edu.guidian.yurpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * TCP服务器实现
 */
public class VertxTcpServer implements HttpServer {


    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();

        //处理请求
//        server.connectHandler(socket -> {
//            socket.handler(buffer -> {
//                //处理接收到的字节数据
//                byte[] requestData = buffer.getBytes();
//                byte[] responseData = handleRequest(requestData);
//                socket.write(Buffer.buffer(responseData));
//            });
//        });
        server.connectHandler(new TcpServerHandler());

        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("Server is now listening on port:" + port);
            }else{
                System.out.println("Failed to start server:" + result.cause());
            }
        });
    }

    private byte[] handleRequest(byte[] requestData) {

        return "hello,client!".getBytes();
    }

//    public static void main(String[] args) {
//        new VertxTcpServer().doStart(8888);
//
//    }

}
