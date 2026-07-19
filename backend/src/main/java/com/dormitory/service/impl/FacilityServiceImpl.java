package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Facility;
import com.dormitory.mapper.FacilityMapper;
import com.dormitory.service.FacilityService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 设施服务实现类
 */
@Service
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, Facility> implements FacilityService {

    @Override
    public Page<Facility> getPage(Integer current, Integer size, String facilityName, Long buildingId, String status) {
        Page<Facility> page = new Page<>(current, size);
        LambdaQueryWrapper<Facility> wrapper = new LambdaQueryWrapper<>();
        if (facilityName != null && !facilityName.isEmpty()) {
            wrapper.like(Facility::getFacilityName, facilityName);
        }
        if (buildingId != null) {
            wrapper.eq(Facility::getBuildingId, buildingId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Facility::getStatus, status);
        }
        wrapper.orderByDesc(Facility::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Facility> getByRoomId(Long roomId) {
        LambdaQueryWrapper<Facility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Facility::getRoomId, roomId);
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getFacilityStats() {
        Map<String, Object> result = new HashMap<>();
        List<Facility> allFacilities = this.list();
        
        int normal = 0;
        int damaged = 0;
        int repairing = 0;
        int scrapped = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        
        for (Facility facility : allFacilities) {
            if (facility.getPrice() != null) {
                totalValue = totalValue.add(facility.getPrice());
            }
            switch (facility.getStatus()) {
                case "normal":
                    normal++;
                    break;
                case "damaged":
                    damaged++;
                    break;
                case "repairing":
                    repairing++;
                    break;
                case "scrapped":
                    scrapped++;
                    break;
            }
        }
        
        result.put("total", allFacilities.size());
        result.put("normal", normal);
        result.put("damaged", damaged);
        result.put("repairing", repairing);
        result.put("scrapped", scrapped);
        result.put("totalValue", totalValue);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getTypeStats() {
        List<Facility> allFacilities = this.list();
        Map<String, Integer> typeCount = new HashMap<>();
        
        for (Facility facility : allFacilities) {
            String type = facility.getFacilityType();
            if (type != null) {
                typeCount.merge(type, 1, Integer::sum);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> typeNames = new HashMap<>();
        typeNames.put("furniture", "家具");
        typeNames.put("appliance", "电器");
        typeNames.put("other", "其他");
        
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", typeNames.getOrDefault(entry.getKey(), entry.getKey()));
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
}
