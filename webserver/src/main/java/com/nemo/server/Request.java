/* 
 * All rights Reserved, Designed By 微迈科技
 * 2017/12/14 15:06
 */
package com.nemo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求相关处理，用来得到一些请求参数一类的操作
 * Created by Nemo on 2017/12/14.
 */
public class Request {

    /**
     * 本次请求的输入流
     */
    private InputStream input;

    /**
     * 本次请求的uri
     */
    private String uri;

    /**
     * 本次请求的url，带参数的uri
     */
    private String url;

    /**
     * 本次请求的请求头的信息
     */
    private Map<String,String> header = new HashMap<String, String>();

    /**
     * 本次请求的地址栏参数
     */
    private Map<String,String> requestParameters = new HashMap<String, String>();

    /**
     * 本次请求的请求体参数
     */
    private Map<String,Object> requestAttributes = new HashMap<String, Object>();

    /**
     * cookie列表
     */
    private Map<String,String> cookie = new HashMap<String, String>();

    /**
     *
     * @param input
     */
    public Request(InputStream input) {
        this.input = input;
    }

    /**
     * 解析请求内容
     */
    public void parser() {

        //得到请求数据
        String requestData = getRequestData();

        //开始解析
        parser(requestData);

        //打印一下
        pritln();
    }

    /**
     * 输出一些参数
     */
    public void pritln(){
        System.out.println("请求地址栏参数：");
        if(requestParameters!=null){
            for(String key : requestParameters.keySet()){
                System.out.println(key + "=" + requestParameters.get(key));
            }
        }
        System.out.println("请求体参数：");
        if(requestAttributes!=null){
            for(String key : requestAttributes.keySet()){
                System.out.println(key + "=" + requestAttributes.get(key));
            }
        }
        System.out.println("请求头参数：");
        if(header!=null){
            for(String key : header.keySet()){
                System.out.println(key + "=" + header.get(key));
            }
        }
        System.out.println("请求带的Cookie：");
        if(cookie!=null){
            for(String key : cookie.keySet()){
                System.out.println(key + "=" + cookie.get(key));
            }
        }
    }

    /**
     * 得到请求数据
     * @return
     */
    private String getRequestData(){
        StringBuffer request = new StringBuffer();
        byte[] buffer = new byte[2048];
        int i = 0;

        try {
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }

        for(int k = 0; k < i; k++) {
            request.append((char)buffer[k]);
        }

        return request.toString();
    }

    /**
     * 开始解析
     * @param requestData
     */
    private void parser(String requestData){
        parserUri(requestData);
    }

    /**
     * 解析请求地址
     * @param requestData
     * @return
     */
    private void parserUri(String requestData) {
        int index1 = requestData.indexOf(' ');
        if(index1 != -1) {
            int index2 = requestData.indexOf(' ', index1 + 1);
            if(index2 > index1) {
                uri = requestData.substring(index1 + 1, index2);
                url = uri;
                if(uri!=null){
                    String tempStr[] = uri.split("\\?");
                    //提取到地址
                    if(tempStr.length>0){
                        uri = tempStr[0];
                    }

                    //含有参数,开始提取
                    if(tempStr.length>1){
                       parserParameters(tempStr[1]);
                    }
                }
            }
        }

        //请求头处理
        parserHeader(requestData);
    }

    /**
     * 解析地址栏参数
     * @param paramsStr
     */
    private void parserParameters(String paramsStr){
        if(paramsStr==null){
            return;
        }
        //多个参数使用&作为分割
        String params[] = paramsStr.split("&");
        if(params == null || params.length<=0){
            return;
        }

        for(String param : params){
            String paramDetail[] = param.split("=");
            if(paramDetail.length<=1){
                //不存在，赋值null
                requestParameters.put(param,null);
            }else{
                requestParameters.put(paramDetail[0],paramDetail[1]);
            }
        }
    }

    /**
     * 解析请求头
     * @param requestData
     * @return
     */
    private void parserHeader(String requestData){
        try {
            StringReader reader = new StringReader(requestData);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            //标志第一行不需要读取
            boolean doNext = false;
            boolean isAttributes = false;
            StringBuffer arrtibutesStrBuffer = new StringBuffer();
            while((str=bufferedReader.readLine())!=null){
                if(doNext && !isAttributes){
                   String tempStrs[] = str.split(": ");
                   String name = tempStrs[0];
                   if(!name.equals("")) { //这部分是请求头
                       String value = str.replaceFirst(tempStrs[0] + ": ", "");
                       if(name.equals("Cookie")) {
                            //Cookie解析
                            parserCookie(value);
                       }else{
                           header.put(name, value);
                       }
                   }else{       //下面这部分是请求体参数
                        isAttributes = true;
                   }
                }else if(isAttributes){
                    arrtibutesStrBuffer.append(str);
                }
                doNext = true;
            }

            //开始解析请求体参数
            parserAttributes(arrtibutesStrBuffer.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 解析cookie
     * @param cookieStr
     */
    private void parserCookie(String cookieStr){
        if(cookieStr==null){
            return;
        }
        String cookies[] = cookieStr.split("; ");
        for(String str : cookies){
            if(str == null){
                continue;
            }
            String cookieArr[] = str.split("=");
            if(cookieArr.length>1) {
                cookie.put(cookieArr[0], str.replaceFirst(cookieArr[0]+"=",""));
            }
        }
    }

    /**
     * 解析请求头参数
     * @param requestData
     */
    private void parserAttributes(String requestData){
        if(requestData==null || requestData.equals("")){
            return;
        }

        try {
            StringReader reader = new StringReader(requestData);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if(str!=null){
                    String[] split = str.split("&");
                    if(str!=null){
                        for(String tempStr : split){
                            if(tempStr!=null) {
                                String tempSplit[] = tempStr.split("=");
                                if(tempSplit.length>0) {
                                    requestAttributes.put(tempSplit[0], tempStr.replaceFirst(tempSplit[0] + "=", ""));
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUri() {
        return uri;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public Map<String, Object> getRequestAttributes() {
        return requestAttributes;
    }
}
