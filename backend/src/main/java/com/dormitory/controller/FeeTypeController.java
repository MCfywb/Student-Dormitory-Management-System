package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.entity.FeeType;
import com.dormitory.service.FeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用类型控制器
 */
@RestController
@RequestMapping("/feeType")
public class FeeTypeController {

    @Autowired
    private FeeTypeService feeTypeService;

    @GetMapping("/list")
    public Result<List<FeeType>> getActiveList() {
        return Result.success(feeTypeService.getActiveList());
    }

    @GetMapping("/{id}")
    public Result<FeeType> getById(@PathVariable Long id) {
        return Result.success(feeTypeService.getById(id));
    }

    @GetMapping("/page")
    public Result<List<FeeType>> getPage() {
        return Result.success(feeTypeService.list());
    }

    @PostMapping
    public Result<Void> save(@RequestBody FeeType feeType) {
        feeTypeService.save(feeType);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody FeeType feeType) {
        feeTypeService.updateById(feeType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        feeTypeService.removeById(id);
        return Result.success();
    }
}
