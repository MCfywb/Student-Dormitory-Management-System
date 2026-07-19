package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生详细信息实体类
 */
@Data
@TableName("student_info")
public class StudentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String studentNo;

    private String realName;

    private String gender;

    private String phone;

    private String email;

    private String college;

    private String major;

    private String className;

    private String grade;

    private String idCard;

    private Long roomId;

    private String roomNumber;

    private Long buildingId;

    private String buildingName;

    private Integer bedNumber;

    private LocalDateTime checkInTime;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
