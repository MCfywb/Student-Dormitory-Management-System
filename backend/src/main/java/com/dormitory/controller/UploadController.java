package com.dormitory.controller;

import com.dormitory.common.Result;
import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam Long userId) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            String relativePath = "/upload/avatar/" + datePath + "/";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File avatarDir = new File(uploadPath + "avatar/" + datePath);
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }
            
            String fullPath = uploadPath + "avatar/" + datePath + "/" + fileName;
            File destFile = new File(fullPath);
            file.transferTo(destFile);
            
            String avatarUrl = relativePath + fileName;
            
            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateById(user);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", avatarUrl);
            return Result.success("上传成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/file")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            String relativePath = "/upload/files/" + datePath + "/";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File filesDir = new File(uploadPath + "files/" + datePath);
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            
            String fullPath = uploadPath + "files/" + datePath + "/" + fileName;
            File destFile = new File(fullPath);
            file.transferTo(destFile);
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", relativePath + fileName);
            result.put("name", originalFilename);
            return Result.success("上传成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
