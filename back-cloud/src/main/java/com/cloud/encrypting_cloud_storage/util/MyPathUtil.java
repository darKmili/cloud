package com.cloud.encrypting_cloud_storage.util;

import com.cloud.encrypting_cloud_storage.exceptions.ApiException;

/**
 * @author leon
 * @Description: 路径工具类
 * @date 2022年05月02日 13:31
 */
public class MyPathUtil {
    public static String[] splitTrim(String path){
        if(path==null){
            throw new ApiException(-1,"空路径异常");
        }
        if(path.charAt(0)=='/'){
            path=path.substring(1);
        }
        if(path.charAt(path.length()-1)=='/'){
            path = path.substring(0,path.length()-1);
        }
        return path.split("/");
    }
}
