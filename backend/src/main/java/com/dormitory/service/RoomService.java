package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.Room;

import java.util.List;
import java.util.Map;

/**
 * 宿舍房间服务接口
 */
public interface RoomService extends IService<Room> {

    Page<Room> getPage(Integer current, Integer size, String roomNumber, Long buildingId, String status);

    List<Room> getAvailableRooms(Long buildingId);

    List<Room> getRoomsByBuilding(Long buildingId);

    void assignRoom(Long roomId, Long studentId, Integer bedNumber);

    void leaveRoom(Long studentId);

    Map<String, Object> getRoomStats();

    List<Map<String, Object>> getFloorStats(Long buildingId);
}
