package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Facility;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设施Mapper接口
 */
@Mapper
public interface FacilityMapper extends BaseMapper<Facility> {
}
