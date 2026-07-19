package com.dormitory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 宿舍事务管理系统主启动类
 * @author DormitorySystem
 */
@SpringBootApplication
@MapperScan("com.dormitory.mapper")
public class DormitoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);
        System.out.println("========================================");
        System.out.println("   宿舍事务管理系统启动成功！");
        System.out.println("   后端地址: http://localhost:8000/api");
        System.out.println("========================================");
    }
}
