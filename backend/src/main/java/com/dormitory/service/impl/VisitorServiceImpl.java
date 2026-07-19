package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Visitor;
import com.dormitory.mapper.VisitorMapper;
import com.dormitory.service.VisitorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 来访登记服务实现类
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Override
    public Page<Visitor> getPage(Integer current, Integer size, String visitorName, String buildingName, String status) {
        Page<Visitor> page = new Page<>(current, size);
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        if (visitorName != null && !visitorName.isEmpty()) {
            wrapper.like(Visitor::getVisitorName, visitorName);
        }
        if (buildingName != null && !buildingName.isEmpty()) {
            wrapper.eq(Visitor::getBuildingName, buildingName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Visitor::getStatus, status);
        }
        wrapper.orderByDesc(Visitor::getVisitTime);
        return this.page(page, wrapper);
    }

    @Override
    public void registerVisit(Visitor visitor) {
        visitor.setVisitTime(LocalDateTime.now());
        visitor.setStatus("visiting");
        this.save(visitor);
    }

    @Override
    public void leave(Long id) {
        Visitor visitor = this.getById(id);
        if (visitor == null) {
            throw new RuntimeException("来访记录不存在");
        }
        visitor.setLeaveTime(LocalDateTime.now());
        visitor.setStatus("left");
        this.updateById(visitor);
    }

    @Override
    public Map<String, Object> getVisitorStats() {
        Map<String, Object> result = new HashMap<>();
        List<Visitor> allVisitors = this.list();
        
        int visiting = 0;
        int left = 0;
        Map<String, Integer> buildingCount = new HashMap<>();
        Map<String, Integer> dayCount = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Visitor visitor : allVisitors) {
            if ("visiting".equals(visitor.getStatus())) {
                visiting++;
            } else {
                left++;
            }
            
            if (visitor.getBuildingName() != null) {
                buildingCount.merge(visitor.getBuildingName(), 1, Integer::sum);
            }
            
            if (visitor.getVisitTime() != null) {
                String day = visitor.getVisitTime().format(formatter);
                dayCount.merge(day, 1, Integer::sum);
            }
        }
        
        result.put("total", allVisitors.size());
        result.put("visiting", visiting);
        result.put("left", left);
        
        List<Map<String, Object>> buildingStats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : buildingCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            buildingStats.add(item);
        }
        result.put("buildingStats", buildingStats);
        
        List<String> days = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : dayCount.entrySet()) {
            days.add(entry.getKey());
            counts.add(entry.getValue());
            count++;
            if (count >= 7) break;
        }
        result.put("days", days);
        result.put("counts", counts);
        
        return result;
    }
}
