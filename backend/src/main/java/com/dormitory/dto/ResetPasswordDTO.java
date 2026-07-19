package com.dormitory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码DTO
 */
@Data
public class ResetPasswordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String studentNo;
    private String phone;
    private String newPassword;
}
