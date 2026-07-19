package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告通知实体类
 */
@Data
@TableName("notice")
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String noticeType;

    private Long publisherId;

    private String publisherName;

    private LocalDateTime publishTime;

    private Integer status;

    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
