package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.dto.LoginDTO;
import com.dormitory.dto.PasswordDTO;
import com.dormitory.dto.RegisterDTO;
import com.dormitory.dto.ResetPasswordDTO;
import com.dormitory.entity.StudentInfo;
import com.dormitory.entity.User;
import com.dormitory.mapper.StudentInfoMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public User login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getPassword, loginDTO.getPassword())
                .eq(User::getStatus, 1);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        User existUser = getUserByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRealName(registerDTO.getRealName());
        user.setGender(registerDTO.getGender());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole("student");
        user.setStatus(1);
        user.setAvatar("/upload/default-avatar.png");
        this.save(user);

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setUserId(user.getId());
        studentInfo.setStudentNo(registerDTO.getStudentNo());
        studentInfo.setRealName(registerDTO.getRealName());
        studentInfo.setGender(registerDTO.getGender());
        studentInfo.setPhone(registerDTO.getPhone());
        studentInfo.setEmail(registerDTO.getEmail());
        studentInfo.setCollege(registerDTO.getCollege());
        studentInfo.setMajor(registerDTO.getMajor());
        studentInfo.setClassName(registerDTO.getClassName());
        studentInfo.setGrade(registerDTO.getGrade());
        studentInfo.setStatus("pending");
        studentInfoMapper.insert(studentInfo);
    }

    @Override
    public void updatePassword(PasswordDTO passwordDTO) {
        User user = this.getById(passwordDTO.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(passwordDTO.getOldPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordDTO.getNewPassword());
        this.updateById(user);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = getUserByUsername(resetPasswordDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!resetPasswordDTO.getPhone().equals(user.getPhone())) {
            throw new RuntimeException("手机号不匹配");
        }
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getUserId, user.getId());
        StudentInfo studentInfo = studentInfoMapper.selectOne(wrapper);
        if (studentInfo == null) {
            throw new RuntimeException("学生信息不存在");
        }
        if (!resetPasswordDTO.getStudentNo().equals(studentInfo.getStudentNo())) {
            throw new RuntimeException("学号不匹配");
        }
        user.setPassword(resetPasswordDTO.getNewPassword());
        this.updateById(user);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public Page<User> getManagerPage(Integer current, Integer size, String username, String realName, Integer status) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "manager");
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addManager(User user) {
        user.setRole("manager");
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getAvatar() == null) {
            user.setAvatar("/upload/default-avatar.png");
        }
        this.save(user);
    }

    @Override
    public void updateManager(User user) {
        User existUser = this.getById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        existUser.setRealName(user.getRealName());
        existUser.setGender(user.getGender());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        existUser.setStatus(user.getStatus());
        this.updateById(existUser);
    }

    @Override
    public void deleteManager(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        this.removeById(id);
    }
}
