package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.FacilityRepair;
import com.dormitory.service.FacilityRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设施维修控制器
 */
@RestController
@RequestMapping("/repair")
public class FacilityRepairController {

    @Autowired
    private FacilityRepairService repairService;

    @GetMapping("/page")
    public Result<PageResult<FacilityRepair>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String status) {
        Page<FacilityRepair> page = repairService.getPage(current, size, roomNumber, status);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/my/{userId}")
    public Result<List<Map<String, Object>>> getMyRepairs(@PathVariable Long userId) {
        return Result.success(repairService.getMyRepairs(userId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getRepairStats() {
        return Result.success(repairService.getRepairStats());
    }

    @GetMapping("/{id}")
    public Result<FacilityRepair> getById(@PathVariable Long id) {
        return Result.success(repairService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody FacilityRepair repair) {
        repairService.addRepair(repair);
        return Result.success();
    }

    @PutMapping("/handle")
    public Result<Void> handleRepair(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam String handleResult,
            @RequestParam Long handlerId,
            @RequestParam String handlerName,
            @RequestParam(required = false) java.math.BigDecimal repairCost) {
        try {
            repairService.handleRepair(id, status, handleResult, handlerId, handlerName, repairCost);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        repairService.removeById(id);
        return Result.success();
    }
}
