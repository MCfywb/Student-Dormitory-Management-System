package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.dto.LoginDTO;
import com.dormitory.dto.PasswordDTO;
import com.dormitory.dto.RegisterDTO;
import com.dormitory.dto.ResetPasswordDTO;
import com.dormitory.entity.StudentInfo;
import com.dormitory.entity.User;
import com.dormitory.service.StudentInfoService;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentInfoService studentInfoService;

    //防止数据安全，传用户的密码或者用户名
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        
        if ("student".equals(user.getRole())) {
            StudentInfo studentInfo = studentInfoService.getByUserId(user.getId());
            data.put("studentInfo", studentInfo);
        }
        
        return Result.success("登录成功", data);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        try {
            userService.updatePassword(passwordDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            userService.resetPassword(resetPasswordDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info/{userId}")
    public Result<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        
        if ("student".equals(user.getRole())) {
            StudentInfo studentInfo = studentInfoService.getByUserId(userId);
            data.put("studentInfo", studentInfo);
        }
        
        return Result.success(data);
    }
}
