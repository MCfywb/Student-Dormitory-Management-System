package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Building;
import com.dormitory.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 宿舍楼控制器
 */
@RestController
@RequestMapping("/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/page")
    public Result<PageResult<Building>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String buildingType) {
        Page<Building> page = buildingService.getPage(current, size, buildingName, buildingType);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/list")
    public Result<List<Building>> getList() {
        return Result.success(buildingService.getList());
    }

    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getBuildingStats() {
        return Result.success(buildingService.getBuildingStats());
    }

    @GetMapping("/{id}")
    public Result<Building> getById(@PathVariable Long id) {
        return Result.success(buildingService.getById(id));
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingDetail(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Building building) {
        buildingService.save(building);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Building building) {
        buildingService.updateById(building);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.removeById(id);
        return Result.success();
    }
}
