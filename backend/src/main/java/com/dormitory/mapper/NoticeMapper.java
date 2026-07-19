package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告通知Mapper接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
