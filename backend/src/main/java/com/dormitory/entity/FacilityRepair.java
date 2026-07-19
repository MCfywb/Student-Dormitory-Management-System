package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设施维修记录实体类
 */
@Data
@TableName("facility_repair")
public class FacilityRepair implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long facilityId;

    private String facilityName;

    private Long roomId;

    private String roomNumber;

    private String buildingName;

    private Long reporterId;

    private String reporterName;

    private LocalDateTime reportTime;

    private String problemDesc;

    private String status;

    private Long handlerId;

    private String handlerName;

    private LocalDateTime handleTime;

    private String handleResult;

    private BigDecimal repairCost;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
