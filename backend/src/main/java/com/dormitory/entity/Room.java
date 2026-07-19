package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宿舍房间实体类
 */
@Data
@TableName("room")
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roomNumber;

    private Long buildingId;

    private String buildingName;

    private Integer floor;

    private Integer capacity;

    private Integer currentCount;

    private String roomType;

    private String status;

    private BigDecimal price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private List<Integer> occupiedBeds;
    
    @TableField(exist = false)
    private Integer availableBeds;
}
