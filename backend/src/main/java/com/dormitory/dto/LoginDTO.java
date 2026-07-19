package com.dormitory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求DTO
 * DTO（Data Transfer Object）层
 * DTO 是 数据传输对象 ，是一种设计模式，用于在不同层之间传输数据。
 */
@Data
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
}
