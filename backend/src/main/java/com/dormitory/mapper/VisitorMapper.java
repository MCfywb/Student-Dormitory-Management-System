package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 来访登记Mapper接口
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {
}
