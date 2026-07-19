package com.dormitory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码DTO
 */
@Data
public class PasswordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String oldPassword;
    private String newPassword;
}
