/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/14 15:05
 */
package com.nemo.server;

import com.nemo.server.bean.GlobalParams;
import com.nemo.server.thread.DealRequestThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Http服务入口相关
 * Created by Nemo on 2017/12/14.
 */
public class HttpServer {

    /**
     * 入口主函数
     * @param args
     */
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start();
    }

    /**
     * 启动服务器，并接收用户请求进行处理
     */
    private void  start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(GlobalParams.DEFAULT_PORT, 1, InetAddress.getByName("127.0.0.1"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("服务启动成功，监听端口："+GlobalParams.DEFAULT_PORT);
        //开始处理请求
        dealRequest(serverSocket);
    }

    /**
     * 开始处理请求
     * @param serverSocket
     */
    private void dealRequest(ServerSocket serverSocket){
        while(!GlobalParams.SHUTDOWN) {
            try {
                //创建socket,等待请求
                Socket socket = serverSocket.accept();
                //如果有请求，则直接放到线程中直接处理即可
                Thread thread = new Thread(new DealRequestThread(socket));
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
                //异常不终止所有进程，应立即继续下一个请求
                continue;
            }
        }
        System.out.println("服务器关闭成功");
    }

}
