package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.dormitory.entity.Facility;

import java.util.List;
import java.util.Map;

/**
 * 设施服务接口
 */
public interface FacilityService extends IService<Facility> {

    Page<Facility> getPage(Integer current, Integer size, String facilityName, String status, String facilityType, Long roomId);

    List<String> getFacilityTypes();

    List<Facility> getByRoomId(Long roomId);

    Map<String, Object> getFacilityStats();

    List<Map<String, Object>> getTypeStats();
}
