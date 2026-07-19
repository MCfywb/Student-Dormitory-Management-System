package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dormitory.entity.Notice;
import com.dormitory.mapper.NoticeMapper;
import com.dormitory.service.NoticeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 公告通知服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public Page<Notice> getPage(Integer current, Integer size, String title, String noticeType) {
        Page<Notice> page = new Page<>(current, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Notice::getTitle, title);
        }
        if (noticeType != null && !noticeType.isEmpty()) {
            wrapper.eq(Notice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(Notice::getPublishTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Notice> getPublishedList(int limit) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getPublishTime)
                .last("LIMIT " + limit);
        return this.list(wrapper);
    }

    @Override
    public Notice getDetail(Long id) {
        Notice notice = this.getById(id);
        if (notice != null) {
            LambdaUpdateWrapper<Notice> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Notice::getId, id)
                    .set(Notice::getViewCount, notice.getViewCount() + 1);
            this.update(wrapper);
            notice.setViewCount(notice.getViewCount() + 1);
        }
        return notice;
    }

    @Override
    public void publishNotice(Notice notice) {
        notice.setPublishTime(LocalDateTime.now());
        notice.setViewCount(0);
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        this.save(notice);
    }

    @Override
    public List<Map<String, Object>> getNoticeStats() {
        List<Notice> allNotices = this.list();
        Map<String, Integer> typeCount = new HashMap<>();
        
        for (Notice notice : allNotices) {
            if (notice.getStatus() == 1) {
                typeCount.merge(notice.getNoticeType(), 1, Integer::sum);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> typeNames = new HashMap<>();
        typeNames.put("notice", "通知");
        typeNames.put("announcement", "公告");
        typeNames.put("warning", "警告");
        
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", typeNames.getOrDefault(entry.getKey(), entry.getKey()));
            item.put("value", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
}
