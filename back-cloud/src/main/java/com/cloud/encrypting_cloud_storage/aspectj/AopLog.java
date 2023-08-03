package com.cloud.encrypting_cloud_storage.aspectj;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 * @Description: TODO
 * @date 2022年04月25日 16:16
 */
@Component
@Aspect
@Slf4j
public class AopLog {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Log {
        // 线程id
        private String threadId;
        // 线程名称
        private String threadName;
        // ip
        private String ip;
        // url
        private String url;
        // http方法 GET POST PUT DELETE PATCH
        private String httpMethod;
        // 类方法
        private String classMethod;
        // 请求参数
        @JsonIgnore
        private Object requestParams;
        // 返回参数
        private Object result;
        // 接口耗时
        private Long timeCost;
        // 操作系统
        private String os;
        // 浏览器
        private String browser;
        // user-agent
        private String userAgent;
    }

    /**
     * 定义切点
     */
    @Pointcut("execution(public * com.cloud.encrypting_cloud_storage.controller.*Controller.*(..))")
    public void log() {

    }

    /**
     * 控制器方法日志
     * 
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        long starter = System.currentTimeMillis();
        Object result = point.proceed();
        String userAgentStr = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        final Log log1 = Log.builder().userAgent(userAgentStr).browser(userAgent.getBrowser().toString())
            .os(userAgent.getOs().toString())
            .threadId(Long.toString(Thread.currentThread().getId())).threadName(Thread.currentThread().getName())
            .ip(getIp(request)).url(request.getRequestURI())
            .classMethod(
                String.format("%s.%s", point.getSignature().getDeclaringTypeName(), point.getSignature().getName()))
            .requestParams(getNameAndValue(point)).httpMethod(request.getMethod()).result(result)
            .timeCost(System.currentTimeMillis() - starter).build();
        log.info("Request log info:{}", Jackson.toJsonString(log1));
        return result;
    }

    /**
     * 获取方法的参数名和值
     *
     * @param point 切入点
     * @return 参数名:值 字典
     */
    private Map<String, Object> getNameAndValue(ProceedingJoinPoint point) {
        final Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        // 获得参数名数组
        final String[] parameterNames = methodSignature.getParameterNames();
        // 获得参数值数组
        final Object[] args = point.getArgs();
        if (ArrayUtil.isEmpty(parameterNames) || ArrayUtil.isEmpty(args)) {
            return Collections.emptyMap();
        }
        if (parameterNames.length != args.length) {
            log.warn("{}方法参数和参数值数量不一致", methodSignature.getName());
            return Collections.emptyMap();
        }
        Map<String, Object> resMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            resMap.put(parameterNames[i], args[i]);
        }
        return resMap;
    }

    public static final String UNKNOWN = "unknown";
    public static final String localHost = "127.0.0.1";

    /**
     * 获取IP地址
     *
     * @param request 请求
     * @return IP地址
     */
    private String getIp(HttpServletRequest request) {
        String ip = null;
        // X-Forwarded-For：Squid 服务代理
        // Proxy-Client-IP：apache 服务代理
        // WL-Proxy-Client-IP：weblogic 服务代理
        // X-Real-IP：nginx服务代理
        ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (localHost.equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.warn(e.getMessage(), e);
            }
        }
        return ip;
    }

}
