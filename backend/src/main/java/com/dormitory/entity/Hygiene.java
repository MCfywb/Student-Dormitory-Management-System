package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宿舍卫生检查实体类
 */
@Data
@TableName("hygiene")
public class Hygiene implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private String roomNumber;

    private Long buildingId;

    private String buildingName;

    private LocalDate checkDate;

    private Integer score;

    private String level;

    private Long checkerId;

    private String checkerName;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
