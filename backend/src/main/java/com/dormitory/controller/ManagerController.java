package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.Result;
import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public Result<Page<User>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        Page<User> page = userService.getManagerPage(current, size, username, realName, status);
        return Result.success(page);
    }

    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        try {
            userService.addManager(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        try {
            userService.updateManager(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            userService.deleteManager(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
