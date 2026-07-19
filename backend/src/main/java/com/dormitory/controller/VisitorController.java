package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Visitor;
import com.dormitory.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 来访登记控制器
 */
@RestController
@RequestMapping("/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    @GetMapping("/page")
    public Result<PageResult<Visitor>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String status) {
        Page<Visitor> page = visitorService.getPage(current, size, visitorName, buildingName, status);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getVisitorStats() {
        return Result.success(visitorService.getVisitorStats());
    }

    @GetMapping("/{id}")
    public Result<Visitor> getById(@PathVariable Long id) {
        return Result.success(visitorService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Visitor visitor) {
        visitorService.registerVisit(visitor);
        return Result.success();
    }

    @PostMapping("/leave/{id}")
    public Result<Void> leave(@PathVariable Long id) {
        try {
            visitorService.leave(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        visitorService.removeById(id);
        return Result.success();
    }
}
