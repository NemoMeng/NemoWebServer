/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/14 15:33
 */
package com.nemo.server.thread;

import com.nemo.server.bean.GlobalParams;
import com.nemo.server.Request;
import com.nemo.server.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 处理请求的线程，每个请求都应由单独的线程来处理
 * Created by Nemo on 2017/12/14.
 */
public class DealRequestThread implements Runnable {

    private Socket socket;

    public DealRequestThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {

            Thread current = Thread.currentThread();
            System.out.println("当前处理线程：" + current.getName());

            //本次请求的输入流
            InputStream input = socket.getInputStream();

            //对于本次请求的输出流
            OutputStream output = socket.getOutputStream();

            //接收请求
            Request request = new Request(input);
            request.parser();

            //处理请求并返回结果
            Response response = new Response(output,request);
            response.output();

            socket.close();

            //如果命令是终止服务，则设定不再接收下一个网络请求
            GlobalParams.SHUTDOWN = request.getUri().equals(GlobalParams.SHUTDOWN_COMMAND);
            //尝试从线程关闭整个程序，这里可能还需要做一些退出前的保存操作
            if(GlobalParams.SHUTDOWN) {
                System.exit(0);
            }
        } catch (IOException e) {
            //错误只做异常堆栈输出，不向外抛出
            e.printStackTrace();
        }
    }
}
