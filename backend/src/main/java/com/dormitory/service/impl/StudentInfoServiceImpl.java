package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.StudentInfo;
import com.dormitory.entity.User;
import com.dormitory.mapper.StudentInfoMapper;
import com.dormitory.mapper.UserMapper;
import com.dormitory.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 学生信息服务实现类
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<StudentInfo> getPage(Integer current, Integer size, String studentNo, String realName, Long buildingId) {
        Page<StudentInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        if (studentNo != null && !studentNo.isEmpty()) {
            wrapper.like(StudentInfo::getStudentNo, studentNo);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(StudentInfo::getRealName, realName);
        }
        if (buildingId != null) {
            wrapper.eq(StudentInfo::getBuildingId, buildingId);
        }
        wrapper.orderByDesc(StudentInfo::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public StudentInfo getByUserId(Long userId) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getUserId, userId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(StudentInfo studentInfo) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNo, studentInfo.getStudentNo());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("学号已存在");
        }
        
        User user = new User();
        user.setUsername(studentInfo.getStudentNo());
        user.setPassword("123456");
        user.setRealName(studentInfo.getRealName());
        user.setGender(studentInfo.getGender());
        user.setPhone(studentInfo.getPhone());
        user.setEmail(studentInfo.getEmail());
        user.setRole("student");
        user.setStatus(1);
        user.setAvatar("/upload/default-avatar.png");
        userMapper.insert(user);
        
        studentInfo.setUserId(user.getId());
        studentInfo.setStatus("pending");
        this.save(studentInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudentInfo(StudentInfo studentInfo) {
        StudentInfo existInfo = this.getById(studentInfo.getId());
        if (existInfo == null) {
            throw new RuntimeException("学生信息不存在");
        }
        
        this.updateById(studentInfo);
        
        LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<>();
        userWrapper.eq(User::getId, existInfo.getUserId())
                .set(User::getRealName, studentInfo.getRealName())
                .set(User::getGender, studentInfo.getGender())
                .set(User::getPhone, studentInfo.getPhone())
                .set(User::getEmail, studentInfo.getEmail());
        userMapper.update(null, userWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        StudentInfo studentInfo = this.getById(id);
        if (studentInfo == null) {
            throw new RuntimeException("学生信息不存在");
        }
        if (studentInfo.getUserId() != null) {
            userMapper.deleteById(studentInfo.getUserId());
        }
        this.removeById(id);
    }

    @Override
    public List<Map<String, Object>> getCollegeStats() {
        List<StudentInfo> students = this.list();
        Map<String, Integer> collegeCount = new HashMap<>();
        
        for (StudentInfo student : students) {
            String college = student.getCollege();
            if (college != null && !college.isEmpty()) {
                collegeCount.merge(college, 1, Integer::sum);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : collegeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        result.sort((a, b) -> (Integer) b.get("value") - (Integer) a.get("value"));
        return result;
    }

    @Override
    public List<Map<String, Object>> getGradeStats() {
        List<StudentInfo> students = this.list();
        Map<String, Integer> gradeCount = new LinkedHashMap<>();
        
        for (StudentInfo student : students) {
            String grade = student.getGrade();
            if (grade != null && !grade.isEmpty()) {
                gradeCount.merge(grade, 1, Integer::sum);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gradeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getStudentDetail(Long id) {
        Map<String, Object> result = new HashMap<>();
        StudentInfo studentInfo = this.getById(id);
        result.put("studentInfo", studentInfo);
        
        if (studentInfo != null && studentInfo.getUserId() != null) {
            User user = userMapper.selectById(studentInfo.getUserId());
            result.put("user", user);
        }
        
        return result;
    }

    @Override
    public List<StudentInfo> getRoommatesByRoomId(Long roomId) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getRoomId, roomId)
                .eq(StudentInfo::getStatus, "checked_in")
                .orderByAsc(StudentInfo::getBedNumber);
        return this.list(wrapper);
    }
}
