package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.FeeType;
import com.dormitory.mapper.FeeTypeMapper;
import com.dormitory.service.FeeTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 费用类型服务实现类
 */
@Service
public class FeeTypeServiceImpl extends ServiceImpl<FeeTypeMapper, FeeType> implements FeeTypeService {

    @Override
    public List<FeeType> getActiveList() {
        LambdaQueryWrapper<FeeType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeType::getStatus, 1)
                .orderByAsc(FeeType::getId);
        return this.list(wrapper);
    }
}
