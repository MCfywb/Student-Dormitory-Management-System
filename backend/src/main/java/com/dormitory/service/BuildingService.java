package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.Building;

import java.util.List;
import java.util.Map;

/**
 * 宿舍楼服务接口
 */
public interface BuildingService extends IService<Building> {

    Page<Building> getPage(Integer current, Integer size, String buildingName, String buildingType);

    List<Building> getList();

    List<Map<String, Object>> getBuildingStats();

    Map<String, Object> getBuildingDetail(Long id);
}
