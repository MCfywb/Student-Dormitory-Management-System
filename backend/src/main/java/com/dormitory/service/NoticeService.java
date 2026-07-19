package com.dormitory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dormitory.entity.Notice;

import java.util.List;
import java.util.Map;

/**
 * 公告通知服务接口
 */
public interface NoticeService extends IService<Notice> {

    Page<Notice> getPage(Integer current, Integer size, String title, String noticeType);

    List<Notice> getPublishedList(int limit);

    Notice getDetail(Long id);

    void publishNotice(Notice notice);

    List<Map<String, Object>> getNoticeStats();
}
