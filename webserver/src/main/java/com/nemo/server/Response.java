/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/14 15:06
 */
package com.nemo.server;

import com.nemo.server.bean.GlobalParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 响应对象，用来执行一些对请求端的反馈
 * Created by Nemo on 2017/12/14.
 */
public class Response {

    /**
     * 本次请求的outputStream对象
     */
    private OutputStream output;

    /**
     * 本次请求的相关处理对象
     */
    private  Request request;

    /**
     * 每次写出内容时，缓存的大小
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 设置写出流对象/请求参数
     * @param output
     */
    public Response(OutputStream output,Request request) {
        this.output = output;
        this.request = request;
    }

    /**
     * 发送一个静态资源给客户端，若本地服务器有对应的文件则返回，否则返回404页面
     */
    public void output() {
        byte[] buffer = new byte[BUFFER_SIZE];
        int ch;
        FileInputStream fis = null;
        try {
            //读取磁盘文件
            File file = new File(GlobalParams.WEB_ROOT, request.getUri());
            if(file.exists()) {     //如果文件存在，则返回文件
                fis = new FileInputStream(file);
                ch = fis.read(buffer);
                while(ch != -1) {
                    output.write(buffer, 0, ch);
                    ch = fis.read(buffer, 0, BUFFER_SIZE);
                }
            } else {        //文件不存在，则返回404 提示
                String errorMessage = "HTTP/1.1 404 File Not Found \r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 24\r\n" +
                        "\r\n" +
                        "<h1>File Not Found!</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
