package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.dto.LoginDTO;
import com.dormitory.dto.PasswordDTO;
import com.dormitory.dto.RegisterDTO;
import com.dormitory.dto.ResetPasswordDTO;
import com.dormitory.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    User login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    void updatePassword(PasswordDTO passwordDTO);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    User getUserByUsername(String username);

    Page<User> getManagerPage(Integer current, Integer size, String username, String realName, Integer status);

    void addManager(User user);

    void updateManager(User user);

    void deleteManager(Long id);
}
