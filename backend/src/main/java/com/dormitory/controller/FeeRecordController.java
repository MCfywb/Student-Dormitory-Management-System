package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.FeeRecord;
import com.dormitory.service.FeeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 费用记录控制器
 */
@RestController
@RequestMapping("/fee")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @GetMapping("/page")
    public Result<PageResult<FeeRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) String academicYear) {
        Page<FeeRecord> page = feeRecordService.getPage(current, size, studentNo, payStatus, academicYear);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<FeeRecord>> getByStudentId(@PathVariable Long studentId) {
        return Result.success(feeRecordService.getByStudentId(studentId));
    }

    @GetMapping("/my/{studentId}")
    public Result<List<Map<String, Object>>> getMyFees(@PathVariable Long studentId) {
        return Result.success(feeRecordService.getMyFees(studentId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getFeeStats() {
        return Result.success(feeRecordService.getFeeStats());
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> getFeeTrend() {
        return Result.success(feeRecordService.getFeeTrend());
    }

    @GetMapping("/{id}")
    public Result<FeeRecord> getById(@PathVariable Long id) {
        return Result.success(feeRecordService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody FeeRecord feeRecord) {
        feeRecordService.save(feeRecord);
        return Result.success();
    }

    @PostMapping("/pay")
    public Result<Void> payFee(@RequestParam Long id, @RequestParam String payMethod) {
        try {
            feeRecordService.payFee(id, payMethod);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody FeeRecord feeRecord) {
        feeRecordService.updateById(feeRecord);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        feeRecordService.removeById(id);
        return Result.success();
    }
}
