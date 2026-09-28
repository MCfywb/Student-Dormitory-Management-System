package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.StudentInfo;

import java.util.List;
import java.util.Map;

/**
 * 学生信息服务接口
 */
public interface StudentInfoService extends IService<StudentInfo> {

    Page<StudentInfo> getPage(Integer current, Integer size, String studentNo, String realName, String college, Long buildingId, String gender, String grade, String status);

    StudentInfo getByUserId(Long userId);

    void addStudent(StudentInfo studentInfo);

    void updateStudentInfo(StudentInfo studentInfo);

    void deleteStudent(Long id);

    List<Map<String, Object>> getCollegeStats();

    List<Map<String, Object>> getGradeStats();

    Map<String, Object> getStudentDetail(Long id);

    List<StudentInfo> getRoommatesByRoomId(Long roomId);
}
