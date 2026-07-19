package com.dormitory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dormitory.common.PageResult;
import com.dormitory.common.Result;
import com.dormitory.entity.Notice;
import com.dormitory.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公告通知控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/page")
    public Result<PageResult<Notice>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String noticeType) {
        Page<Notice> page = noticeService.getPage(current, size, title, noticeType);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/list")
    public Result<List<Notice>> getPublishedList(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(noticeService.getPublishedList(limit));
    }

    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getNoticeStats() {
        return Result.success(noticeService.getNoticeStats());
    }

    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        return Result.success(noticeService.getDetail(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Notice notice) {
        noticeService.publishNotice(notice);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }
}
