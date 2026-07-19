package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.FeeRecord;
import com.dormitory.mapper.FeeRecordMapper;
import com.dormitory.service.FeeRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 费用记录服务实现类
 */
@Service
public class FeeRecordServiceImpl extends ServiceImpl<FeeRecordMapper, FeeRecord> implements FeeRecordService {

    @Override
    public Page<FeeRecord> getPage(Integer current, Integer size, String studentNo, String payStatus, String academicYear) {
        Page<FeeRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        if (studentNo != null && !studentNo.isEmpty()) {
            wrapper.like(FeeRecord::getStudentNo, studentNo);
        }
        if (payStatus != null && !payStatus.isEmpty()) {
            wrapper.eq(FeeRecord::getPayStatus, payStatus);
        }
        if (academicYear != null && !academicYear.isEmpty()) {
            wrapper.eq(FeeRecord::getAcademicYear, academicYear);
        }
        wrapper.orderByDesc(FeeRecord::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<FeeRecord> getByStudentId(Long studentId) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getStudentId, studentId)
                .orderByDesc(FeeRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public void payFee(Long id, String payMethod) {
        FeeRecord feeRecord = this.getById(id);
        if (feeRecord == null) {
            throw new RuntimeException("费用记录不存在");
        }
        if ("paid".equals(feeRecord.getPayStatus())) {
            throw new RuntimeException("该费用已支付");
        }
        
        feeRecord.setPayStatus("paid");
        feeRecord.setPayMethod(payMethod);
        feeRecord.setPayTime(LocalDateTime.now());
        this.updateById(feeRecord);
    }

    @Override
    public List<Map<String, Object>> getMyFees(Long studentId) {
        List<FeeRecord> fees = getByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (FeeRecord fee : fees) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", fee.getId());
            item.put("feeTypeName", fee.getFeeTypeName());
            item.put("amount", fee.getAmount());
            item.put("payStatus", fee.getPayStatus());
            item.put("academicYear", fee.getAcademicYear());
            item.put("semester", fee.getSemester());
            item.put("dueDate", fee.getDueDate());
            item.put("payTime", fee.getPayTime());
            item.put("payMethod", fee.getPayMethod());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getFeeStats() {
        Map<String, Object> result = new HashMap<>();
        List<FeeRecord> allFees = this.list();
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal unpaidAmount = BigDecimal.ZERO;
        int paidCount = 0;
        int unpaidCount = 0;
        int overdueCount = 0;
        
        for (FeeRecord fee : allFees) {
            totalAmount = totalAmount.add(fee.getAmount());
            switch (fee.getPayStatus()) {
                case "paid":
                    paidAmount = paidAmount.add(fee.getAmount());
                    paidCount++;
                    break;
                case "unpaid":
                    unpaidAmount = unpaidAmount.add(fee.getAmount());
                    unpaidCount++;
                    break;
                case "overdue":
                    unpaidAmount = unpaidAmount.add(fee.getAmount());
                    overdueCount++;
                    break;
            }
        }
        
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        result.put("unpaidAmount", unpaidAmount);
        result.put("paidCount", paidCount);
        result.put("unpaidCount", unpaidCount);
        result.put("overdueCount", overdueCount);
        
        return result;
    }

    @Override
    public BigDecimal getStudentTotalFee(Long studentId, String academicYear) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getStudentId, studentId);
        if (academicYear != null && !academicYear.isEmpty()) {
            wrapper.eq(FeeRecord::getAcademicYear, academicYear);
        }
        
        List<FeeRecord> fees = this.list(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (FeeRecord fee : fees) {
            total = total.add(fee.getAmount());
        }
        return total;
    }

    @Override
    public Map<String, Object> getFeeTrend() {
        Map<String, Object> result = new HashMap<>();
        List<FeeRecord> allFees = this.list();
        
        Map<String, BigDecimal> monthAmount = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (FeeRecord fee : allFees) {
            if (fee.getPayTime() != null) {
                String month = fee.getPayTime().format(formatter);
                monthAmount.merge(month, fee.getAmount(), BigDecimal::add);
            }
        }
        
        List<String> months = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        int count = 0;
        for (Map.Entry<String, BigDecimal> entry : monthAmount.entrySet()) {
            months.add(entry.getKey());
            amounts.add(entry.getValue());
            count++;
            if (count >= 12) break;
        }
        
        result.put("months", months);
        result.put("amounts", amounts);
        
        return result;
    }
}
