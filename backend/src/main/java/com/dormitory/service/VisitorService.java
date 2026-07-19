package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.Visitor;

import java.util.List;
import java.util.Map;

/**
 * 来访登记服务接口
 */
public interface VisitorService extends IService<Visitor> {

    Page<Visitor> getPage(Integer current, Integer size, String visitorName, String buildingName, String status);

    void registerVisit(Visitor visitor);

    void leave(Long id);

    Map<String, Object> getVisitorStats();
}
