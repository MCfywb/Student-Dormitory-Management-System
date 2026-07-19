package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 首页统计数据控制器
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private HygieneService hygieneService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityRepairService repairService;

    @Autowired
    private FeeRecordService feeRecordService;

    @Autowired
    private VisitorService visitorService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> roomStats = roomService.getRoomStats();
        result.put("totalRooms", roomStats.get("totalRooms"));
        result.put("totalBeds", roomStats.get("totalBeds"));
        result.put("usedBeds", roomStats.get("usedBeds"));
        result.put("availableBeds", roomStats.get("availableBeds"));
        
        result.put("totalStudents", studentInfoService.count());
        
        Map<String, Object> feeStats = feeRecordService.getFeeStats();
        result.put("totalFeeAmount", feeStats.get("totalAmount"));
        result.put("paidFeeAmount", feeStats.get("paidAmount"));
        result.put("unpaidFeeCount", feeStats.get("unpaidCount"));
        
        Map<String, Object> repairStats = repairService.getRepairStats();
        result.put("pendingRepairs", repairStats.get("pending"));
        
        Map<String, Object> visitorStats = visitorService.getVisitorStats();
        result.put("todayVisitors", visitorStats.get("visiting"));
        
        return Result.success(result);
    }

    @GetMapping("/buildingStats")
    public Result<List<Map<String, Object>>> getBuildingStats() {
        return Result.success(buildingService.getBuildingStats());
    }

    @GetMapping("/roomStats")
    public Result<Map<String, Object>> getRoomStats() {
        return Result.success(roomService.getRoomStats());
    }

    @GetMapping("/studentCollegeStats")
    public Result<List<Map<String, Object>>> getStudentCollegeStats() {
        return Result.success(studentInfoService.getCollegeStats());
    }

    @GetMapping("/studentGradeStats")
    public Result<List<Map<String, Object>>> getStudentGradeStats() {
        return Result.success(studentInfoService.getGradeStats());
    }

    @GetMapping("/hygieneStats")
    public Result<Map<String, Object>> getHygieneStats() {
        return Result.success(hygieneService.getHygieneStats());
    }

    @GetMapping("/facilityStats")
    public Result<Map<String, Object>> getFacilityStats() {
        return Result.success(facilityService.getFacilityStats());
    }

    @GetMapping("/facilityTypeStats")
    public Result<List<Map<String, Object>>> getFacilityTypeStats() {
        return Result.success(facilityService.getTypeStats());
    }

    @GetMapping("/repairStats")
    public Result<Map<String, Object>> getRepairStats() {
        return Result.success(repairService.getRepairStats());
    }

    @GetMapping("/feeStats")
    public Result<Map<String, Object>> getFeeStats() {
        return Result.success(feeRecordService.getFeeStats());
    }

    @GetMapping("/feeTrend")
    public Result<Map<String, Object>> getFeeTrend() {
        return Result.success(feeRecordService.getFeeTrend());
    }

    @GetMapping("/visitorStats")
    public Result<Map<String, Object>> getVisitorStats() {
        return Result.success(visitorService.getVisitorStats());
    }

    @GetMapping("/recentHygiene")
    public Result<List<Map<String, Object>>> getRecentHygiene() {
        return Result.success(hygieneService.getRecentChecks(5));
    }

    @GetMapping("/buildingHygieneStats")
    public Result<List<Map<String, Object>>> getBuildingHygieneStats() {
        return Result.success(hygieneService.getBuildingHygieneStats());
    }
}
