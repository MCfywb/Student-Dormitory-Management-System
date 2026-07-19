package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Facility;
import com.dormitory.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设施控制器
 */
@RestController
@RequestMapping("/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/page")
    public Result<PageResult<Facility>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String facilityName,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status) {
        Page<Facility> page = facilityService.getPage(current, size, facilityName, buildingId, status);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/room/{roomId}")
    public Result<List<Facility>> getByRoomId(@PathVariable Long roomId) {
        return Result.success(facilityService.getByRoomId(roomId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getFacilityStats() {
        return Result.success(facilityService.getFacilityStats());
    }

    @GetMapping("/typeStats")
    public Result<List<Map<String, Object>>> getTypeStats() {
        return Result.success(facilityService.getTypeStats());
    }

    @GetMapping("/{id}")
    public Result<Facility> getById(@PathVariable Long id) {
        return Result.success(facilityService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Facility facility) {
        facilityService.save(facility);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Facility facility) {
        facilityService.updateById(facility);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        facilityService.removeById(id);
        return Result.success();
    }
}
