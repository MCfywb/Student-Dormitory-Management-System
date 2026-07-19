package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Facility;
import com.dormitory.entity.FacilityRepair;
import com.dormitory.entity.FeeRecord;
import com.dormitory.entity.FeeType;
import com.dormitory.entity.StudentInfo;
import com.dormitory.mapper.FacilityMapper;
import com.dormitory.mapper.FacilityRepairMapper;
import com.dormitory.mapper.StudentInfoMapper;
import com.dormitory.service.FacilityRepairService;
import com.dormitory.service.FeeRecordService;
import com.dormitory.service.FeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 设施维修服务实现类
 */
@Service
public class FacilityRepairServiceImpl extends ServiceImpl<FacilityRepairMapper, FacilityRepair> implements FacilityRepairService {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private FeeRecordService feeRecordService;

    @Autowired
    private FeeTypeService feeTypeService;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public Page<FacilityRepair> getPage(Integer current, Integer size, String roomNumber, String status) {
        Page<FacilityRepair> page = new Page<>(current, size);
        LambdaQueryWrapper<FacilityRepair> wrapper = new LambdaQueryWrapper<>();
        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(FacilityRepair::getRoomNumber, roomNumber);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(FacilityRepair::getStatus, status);
        }
        wrapper.orderByDesc(FacilityRepair::getReportTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRepair(FacilityRepair repair) {
        repair.setReportTime(LocalDateTime.now());
        repair.setStatus("pending");
        this.save(repair);
        
        Facility facility = facilityMapper.selectById(repair.getFacilityId());
        if (facility != null) {
            facility.setStatus("damaged");
            facilityMapper.updateById(facility);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRepair(Long id, String status, String handleResult, Long handlerId, String handlerName, BigDecimal repairCost) {
        FacilityRepair repair = this.getById(id);
        if (repair == null) {
            throw new RuntimeException("维修记录不存在");
        }
        
        repair.setStatus(status);
        repair.setHandleResult(handleResult);
        repair.setHandlerId(handlerId);
        repair.setHandlerName(handlerName);
        repair.setHandleTime(LocalDateTime.now());
        if (repairCost != null) {
            repair.setRepairCost(repairCost);
        }
        this.updateById(repair);
        
        if ("completed".equals(status)) {
            Facility facility = facilityMapper.selectById(repair.getFacilityId());
            if (facility != null) {
                facility.setStatus("normal");
                facilityMapper.updateById(facility);
            }
            
            if (repair.getRepairCost() != null && repair.getRepairCost().compareTo(BigDecimal.ZERO) > 0) {
                createRepairFee(repair);
            }
        }
    }

    private void createRepairFee(FacilityRepair repair) {
        FeeType repairFeeType = getOrCreateRepairFeeType();
        
        StudentInfo studentInfo = studentInfoMapper.selectById(repair.getReporterId());
        String studentNo = studentInfo != null ? studentInfo.getStudentNo() : null;
        
        LocalDate dueDate = null;
        if (repair.getReportTime() != null) {
            dueDate = repair.getReportTime().plusDays(7).toLocalDate();
        }
        
        FeeRecord feeRecord = new FeeRecord();
        feeRecord.setStudentId(repair.getReporterId());
        feeRecord.setStudentNo(studentNo);
        feeRecord.setStudentName(repair.getReporterName());
        feeRecord.setFeeTypeId(repairFeeType.getId());
        feeRecord.setFeeTypeName(repairFeeType.getTypeName());
        feeRecord.setAmount(repair.getRepairCost());
        feeRecord.setPayStatus("unpaid");
        feeRecord.setAcademicYear(getCurrentAcademicYear());
        feeRecord.setSemester(getCurrentSemester());
        feeRecord.setDueDate(dueDate);
        feeRecord.setRemark("维修费用 - " + repair.getFacilityName() + " (" + repair.getRoomNumber() + ")");
        
        feeRecordService.save(feeRecord);
    }

    private FeeType getOrCreateRepairFeeType() {
        LambdaQueryWrapper<FeeType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeType::getTypeCode, "repair");
        FeeType feeType = feeTypeService.getOne(wrapper);
        
        if (feeType == null) {
            feeType = new FeeType();
            feeType.setTypeName("维修费");
            feeType.setTypeCode("repair");
            feeType.setPrice(BigDecimal.ZERO);
            feeType.setUnit("次");
            feeType.setDescription("设施维修费用");
            feeType.setStatus(1);
            feeTypeService.save(feeType);
        }
        
        return feeType;
    }

    private String getCurrentAcademicYear() {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 9) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }

    private String getCurrentSemester() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 9 || month <= 1) {
            return "第一学期";
        } else {
            return "第二学期";
        }
    }

    @Override
    public List<Map<String, Object>> getMyRepairs(Long userId) {
        LambdaQueryWrapper<FacilityRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FacilityRepair::getReporterId, userId)
                .orderByDesc(FacilityRepair::getReportTime);
        List<FacilityRepair> repairs = this.list(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (FacilityRepair repair : repairs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", repair.getId());
            item.put("facilityName", repair.getFacilityName());
            item.put("roomNumber", repair.getRoomNumber());
            item.put("buildingName", repair.getBuildingName());
            item.put("problemDesc", repair.getProblemDesc());
            item.put("status", repair.getStatus());
            item.put("reportTime", repair.getReportTime());
            item.put("handleResult", repair.getHandleResult());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getRepairStats() {
        Map<String, Object> result = new HashMap<>();
        List<FacilityRepair> allRepairs = this.list();
        
        int pending = 0;
        int processing = 0;
        int completed = 0;
        int rejected = 0;
        
        for (FacilityRepair repair : allRepairs) {
            switch (repair.getStatus()) {
                case "pending":
                    pending++;
                    break;
                case "processing":
                    processing++;
                    break;
                case "completed":
                    completed++;
                    break;
                case "rejected":
                    rejected++;
                    break;
            }
        }
        
        result.put("total", allRepairs.size());
        result.put("pending", pending);
        result.put("processing", processing);
        result.put("completed", completed);
        result.put("rejected", rejected);
        
        return result;
    }
}
