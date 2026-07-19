package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Building;
import com.dormitory.entity.Room;
import com.dormitory.entity.StudentInfo;
import com.dormitory.mapper.BuildingMapper;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentInfoMapper;
import com.dormitory.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 宿舍楼服务实现类
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public Page<Building> getPage(Integer current, Integer size, String buildingName, String buildingType) {
        Page<Building> page = new Page<>(current, size);
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (buildingName != null && !buildingName.isEmpty()) {
            wrapper.like(Building::getBuildingName, buildingName);
        }
        if (buildingType != null && !buildingType.isEmpty()) {
            wrapper.eq(Building::getBuildingType, buildingType);
        }
        wrapper.orderByDesc(Building::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Building> getList() {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Building::getBuildingName);
        return this.list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getBuildingStats() {
        List<Building> buildings = this.getList();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Building building : buildings) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("buildingName", building.getBuildingName());
            stat.put("buildingType", building.getBuildingType());
            
            LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.eq(Room::getBuildingId, building.getId());
            List<Room> rooms = roomMapper.selectList(roomWrapper);
            
            int totalBeds = 0;
            int usedBeds = 0;
            for (Room room : rooms) {
                totalBeds += room.getCapacity();
                usedBeds += room.getCurrentCount();
            }
            
            stat.put("totalBeds", totalBeds);
            stat.put("usedBeds", usedBeds);
            stat.put("availableBeds", totalBeds - usedBeds);
            stat.put("usageRate", totalBeds > 0 ? Math.round((double) usedBeds / totalBeds * 100) : 0);
            result.add(stat);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getBuildingDetail(Long id) {
        Map<String, Object> result = new HashMap<>();
        Building building = this.getById(id);
        result.put("building", building);
        
        LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(Room::getBuildingId, id);
        List<Room> rooms = roomMapper.selectList(roomWrapper);
        result.put("rooms", rooms);
        
        return result;
    }
}
