package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.Hygiene;

import java.util.List;
import java.util.Map;

/**
 * 卫生检查服务接口
 */
public interface HygieneService extends IService<Hygiene> {

    Page<Hygiene> getPage(Integer current, Integer size, String roomNumber, Long buildingId, String level);

    List<Hygiene> getByRoomId(Long roomId);

    void addCheck(Hygiene hygiene);

    Map<String, Object> getHygieneStats();

    List<Map<String, Object>> getRecentChecks(int limit);

    List<Map<String, Object>> getBuildingHygieneStats();
}
