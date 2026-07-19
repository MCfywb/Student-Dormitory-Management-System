package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.FeeRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 费用记录服务接口
 */
public interface FeeRecordService extends IService<FeeRecord> {

    Page<FeeRecord> getPage(Integer current, Integer size, String studentNo, String payStatus, String academicYear);

    List<FeeRecord> getByStudentId(Long studentId);

    void payFee(Long id, String payMethod);

    List<Map<String, Object>> getMyFees(Long studentId);

    Map<String, Object> getFeeStats();

    BigDecimal getStudentTotalFee(Long studentId, String academicYear);

    Map<String, Object> getFeeTrend();
}
