package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.StudentInfo;
import com.dormitory.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生信息控制器
 */
@RestController
@RequestMapping("/student")
public class StudentInfoController {

    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping("/page")
    public Result<PageResult<StudentInfo>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long buildingId) {
        Page<StudentInfo> page = studentInfoService.getPage(current, size, studentNo, realName, buildingId);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/user/{userId}")
    public Result<StudentInfo> getByUserId(@PathVariable Long userId) {
        return Result.success(studentInfoService.getByUserId(userId));
    }

    @GetMapping("/{id}")
    public Result<StudentInfo> getById(@PathVariable Long id) {
        return Result.success(studentInfoService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody StudentInfo studentInfo) {
        try {
            studentInfoService.addStudent(studentInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        return Result.success(studentInfoService.getStudentDetail(id));
    }

    @GetMapping("/collegeStats")
    public Result<List<Map<String, Object>>> getCollegeStats() {
        return Result.success(studentInfoService.getCollegeStats());
    }

    @GetMapping("/roommates/{roomId}")
    public Result<List<StudentInfo>> getRoommates(@PathVariable Long roomId) {
        return Result.success(studentInfoService.getRoommatesByRoomId(roomId));
    }

    @GetMapping("/gradeStats")
    public Result<List<Map<String, Object>>> getGradeStats() {
        return Result.success(studentInfoService.getGradeStats());
    }

    @PutMapping
    public Result<Void> update(@RequestBody StudentInfo studentInfo) {
        try {
            studentInfoService.updateStudentInfo(studentInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            studentInfoService.deleteStudent(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
