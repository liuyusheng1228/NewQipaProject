package com.qipa.jetpackmvvm.network.utils.rsa;


import com.google.gson.Gson;

public class RSAJsonUtils{
    /**
     * 将String转成JSON
     * @param str 需要转JSON的字符串
     * @return 返回一个Object对象
     */
    public static String stringToJson(String str){
        return new Gson().toJson(str);
    }

}
