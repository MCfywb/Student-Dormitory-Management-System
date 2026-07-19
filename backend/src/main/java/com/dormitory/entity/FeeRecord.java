package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 费用记录实体类
 */
@Data
@TableName("fee_record")
public class FeeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private Long feeTypeId;

    private String feeTypeName;

    private BigDecimal amount;

    private String payStatus;

    private LocalDateTime payTime;

    private String payMethod;

    private String academicYear;

    private String semester;

    private LocalDate dueDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
