/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/14 15:22
 */
package com.nemo.server.bean;

import java.io.File;

/**
 * 全局变量定义
 * Created by Nemo on 2017/12/14.
 */
public class GlobalParams {

    /**
     * 定义全局量，用来标识当前的资源地址
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    /**
     * 关闭操作的命令，需要考虑这个命令执行的安全性
     */
    public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    /**
     * 默认启动端口
     */
    public static int DEFAULT_PORT = 8080;

    /**
     * 是否关闭当前服务的标志，如需关闭服务，则修改此标志为true即可
     */
    public static boolean SHUTDOWN = false;

}
