package com.qipa.jetpackmvvm.network.utils.rsa;


public class DecryptUtils {
    /**
     * 解密工具类
     * @param str 接收的参数
     * @return 返回一个解密的String类型的JSON
     * @throws Exception
     */
    public static String dncrypt(String str) throws Exception {
        String decrypt = RSAUtils.decrypt(str);
        return RSAJsonUtils.stringToJson(decrypt);
    }
}
