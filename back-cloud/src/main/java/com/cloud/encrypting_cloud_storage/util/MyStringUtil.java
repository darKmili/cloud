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
     * 
     * @param length 长度
     * @return 字符串
     */
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }


    public static Long getId(String userId) {
        return Long.parseLong(userId.replace("cloud_", ""));
    }

    public static String byteToString(byte[] data) {
        int len = data.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i]);
        }
        return sb.toString();
    }




    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn
     * 需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     * 本方法不处理任何异常，所有异常全部抛出
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte)Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    public static int bytesToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
    }

}
