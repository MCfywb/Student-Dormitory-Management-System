package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Hygiene;
import com.dormitory.service.HygieneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 卫生检查控制器
 */
@RestController
@RequestMapping("/hygiene")
public class HygieneController {

    @Autowired
    private HygieneService hygieneService;

    @GetMapping("/page")
    public Result<PageResult<Hygiene>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String level) {
        Page<Hygiene> page = hygieneService.getPage(current, size, roomNumber, buildingId, level);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/room/{roomId}")
    public Result<List<Hygiene>> getByRoomId(@PathVariable Long roomId) {
        return Result.success(hygieneService.getByRoomId(roomId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getHygieneStats() {
        return Result.success(hygieneService.getHygieneStats());
    }

    @GetMapping("/recent")
    public Result<List<Map<String, Object>>> getRecentChecks(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(hygieneService.getRecentChecks(limit));
    }

    @GetMapping("/buildingStats")
    public Result<List<Map<String, Object>>> getBuildingHygieneStats() {
        return Result.success(hygieneService.getBuildingHygieneStats());
    }

    @GetMapping("/{id}")
    public Result<Hygiene> getById(@PathVariable Long id) {
        return Result.success(hygieneService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Hygiene hygiene) {
        hygieneService.addCheck(hygiene);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Hygiene hygiene) {
        hygieneService.updateById(hygiene);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hygieneService.removeById(id);
        return Result.success();
    }
}
