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
            Response response = new Response(output);
            response.setRequest(request);
            response.output();

            //如果命令是终止服务，则尝试终止
            GlobalParams.SHUTDOWN = request.getUri().equals(GlobalParams.SHUTDOWN_COMMAND);
            socket.close();
        } catch (IOException e) {
            //错误只做异常堆栈输出，不向外抛出
            e.printStackTrace();
        }
    }
}
