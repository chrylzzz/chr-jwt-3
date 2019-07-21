package com.chryl.con;

import com.alibaba.fastjson.JSONObject;
import com.chryl.anno.CheckToken;
import com.chryl.anno.PassToken;
import com.chryl.bean.User;
import com.chryl.bean.UserModel;
import com.chryl.mapper.UserMapper;
import com.chryl.utils.JwtUtil;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 使用postman
 * <p>
 * Created By Chr on 2019/7/19.
 */
@Api(value = "用户接口", description = "login/get")
@RestController
@RequestMapping("/v2/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    //登陆不验证jwt,只生成jwt
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "密码", required = true, dataType = "String")
    })
    @PassToken
    @PostMapping(value = "/login")
    public Object login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {

        JSONObject jsonObject = new JSONObject();
        User user = userMapper.selectByUserName(username);
        if (user == null) {
            jsonObject.put("message", "登录失败,用户不存在");
            return jsonObject;
        } else {
            if (!password.equals(user.getUserPassword())) {
                jsonObject.put("message", "登录失败");
                return jsonObject;
            } else {
                String token = JwtUtil.createJWT(6000000, user);
                jsonObject.put("token", token);
                jsonObject.put("user", user);
                return jsonObject;
            }
        }
    }

    //查看个人信息,验证jwt
    @ApiImplicitParam(paramType = "path", name = "id", value = "用户id", required = true, dataType = "String")
    @CheckToken
    @GetMapping("/getMessage/{id}")
    public String getMessage(@PathVariable("id") String id) {
        User user = userMapper.selectByUserId(id);
        return "你已通过验证:" + user.getUserName();
    }

    private UserModel convertUserModelFromUser(User user) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(user, userModel);
        return userModel;
    }

}
