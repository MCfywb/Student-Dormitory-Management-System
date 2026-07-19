package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设施实体类
 */
@Data
@TableName("facility")
public class Facility implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String facilityName;

    private String facilityType;

    private Long roomId;

    private String roomNumber;

    private Long buildingId;

    private String buildingName;

    private LocalDate purchaseDate;

    private BigDecimal price;

    private String status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
