package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Building;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宿舍楼Mapper接口
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {
}
