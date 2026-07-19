package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Hygiene;
import com.dormitory.mapper.HygieneMapper;
import com.dormitory.service.HygieneService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 卫生检查服务实现类
 */
@Service
public class HygieneServiceImpl extends ServiceImpl<HygieneMapper, Hygiene> implements HygieneService {

    @Override
    public Page<Hygiene> getPage(Integer current, Integer size, String roomNumber, Long buildingId, String level) {
        Page<Hygiene> page = new Page<>(current, size);
        LambdaQueryWrapper<Hygiene> wrapper = new LambdaQueryWrapper<>();
        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(Hygiene::getRoomNumber, roomNumber);
        }
        if (buildingId != null) {
            wrapper.eq(Hygiene::getBuildingId, buildingId);
        }
        if (level != null && !level.isEmpty()) {
            wrapper.eq(Hygiene::getLevel, level);
        }
        wrapper.orderByDesc(Hygiene::getCheckDate);
        return this.page(page, wrapper);
    }

    @Override
    public List<Hygiene> getByRoomId(Long roomId) {
        LambdaQueryWrapper<Hygiene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hygiene::getRoomId, roomId)
                .orderByDesc(Hygiene::getCheckDate);
        return this.list(wrapper);
    }

    @Override
    public void addCheck(Hygiene hygiene) {
        if (hygiene.getScore() >= 90) {
            hygiene.setLevel("excellent");
        } else if (hygiene.getScore() >= 80) {
            hygiene.setLevel("good");
        } else if (hygiene.getScore() >= 60) {
            hygiene.setLevel("qualified");
        } else {
            hygiene.setLevel("unqualified");
        }
        this.save(hygiene);
    }

    @Override
    public Map<String, Object> getHygieneStats() {
        Map<String, Object> result = new HashMap<>();
        List<Hygiene> allChecks = this.list();
        
        int excellent = 0;
        int good = 0;
        int qualified = 0;
        int unqualified = 0;
        double totalScore = 0;
        
        for (Hygiene hygiene : allChecks) {
            totalScore += hygiene.getScore();
            switch (hygiene.getLevel()) {
                case "excellent":
                    excellent++;
                    break;
                case "good":
                    good++;
                    break;
                case "qualified":
                    qualified++;
                    break;
                case "unqualified":
                    unqualified++;
                    break;
            }
        }
        
        result.put("total", allChecks.size());
        result.put("excellent", excellent);
        result.put("good", good);
        result.put("qualified", qualified);
        result.put("unqualified", unqualified);
        result.put("averageScore", allChecks.isEmpty() ? 0 : Math.round(totalScore / allChecks.size() * 10) / 10.0);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentChecks(int limit) {
        LambdaQueryWrapper<Hygiene> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Hygiene::getCheckDate)
                .last("LIMIT " + limit);
        List<Hygiene> hygienes = this.list(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Hygiene hygiene : hygienes) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", hygiene.getId());
            item.put("roomNumber", hygiene.getRoomNumber());
            item.put("buildingName", hygiene.getBuildingName());
            item.put("score", hygiene.getScore());
            item.put("level", hygiene.getLevel());
            item.put("checkDate", hygiene.getCheckDate());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getBuildingHygieneStats() {
        List<Hygiene> allChecks = this.list();
        Map<String, List<Hygiene>> buildingMap = new HashMap<>();
        
        for (Hygiene hygiene : allChecks) {
            buildingMap.computeIfAbsent(hygiene.getBuildingName(), k -> new ArrayList<>()).add(hygiene);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Hygiene>> entry : buildingMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("buildingName", entry.getKey());
            
            double totalScore = 0;
            for (Hygiene h : entry.getValue()) {
                totalScore += h.getScore();
            }
            item.put("averageScore", entry.getValue().isEmpty() ? 0 : Math.round(totalScore / entry.getValue().size() * 10) / 10.0);
            item.put("checkCount", entry.getValue().size());
            result.add(item);
        }
        
        return result;
    }
}
