package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.FacilityRepair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 设施维修服务接口
 */
public interface FacilityRepairService extends IService<FacilityRepair> {

    Page<FacilityRepair> getPage(Integer current, Integer size, String roomNumber, String status);

    void addRepair(FacilityRepair repair);

    void handleRepair(Long id, String status, String handleResult, Long handlerId, String handlerName, BigDecimal repairCost);

    List<Map<String, Object>> getMyRepairs(Long userId);

    Map<String, Object> getRepairStats();
}
