package com.cloud.encrypting_cloud_storage.controller;

import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import com.cloud.encrypting_cloud_storage.models.ApiResponse;
import com.cloud.encrypting_cloud_storage.models.dto.AuthenticationRequest;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.util.MyJWTUtil;
import com.cloud.encrypting_cloud_storage.util.MyStringUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author leon
 * @Description: 登录注册控制器
 * @date 2022年04月08日 20:25
 */
@RestController
@RequestMapping("/users")
@Slf4j
@Api(tags = "用户控制器", value = "用户管理")
public class UserController extends BaseController {


    /**
     * 检查用户是否存在，并返回用户的随机码
     *
     * @param authenticationRequest 请求
     * @return 随机码
     */
    @PostMapping("/check")
    @ApiOperation(value = "检查用户是否存在")
    @ApiImplicitParams({@ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String")})
    @ApiResponses({@io.swagger.annotations.ApiResponse(code = 2000,message = "响应成功",response = ApiResponse.class)})
    public ApiResponse checkUser(@RequestBody AuthenticationRequest authenticationRequest) {
        String randomValue = null;
        String email = authenticationRequest.getEmail();
        if (email == null || email.length() == 0) {
            return ApiResponse.ofStatus(StatusEnum.INVALID_REQUEST);
        }
        randomValue = userService.findRandomValueByEmail(email);
        if (randomValue == null) {
            return ApiResponse.ofStatus(StatusEnum.NO_USER);
        }
        log.info("------------" + randomValue);
        return ApiResponse.ofSuccess(randomValue);
    }


    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public ApiResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        // 验证用户
        UserPo userPo = authenticationService.authentication(authenticationRequest);
        String token = MyJWTUtil.createToken(userPo);
        userPo.setToken(token);
        return ApiResponse.ofSuccess(userPo);
    }

    /**
     * 用户注册
     *
     * @param userPo 用户对象
     * @return ApiResponse
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public ApiResponse register( @RequestBody UserPo userPo) {
        // 查看用户是否存在
        UserPo userPoByEmail = userService.findUserByEmail(userPo.getEmail());
        if (userPoByEmail != null) {
            return ApiResponse.ofStatus(StatusEnum.HAVE_USER);
        }
        // 初始化用户
        UserPo initUser = userService.initUser(userPo);
        return ApiResponse.ofSuccess(initUser);
    }


    @GetMapping("/{userPath}")
    @ApiOperation(value = "获取用户详细信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "userPath", value = "用户路径", dataTypeClass = String.class)})
    public ApiResponse findInfoById(@PathVariable(value = "userPath") String userId) {
        UserPo userPo = userService.findById(MyStringUtil.getId(userId));
        return ApiResponse.ofSuccess(userPo);
    }

    @GetMapping("/{userPath}/logout")
    @ApiOperation(value = "用户退出登录")
    public ApiResponse logout(@PathVariable(value = "userPath") String userId, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        MyJWTUtil.invalidToken(token);
        return ApiResponse.ofSuccess();
    }

}
