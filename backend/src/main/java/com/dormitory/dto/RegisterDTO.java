package com.dormitory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String realName;
    private String gender;
    private String phone;
    private String email;
    private String studentNo;
    private String college;
    private String major;
    private String className;
    private String grade;
}
