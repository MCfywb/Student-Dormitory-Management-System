package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 来访登记实体类
 */
@Data
@TableName("visitor")
public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String visitorName;

    private String visitorPhone;

    private String visitorIdCard;

    private String visitReason;

    private Long buildingId;

    private String buildingName;

    private Long roomId;

    private String roomNumber;

    private Long visitedStudentId;

    private String visitedStudentName;

    private LocalDateTime visitTime;

    private LocalDateTime leaveTime;

    private String status;

    private Long registerId;

    private String registerName;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
