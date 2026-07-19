package com.dormitory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.FeeType;

import java.util.List;

/**
 * 费用类型服务接口
 */
public interface FeeTypeService extends IService<FeeType> {

    List<FeeType> getActiveList();
}
