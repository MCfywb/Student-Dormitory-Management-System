package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Room;
import com.dormitory.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 宿舍房间控制器
 */
@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/page")
    public Result<PageResult<Room>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status) {
        Page<Room> page = roomService.getPage(current, size, roomNumber, buildingId, status);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/available")
    public Result<List<Room>> getAvailableRooms(@RequestParam Long buildingId) {
        return Result.success(roomService.getAvailableRooms(buildingId));
    }

    @GetMapping("/building/{buildingId}")
    public Result<List<Room>> getRoomsByBuilding(@PathVariable Long buildingId) {
        return Result.success(roomService.getRoomsByBuilding(buildingId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getRoomStats() {
        return Result.success(roomService.getRoomStats());
    }

    @GetMapping("/floorStats/{buildingId}")
    public Result<List<Map<String, Object>>> getFloorStats(@PathVariable Long buildingId) {
        return Result.success(roomService.getFloorStats(buildingId));
    }

    @GetMapping("/{id}")
    public Result<Room> getById(@PathVariable Long id) {
        return Result.success(roomService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Room room) {
        roomService.save(room);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Room room) {
        roomService.updateById(room);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.removeById(id);
        return Result.success();
    }

    @PostMapping("/assign")
    public Result<Void> assignRoom(@RequestParam Long roomId, @RequestParam Long studentId, @RequestParam Integer bedNumber) {
        try {
            roomService.assignRoom(roomId, studentId, bedNumber);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/leave/{studentId}")
    public Result<Void> leaveRoom(@PathVariable Long studentId) {
        roomService.leaveRoom(studentId);
        return Result.success();
    }
}
