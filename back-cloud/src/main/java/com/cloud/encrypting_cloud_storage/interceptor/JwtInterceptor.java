package com.cloud.encrypting_cloud_storage.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.ApiResponse;
import com.cloud.encrypting_cloud_storage.service.UserService;
import com.cloud.encrypting_cloud_storage.util.MyJWTUtil;
import com.cloud.encrypting_cloud_storage.util.MyPathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author leon
 * @Description: 权限认证，检查用户是否登录，访问地址是否合法
 * @date 2022年04月22日 10:49
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //axios在发送请求之前需要先发送一个OPTIONS预请求，相当于请求两次，直接放行
        if ("OPTIONS".equals(request.getMethod().toString())) {
            return true; //true是直接放行，前端抓包会有options请求
            //false拒接访问，抓包就不会有options请求了

        }
        String[] split = MyPathUtil.splitTrim(request.getRequestURI());

        String token = request.getHeader("token");
        DecodedJWT decodedJWT = null;
        /**
         * 验证token有效性，代表当前是合法用户
         */
        try {
            decodedJWT = MyJWTUtil.verifyToken(token);
        } catch (ApiException e) {
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(JSONObject.toJSONString(ApiResponse.ofStatus(StatusEnum.NOT_LOGIN)));
            writer.flush();
            return false;
        }
//        /**
//         * 验证路径有效性
//         */

//        String id = String.valueOf(decodedJWT.getClaim("uid").asLong());
//        if (!id.equals(split[2])) {
//            response.setContentType("application/json");
//            PrintWriter writer = response.getWriter();
//            writer.write(JSONObject.toJSONString(ApiResponse.ofStatus(StatusEnum.NO_PERMISSIONS)));
//            writer.flush();
//            return false;
//        }
        return true;
    }
}
