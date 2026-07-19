package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Hygiene;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卫生检查Mapper接口
 */
@Mapper
public interface HygieneMapper extends BaseMapper<Hygiene> {
}
