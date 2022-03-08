package com.qipa.jetpackmvvm.network.utils.rsa;

import java.util.List;

public class RSAParameter<T> {

    /**
     * 将格式转化为String
     * @param t 未知类型的参数
     * @return 返回一个String的值
     */
    public String toString(T t){
        return "{" + String.valueOf(t) +"}";
    }

    /**
     * 将格式转化为String
     * @param t1 未知类型的参数1
     * @param t2 未知类型的参数2
     * @return 返回一个String
     */
    public String toString(T t1, T t2){
        String str = "{" + String.valueOf(t1) + "," +String.valueOf(t2) + "}";
        return str;
    }

    /**
     * 将格式转化为String
     * @param list 未知类型的List参数
     * @return 返回一个String
     */
    public String toString(List<T> list){
        String str = "{";
        for (T t : list){
            if (t.equals(list.get(list.size()-1))){
                str += t;
                break;
            }
            str += t + ",";
        }
        str += "}";
        return str;
    }


}
