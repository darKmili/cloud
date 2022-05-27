package com.cloud.encrypting_cloud_storage.util;

import java.util.Random;

/**
 * @author leon
 * @Description: 自动用户名工具
 * @date 2022年05月01日 18:47
 */
public class MyStringUtil {
    /**
     * 生成随机字符串，数字和字母组成,
     * @param length 长度
     * @return 字符串
     */
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }


    public static Long getId(String userId){
        return Long.parseLong(userId.replace("cloud_",""));
    }


}
