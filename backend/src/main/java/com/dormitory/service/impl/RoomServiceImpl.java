package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Room;
import com.dormitory.entity.StudentInfo;
import com.dormitory.mapper.RoomMapper;
import com.dormitory.mapper.StudentInfoMapper;
import com.dormitory.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 宿舍房间服务实现类
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public Page<Room> getPage(Integer current, Integer size, String roomNumber, Long buildingId, String status) {
        Page<Room> page = new Page<>(current, size);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(Room::getRoomNumber, roomNumber);
        }
        if (buildingId != null) {
            wrapper.eq(Room::getBuildingId, buildingId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Room::getStatus, status);
        }
        wrapper.orderByAsc(Room::getBuildingId).orderByAsc(Room::getFloor).orderByAsc(Room::getRoomNumber);
        Page<Room> result = this.page(page, wrapper);
        
        for (Room room : result.getRecords()) {
            LambdaQueryWrapper<StudentInfo> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(StudentInfo::getRoomId, room.getId())
                    .eq(StudentInfo::getStatus, "checked_in");
            Long actualCount = studentInfoMapper.selectCount(studentWrapper);
            room.setCurrentCount(actualCount.intValue());
        }
        
        return result;
    }

    @Override
    public List<Room> getAvailableRooms(Long buildingId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, buildingId)
                .ne(Room::getStatus, "full")
                .ne(Room::getStatus, "maintenance");
        return this.list(wrapper);
    }

    @Override
    public List<Room> getRoomsByBuilding(Long buildingId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, buildingId)
                .orderByAsc(Room::getFloor)
                .orderByAsc(Room::getRoomNumber);
        List<Room> rooms = this.list(wrapper);
        
        for (Room room : rooms) {
            LambdaQueryWrapper<StudentInfo> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(StudentInfo::getRoomId, room.getId())
                    .eq(StudentInfo::getStatus, "checked_in");
            Long actualCount = studentInfoMapper.selectCount(studentWrapper);
            room.setCurrentCount(actualCount.intValue());
            
            LambdaQueryWrapper<StudentInfo> bedWrapper = new LambdaQueryWrapper<>();
            bedWrapper.eq(StudentInfo::getRoomId, room.getId())
                    .eq(StudentInfo::getStatus, "checked_in")
                    .isNotNull(StudentInfo::getBedNumber);
            List<StudentInfo> students = studentInfoMapper.selectList(bedWrapper);
            List<Integer> occupiedBeds = new ArrayList<>();
            for (StudentInfo student : students) {
                if (student.getBedNumber() != null) {
                    occupiedBeds.add(student.getBedNumber());
                }
            }
            room.setOccupiedBeds(occupiedBeds);
            room.setAvailableBeds(room.getCapacity() - occupiedBeds.size());
        }
        
        return rooms;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoom(Long roomId, Long studentId, Integer bedNumber) {
        Room room = this.getById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        LambdaQueryWrapper<StudentInfo> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(StudentInfo::getRoomId, roomId)
                .eq(StudentInfo::getStatus, "checked_in");
        Long currentCount = studentInfoMapper.selectCount(countWrapper);
        if (currentCount >= room.getCapacity()) {
            throw new RuntimeException("房间已满");
        }

        StudentInfo studentInfo = studentInfoMapper.selectById(studentId);
        if (studentInfo == null) {
            throw new RuntimeException("学生不存在");
        }

        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getRoomId, roomId)
                .eq(StudentInfo::getBedNumber, bedNumber)
                .eq(StudentInfo::getStatus, "checked_in");
        Long count = studentInfoMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该床位已被占用");
        }

        studentInfo.setRoomId(roomId);
        studentInfo.setRoomNumber(room.getRoomNumber());
        studentInfo.setBuildingId(room.getBuildingId());
        studentInfo.setBuildingName(room.getBuildingName());
        studentInfo.setBedNumber(bedNumber);
        studentInfo.setCheckInTime(LocalDateTime.now());
        studentInfo.setStatus("checked_in");
        studentInfoMapper.updateById(studentInfo);

        room.setCurrentCount(currentCount.intValue() + 1);
        if (room.getCurrentCount() >= room.getCapacity()) {
            room.setStatus("full");
        } else {
            room.setStatus("available");
        }
        this.updateById(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveRoom(Long studentId) {
        StudentInfo studentInfo = studentInfoMapper.selectById(studentId);
        if (studentInfo == null) {
            return;
        }

        Long roomId = studentInfo.getRoomId();
        if (roomId == null) {
            studentInfo.setStatus("checked_out");
            studentInfoMapper.updateById(studentInfo);
            return;
        }

        Room room = this.getById(roomId);
        if (room != null) {
            LambdaQueryWrapper<StudentInfo> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(StudentInfo::getRoomId, roomId)
                    .eq(StudentInfo::getStatus, "checked_in");
            Long actualCount = studentInfoMapper.selectCount(countWrapper);
            room.setCurrentCount(Math.max(0, actualCount.intValue() - 1));
            if ("full".equals(room.getStatus())) {
                room.setStatus("available");
            }
            this.updateById(room);
        }

        studentInfo.setRoomId(null);
        studentInfo.setRoomNumber(null);
        studentInfo.setBuildingId(null);
        studentInfo.setBuildingName(null);
        studentInfo.setBedNumber(null);
        studentInfo.setStatus("checked_out");
        studentInfoMapper.updateById(studentInfo);
    }

    @Override
    public Map<String, Object> getRoomStats() {
        Map<String, Object> result = new HashMap<>();
        
        List<Room> allRooms = this.list();
        int totalRooms = allRooms.size();
        int fullRooms = 0;
        int availableRooms = 0;
        int maintenanceRooms = 0;
        int totalBeds = 0;
        int usedBeds = 0;
        
        for (Room room : allRooms) {
            totalBeds += room.getCapacity();
            
            LambdaQueryWrapper<StudentInfo> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(StudentInfo::getRoomId, room.getId())
                    .eq(StudentInfo::getStatus, "checked_in");
            Long actualCount = studentInfoMapper.selectCount(studentWrapper);
            usedBeds += actualCount.intValue();
            
            if ("full".equals(room.getStatus())) {
                fullRooms++;
            } else if ("maintenance".equals(room.getStatus())) {
                maintenanceRooms++;
            } else {
                availableRooms++;
            }
        }
        
        result.put("totalRooms", totalRooms);
        result.put("fullRooms", fullRooms);
        result.put("availableRooms", availableRooms);
        result.put("maintenanceRooms", maintenanceRooms);
        result.put("totalBeds", totalBeds);
        result.put("usedBeds", usedBeds);
        result.put("availableBeds", totalBeds - usedBeds);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getFloorStats(Long buildingId) {
        List<Map<String, Object>> result = new ArrayList<>();
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getBuildingId, buildingId)
                .orderByAsc(Room::getFloor);
        List<Room> rooms = this.list(wrapper);
        
        Map<Integer, List<Room>> floorMap = new TreeMap<>();
        for (Room room : rooms) {
            floorMap.computeIfAbsent(room.getFloor(), k -> new ArrayList<>()).add(room);
        }
        
        for (Map.Entry<Integer, List<Room>> entry : floorMap.entrySet()) {
            Map<String, Object> floorStat = new HashMap<>();
            floorStat.put("floor", entry.getKey() + "层");
            floorStat.put("roomCount", entry.getValue().size());
            int total = 0;
            int used = 0;
            for (Room room : entry.getValue()) {
                total += room.getCapacity();
                
                LambdaQueryWrapper<StudentInfo> studentWrapper = new LambdaQueryWrapper<>();
                studentWrapper.eq(StudentInfo::getRoomId, room.getId())
                        .eq(StudentInfo::getStatus, "checked_in");
                Long actualCount = studentInfoMapper.selectCount(studentWrapper);
                used += actualCount.intValue();
            }
            floorStat.put("totalBeds", total);
            floorStat.put("usedBeds", used);
            result.add(floorStat);
        }
        
        return result;
    }
}
