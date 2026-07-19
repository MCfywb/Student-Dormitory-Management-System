-- =============================================
-- 宿舍事务管理系统数据库初始化脚本
-- 数据库名: dormitory_db
-- 创建时间: 2026
-- =============================================

-- 创建数据库
DROP DATABASE IF EXISTS dormitory_db;
CREATE DATABASE dormitory_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dormitory_db;

-- =============================================
-- 1. 系统用户表 (sys_user)
-- 存储所有用户信息，包括管理员、宿管、学生
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT '/upload/default-avatar.png' COMMENT '头像路径',
    role VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色: admin-管理员, manager-宿管, student-学生',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入用户测试数据
INSERT INTO sys_user (username, password, real_name, gender, phone, email, role, status) VALUES
('admin', '123456', '系统管理员', '男', '13800000001', 'admin@dormitory.com', 'admin', 1),
('manager1', '123456', '张宿管', '男', '13800000002', 'manager1@dormitory.com', 'manager', 1),
('manager2', '123456', '李宿管', '女', '13800000003', 'manager2@dormitory.com', 'manager', 1),
('manager3', '123456', '王宿管', '男', '13800000004', 'manager3@dormitory.com', 'manager', 1),
('2021001001', '123456', '张三', '男', '13900001001', 'zhangsan@stu.edu.cn', 'student', 1),
('2021001002', '123456', '李四', '男', '13900001002', 'lisi@stu.edu.cn', 'student', 1),
('2021001003', '123456', '王五', '男', '13900001003', 'wangwu@stu.edu.cn', 'student', 1),
('2021001004', '123456', '赵六', '女', '13900001004', 'zhaoliu@stu.edu.cn', 'student', 1),
('2021001005', '123456', '孙七', '女', '13900001005', 'sunqi@stu.edu.cn', 'student', 1),
('2021001006', '123456', '周八', '男', '13900001006', 'zhouba@stu.edu.cn', 'student', 1),
('2021001007', '123456', '吴九', '男', '13900001007', 'wujiu@stu.edu.cn', 'student', 1),
('2021001008', '123456', '郑十', '女', '13900001008', 'zhengshi@stu.edu.cn', 'student', 1),
('2021002001', '123456', '刘明', '男', '13900002001', 'liuming@stu.edu.cn', 'student', 1),
('2021002002', '123456', '陈红', '女', '13900002002', 'chenhong@stu.edu.cn', 'student', 1),
('2021002003', '123456', '杨洋', '男', '13900002003', 'yangyang@stu.edu.cn', 'student', 1),
('2021002004', '123456', '黄丽', '女', '13900002004', 'huangli@stu.edu.cn', 'student', 1),
('2021002005', '123456', '林峰', '男', '13900002005', 'linfeng@stu.edu.cn', 'student', 1),
('2021002006', '123456', '何静', '女', '13900002006', 'hejing@stu.edu.cn', 'student', 1),
('2021003001', '123456', '马超', '男', '13900003001', 'machao@stu.edu.cn', 'student', 1),
('2021003002', '123456', '高燕', '女', '13900003002', 'gaoyan@stu.edu.cn', 'student', 1),
('2021003003', '123456', '罗伟', '男', '13900003003', 'luowei@stu.edu.cn', 'student', 1),
('2021003004', '123456', '梁婷', '女', '13900003004', 'liangting@stu.edu.cn', 'student', 1),
('2021003005', '123456', '宋强', '男', '13900003005', 'songqiang@stu.edu.cn', 'student', 1),
('2021003006', '123456', '唐芳', '女', '13900003006', 'tangfang@stu.edu.cn', 'student', 1);

-- =============================================
-- 2. 宿舍楼表 (building)
-- 存储宿舍楼基本信息
-- =============================================
DROP TABLE IF EXISTS building;
CREATE TABLE building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋ID',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    building_type VARCHAR(20) NOT NULL COMMENT '楼栋类型: male-男生楼, female-女生楼',
    floors INT NOT NULL DEFAULT 6 COMMENT '楼层数',
    rooms_per_floor INT NOT NULL DEFAULT 20 COMMENT '每层房间数',
    manager_id BIGINT COMMENT '宿管ID',
    manager_name VARCHAR(50) COMMENT '宿管姓名',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- 插入宿舍楼测试数据
INSERT INTO building (building_name, building_type, floors, rooms_per_floor, manager_id, manager_name, description) VALUES
('1号楼', 'male', 6, 20, 2, '张宿管', '男生宿舍楼，靠近食堂'),
('2号楼', 'male', 6, 20, 2, '张宿管', '男生宿舍楼，靠近操场'),
('3号楼', 'female', 6, 20, 3, '李宿管', '女生宿舍楼，靠近图书馆'),
('4号楼', 'female', 6, 20, 3, '李宿管', '女生宿舍楼，靠近教学楼'),
('5号楼', 'male', 8, 25, 4, '王宿管', '男生宿舍楼，新建楼栋');

-- =============================================
-- 3. 宿舍房间表 (room)
-- 存储宿舍房间信息
-- =============================================
DROP TABLE IF EXISTS room;
CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    building_id BIGINT NOT NULL COMMENT '所属楼栋ID',
    building_name VARCHAR(50) COMMENT '所属楼栋名称',
    floor INT NOT NULL COMMENT '所在楼层',
    capacity INT NOT NULL DEFAULT 4 COMMENT '床位容量',
    current_count INT DEFAULT 0 COMMENT '当前入住人数',
    room_type VARCHAR(20) DEFAULT 'standard' COMMENT '房间类型: standard-标准间, superior-优越间',
    status VARCHAR(20) DEFAULT 'available' COMMENT '状态: available-可入住, full-已满, maintenance-维修中',
    price DECIMAL(10,2) DEFAULT 1200.00 COMMENT '住宿费(元/学期)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_room (room_number, building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍房间表';

-- 插入房间测试数据 (1号楼部分房间)
INSERT INTO room (room_number, building_id, building_name, floor, capacity, current_count, room_type, status, price) VALUES
('101', 1, '1号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('102', 1, '1号楼', 1, 4, 3, 'standard', 'available', 1200.00),
('103', 1, '1号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('104', 1, '1号楼', 1, 4, 2, 'standard', 'available', 1200.00),
('201', 1, '1号楼', 2, 4, 4, 'standard', 'full', 1200.00),
('202', 1, '1号楼', 2, 4, 4, 'standard', 'full', 1200.00),
('203', 1, '1号楼', 2, 4, 3, 'standard', 'available', 1200.00),
('204', 1, '1号楼', 2, 4, 1, 'standard', 'available', 1200.00),
('301', 1, '1号楼', 3, 4, 4, 'standard', 'full', 1200.00),
('302', 1, '1号楼', 3, 4, 2, 'standard', 'available', 1200.00),
-- 2号楼部分房间
('101', 2, '2号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('102', 2, '2号楼', 1, 4, 3, 'standard', 'available', 1200.00),
('103', 2, '2号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('201', 2, '2号楼', 2, 4, 4, 'standard', 'full', 1200.00),
('202', 2, '2号楼', 2, 4, 2, 'standard', 'available', 1200.00),
-- 3号楼部分房间(女生楼)
('101', 3, '3号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('102', 3, '3号楼', 1, 4, 3, 'standard', 'available', 1200.00),
('103', 3, '3号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('201', 3, '3号楼', 2, 4, 4, 'standard', 'full', 1200.00),
('202', 3, '3号楼', 2, 4, 2, 'standard', 'available', 1200.00),
-- 4号楼部分房间(女生楼)
('101', 4, '4号楼', 1, 4, 4, 'standard', 'full', 1200.00),
('102', 4, '4号楼', 1, 4, 3, 'standard', 'available', 1200.00),
('201', 4, '4号楼', 2, 4, 4, 'standard', 'full', 1200.00),
('202', 4, '4号楼', 2, 4, 1, 'standard', 'available', 1200.00),
-- 5号楼部分房间
('101', 5, '5号楼', 1, 4, 4, 'superior', 'full', 1500.00),
('102', 5, '5号楼', 1, 4, 2, 'superior', 'available', 1500.00),
('201', 5, '5号楼', 2, 4, 3, 'superior', 'available', 1500.00);

-- =============================================
-- 4. 学生详细信息表 (student_info)
-- 存储学生详细信息，与用户表关联
-- =============================================
DROP TABLE IF EXISTS student_info;
CREATE TABLE student_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生信息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    real_name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(50) COMMENT '邮箱',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(50) COMMENT '班级',
    grade VARCHAR(20) COMMENT '年级',
    id_card VARCHAR(20) COMMENT '身份证号',
    room_id BIGINT COMMENT '宿舍房间ID',
    room_number VARCHAR(20) COMMENT '房间号',
    building_id BIGINT COMMENT '楼栋ID',
    building_name VARCHAR(50) COMMENT '楼栋名称',
    bed_number INT COMMENT '床位号',
    check_in_time DATETIME COMMENT '入住时间',
    status VARCHAR(20) DEFAULT 'checked_in' COMMENT '状态: checked_in-已入住, checked_out-已退宿',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生详细信息表';

-- 插入学生详细信息测试数据
INSERT INTO student_info (user_id, student_no, real_name, gender, phone, email, college, major, class_name, grade, room_id, room_number, building_id, building_name, bed_number, check_in_time, status) VALUES
(5, '2021001001', '张三', '男', '13900001001', 'zhangsan@stu.edu.cn', '计算机学院', '软件工程', '软工2101班', '2021级', 1, '101', 1, '1号楼', 1, '2021-09-01 10:00:00', 'checked_in'),
(6, '2021001002', '李四', '男', '13900001002', 'lisi@stu.edu.cn', '计算机学院', '软件工程', '软工2101班', '2021级', 1, '101', 1, '1号楼', 2, '2021-09-01 10:30:00', 'checked_in'),
(7, '2021001003', '王五', '男', '13900001003', 'wangwu@stu.edu.cn', '计算机学院', '计算机科学', '计科2101班', '2021级', 1, '101', 1, '1号楼', 3, '2021-09-01 11:00:00', 'checked_in'),
(8, '2021001004', '赵六', '女', '13900001004', 'zhaoliu@stu.edu.cn', '外国语学院', '英语', '英语2101班', '2021级', 17, '101', 3, '3号楼', 1, '2021-09-01 14:00:00', 'checked_in'),
(9, '2021001005', '孙七', '女', '13900001005', 'sunqi@stu.edu.cn', '外国语学院', '英语', '英语2101班', '2021级', 17, '101', 3, '3号楼', 2, '2021-09-01 14:30:00', 'checked_in'),
(10, '2021001006', '周八', '男', '13900001006', 'zhouba@stu.edu.cn', '机械学院', '机械工程', '机械2101班', '2021级', 2, '102', 1, '1号楼', 1, '2021-09-02 09:00:00', 'checked_in'),
(11, '2021001007', '吴九', '男', '13900001007', 'wujiu@stu.edu.cn', '机械学院', '机械工程', '机械2101班', '2021级', 2, '102', 1, '1号楼', 2, '2021-09-02 09:30:00', 'checked_in'),
(12, '2021001008', '郑十', '女', '13900001008', 'zhengshi@stu.edu.cn', '经管学院', '工商管理', '工商2101班', '2021级', 18, '102', 3, '3号楼', 1, '2021-09-02 10:00:00', 'checked_in'),
(13, '2021002001', '刘明', '男', '13900002001', 'liuming@stu.edu.cn', '计算机学院', '软件工程', '软工2102班', '2021级', 5, '201', 1, '1号楼', 1, '2021-09-03 09:00:00', 'checked_in'),
(14, '2021002002', '陈红', '女', '13900002002', 'chenhong@stu.edu.cn', '外国语学院', '日语', '日语2101班', '2021级', 17, '101', 3, '3号楼', 3, '2021-09-03 10:00:00', 'checked_in'),
(15, '2021002003', '杨洋', '男', '13900002003', 'yangyang@stu.edu.cn', '计算机学院', '计算机科学', '计科2102班', '2021级', 5, '201', 1, '1号楼', 2, '2021-09-03 11:00:00', 'checked_in'),
(16, '2021002004', '黄丽', '女', '13900002004', 'huangli@stu.edu.cn', '经管学院', '会计学', '会计2101班', '2021级', 18, '102', 3, '3号楼', 2, '2021-09-03 14:00:00', 'checked_in'),
(17, '2021002005', '林峰', '男', '13900002005', 'linfeng@stu.edu.cn', '机械学院', '自动化', '自动化2101班', '2021级', 6, '202', 1, '1号楼', 1, '2021-09-04 09:00:00', 'checked_in'),
(18, '2021002006', '何静', '女', '13900002006', 'hejing@stu.edu.cn', '外国语学院', '英语', '英语2102班', '2021级', 19, '103', 3, '3号楼', 1, '2021-09-04 10:00:00', 'checked_in'),
(19, '2021003001', '马超', '男', '13900003001', 'machao@stu.edu.cn', '计算机学院', '网络工程', '网络2101班', '2021级', 11, '101', 2, '2号楼', 1, '2021-09-05 09:00:00', 'checked_in'),
(20, '2021003002', '高燕', '女', '13900003002', 'gaoyan@stu.edu.cn', '经管学院', '金融学', '金融2101班', '2021级', 21, '101', 4, '4号楼', 1, '2021-09-05 10:00:00', 'checked_in'),
(21, '2021003003', '罗伟', '男', '13900003003', 'luowei@stu.edu.cn', '机械学院', '机械工程', '机械2102班', '2021级', 11, '101', 2, '2号楼', 2, '2021-09-05 11:00:00', 'checked_in'),
(22, '2021003004', '梁婷', '女', '13900003004', 'liangting@stu.edu.cn', '外国语学院', '法语', '法语2101班', '2021级', 21, '101', 4, '4号楼', 2, '2021-09-05 14:00:00', 'checked_in'),
(23, '2021003005', '宋强', '男', '13900003005', 'songqiang@stu.edu.cn', '计算机学院', '软件工程', '软工2103班', '2021级', 14, '201', 2, '2号楼', 1, '2021-09-06 09:00:00', 'checked_in'),
(24, '2021003006', '唐芳', '女', '13900003006', 'tangfang@stu.edu.cn', '经管学院', '市场营销', '营销2101班', '2021级', 23, '201', 4, '4号楼', 1, '2021-09-06 10:00:00', 'checked_in');

-- =============================================
-- 5. 宿舍卫生检查表 (hygiene)
-- 记录宿舍卫生检查情况
-- =============================================
DROP TABLE IF EXISTS hygiene;
CREATE TABLE hygiene (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '卫生检查ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    check_date DATE NOT NULL COMMENT '检查日期',
    score INT NOT NULL COMMENT '卫生评分(0-100)',
    level VARCHAR(20) COMMENT '等级: excellent-优秀, good-良好, qualified-合格, unqualified-不合格',
    checker_id BIGINT COMMENT '检查人ID',
    checker_name VARCHAR(50) COMMENT '检查人姓名',
    remark VARCHAR(500) COMMENT '检查备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍卫生检查表';

-- 插入卫生检查测试数据
INSERT INTO hygiene (room_id, room_number, building_id, building_name, check_date, score, level, checker_id, checker_name, remark) VALUES
(1, '101', 1, '1号楼', '2024-03-01', 95, 'excellent', 2, '张宿管', '宿舍整洁，物品摆放整齐'),
(1, '101', 1, '1号楼', '2024-03-08', 88, 'good', 2, '张宿管', '整体良好，地面需加强清洁'),
(1, '101', 1, '1号楼', '2024-03-15', 92, 'excellent', 2, '张宿管', '保持良好'),
(2, '102', 1, '1号楼', '2024-03-01', 85, 'good', 2, '张宿管', '阳台需要整理'),
(2, '102', 1, '1号楼', '2024-03-08', 90, 'excellent', 2, '张宿管', '进步明显'),
(2, '102', 1, '1号楼', '2024-03-15', 87, 'good', 2, '张宿管', '继续保持'),
(5, '201', 1, '1号楼', '2024-03-01', 78, 'qualified', 2, '张宿管', '桌面杂乱，需要整改'),
(5, '201', 1, '1号楼', '2024-03-08', 82, 'good', 2, '张宿管', '有所改善'),
(5, '201', 1, '1号楼', '2024-03-15', 85, 'good', 2, '张宿管', '继续努力'),
(17, '101', 3, '3号楼', '2024-03-01', 96, 'excellent', 3, '李宿管', '女生宿舍保持得很好'),
(17, '101', 3, '3号楼', '2024-03-08', 94, 'excellent', 3, '李宿管', '优秀'),
(17, '101', 3, '3号楼', '2024-03-15', 98, 'excellent', 3, '李宿管', '模范宿舍'),
(18, '102', 3, '3号楼', '2024-03-01', 89, 'good', 3, '李宿管', '良好'),
(18, '102', 3, '3号楼', '2024-03-08', 91, 'excellent', 3, '李宿管', '进步'),
(18, '102', 3, '3号楼', '2024-03-15', 93, 'excellent', 3, '李宿管', '继续保持'),
(11, '101', 2, '2号楼', '2024-03-01', 72, 'qualified', 2, '张宿管', '需要加强卫生意识'),
(11, '101', 2, '2号楼', '2024-03-08', 80, 'good', 2, '张宿管', '有所改善'),
(11, '101', 2, '2号楼', '2024-03-15', 84, 'good', 2, '张宿管', '继续改进'),
(21, '101', 4, '4号楼', '2024-03-01', 92, 'excellent', 3, '李宿管', '整洁'),
(21, '101', 4, '4号楼', '2024-03-08', 90, 'excellent', 3, '李宿管', '良好'),
(21, '101', 4, '4号楼', '2024-03-15', 95, 'excellent', 3, '李宿管', '优秀'),
(25, '101', 5, '5号楼', '2024-03-01', 97, 'excellent', 4, '王宿管', '新楼栋保持得很好'),
(25, '101', 5, '5号楼', '2024-03-08', 96, 'excellent', 4, '王宿管', '优秀'),
(25, '101', 5, '5号楼', '2024-03-15', 98, 'excellent', 4, '王宿管', '模范宿舍');

-- =============================================
-- 6. 设施表 (facility)
-- 存储宿舍设施基本信息
-- =============================================
DROP TABLE IF EXISTS facility;
CREATE TABLE facility (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设施ID',
    facility_name VARCHAR(50) NOT NULL COMMENT '设施名称',
    facility_type VARCHAR(30) NOT NULL COMMENT '设施类型: furniture-家具, appliance-电器, other-其他',
    room_id BIGINT COMMENT '所属房间ID',
    room_number VARCHAR(20) COMMENT '房间号',
    building_id BIGINT COMMENT '楼栋ID',
    building_name VARCHAR(50) COMMENT '楼栋名称',
    purchase_date DATE COMMENT '购买日期',
    price DECIMAL(10,2) COMMENT '价格',
    status VARCHAR(20) DEFAULT 'normal' COMMENT '状态: normal-正常, damaged-损坏, repairing-维修中, scrapped-已报废',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施表';

-- 插入设施测试数据
INSERT INTO facility (facility_name, facility_type, room_id, room_number, building_id, building_name, purchase_date, price, status, remark) VALUES
-- 1号楼101设施
('床铺', 'furniture', 1, '101', 1, '1号楼', '2020-08-01', 800.00, 'normal', '上下铺'),
('床铺', 'furniture', 1, '101', 1, '1号楼', '2020-08-01', 800.00, 'normal', '上下铺'),
('书桌', 'furniture', 1, '101', 1, '1号楼', '2020-08-01', 350.00, 'normal', '学生书桌'),
('衣柜', 'furniture', 1, '101', 1, '1号楼', '2020-08-01', 500.00, 'normal', '四门衣柜'),
('空调', 'appliance', 1, '101', 1, '1号楼', '2020-08-01', 2800.00, 'normal', '1.5匹挂机'),
('热水器', 'appliance', 1, '101', 1, '1号楼', '2020-08-01', 1500.00, 'damaged', '需要维修'),
-- 1号楼102设施
('床铺', 'furniture', 2, '102', 1, '1号楼', '2020-08-01', 800.00, 'normal', '上下铺'),
('书桌', 'furniture', 2, '102', 1, '1号楼', '2020-08-01', 350.00, 'damaged', '桌面有划痕'),
('空调', 'appliance', 2, '102', 1, '1号楼', '2020-08-01', 2800.00, 'normal', '1.5匹挂机'),
-- 1号楼201设施
('床铺', 'furniture', 5, '201', 1, '1号楼', '2020-08-01', 800.00, 'normal', '上下铺'),
('空调', 'appliance', 5, '201', 1, '1号楼', '2020-08-01', 2800.00, 'repairing', '正在维修中'),
-- 3号楼101设施
('床铺', 'furniture', 17, '101', 3, '3号楼', '2021-08-01', 850.00, 'normal', '上下铺'),
('书桌', 'furniture', 17, '101', 3, '3号楼', '2021-08-01', 380.00, 'normal', '学生书桌'),
('空调', 'appliance', 17, '101', 3, '3号楼', '2021-08-01', 3000.00, 'normal', '1.5匹变频'),
('洗衣机', 'appliance', 17, '101', 3, '3号楼', '2021-08-01', 2000.00, 'normal', '小型洗衣机'),
-- 3号楼102设施
('床铺', 'furniture', 18, '102', 3, '3号楼', '2021-08-01', 850.00, 'normal', '上下铺'),
('空调', 'appliance', 18, '102', 3, '3号楼', '2021-08-01', 3000.00, 'normal', '1.5匹变频'),
-- 5号楼101设施(新楼设施较新)
('床铺', 'furniture', 25, '101', 5, '5号楼', '2023-08-01', 1000.00, 'normal', '新式上下铺'),
('书桌', 'furniture', 25, '101', 5, '5号楼', '2023-08-01', 450.00, 'normal', '组合书桌'),
('空调', 'appliance', 25, '101', 5, '5号楼', '2023-08-01', 3500.00, 'normal', '2匹变频'),
('冰箱', 'appliance', 25, '101', 5, '5号楼', '2023-08-01', 1800.00, 'normal', '小型冰箱');

-- =============================================
-- 7. 设施维修记录表 (facility_repair)
-- 记录设施维修申请和处理情况
-- =============================================
DROP TABLE IF EXISTS facility_repair;
CREATE TABLE facility_repair (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '维修记录ID',
    facility_id BIGINT NOT NULL COMMENT '设施ID',
    facility_name VARCHAR(50) COMMENT '设施名称',
    room_id BIGINT COMMENT '房间ID',
    room_number VARCHAR(20) COMMENT '房间号',
    building_name VARCHAR(50) COMMENT '楼栋名称',
    reporter_id BIGINT COMMENT '报修人ID',
    reporter_name VARCHAR(50) COMMENT '报修人姓名',
    report_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    problem_desc VARCHAR(500) COMMENT '问题描述',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待处理, processing-处理中, completed-已完成, rejected-已拒绝',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    handle_time DATETIME COMMENT '处理时间',
    handle_result VARCHAR(500) COMMENT '处理结果',
    repair_cost DECIMAL(10,2) COMMENT '维修费用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施维修记录表';

-- 插入维修记录测试数据
INSERT INTO facility_repair (facility_id, facility_name, room_id, room_number, building_name, reporter_id, reporter_name, report_time, problem_desc, status, handler_id, handler_name, handle_time, handle_result, repair_cost) VALUES
(6, '热水器', 1, '101', '1号楼', 5, '张三', '2024-03-10 09:30:00', '热水器不加热，可能是加热管坏了', 'completed', 2, '张宿管', '2024-03-11 14:00:00', '已更换加热管，正常使用', 150.00),
(8, '书桌', 2, '102', '1号楼', 10, '周八', '2024-03-12 10:00:00', '书桌桌面有较大划痕，影响使用', 'completed', 2, '张宿管', '2024-03-13 15:00:00', '已更换桌面', 200.00),
(11, '空调', 5, '201', '1号楼', 13, '刘明', '2024-03-14 08:00:00', '空调制冷效果不好，需要加氟', 'processing', 2, '张宿管', NULL, '正在联系维修人员', NULL),
(3, '书桌', 1, '101', '1号楼', 6, '李四', '2024-02-20 14:00:00', '书桌抽屉把手松动', 'completed', 2, '张宿管', '2024-02-21 10:00:00', '已拧紧螺丝', 0.00);

-- =============================================
-- 8. 费用类型表 (fee_type)
-- 存储费用类型信息
-- =============================================
DROP TABLE IF EXISTS fee_type;
CREATE TABLE fee_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '费用类型ID',
    type_name VARCHAR(50) NOT NULL COMMENT '类型名称',
    type_code VARCHAR(30) NOT NULL UNIQUE COMMENT '类型编码',
    price DECIMAL(10,2) NOT NULL COMMENT '费用金额',
    unit VARCHAR(20) COMMENT '单位',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用类型表';

-- 插入费用类型测试数据
INSERT INTO fee_type (type_name, type_code, price, unit, description, status) VALUES
('住宿费', 'accommodation', 1200.00, '元/学期', '学生宿舍住宿费用', 1),
('水电费', 'utilities', 100.00, '元/月', '宿舍水电费用', 1),
('网费', 'internet', 50.00, '元/月', '宿舍网络费用', 1),
('空调费', 'aircon', 30.00, '元/月', '空调使用费用', 1),
('押金', 'deposit', 200.00, '元', '入住押金', 1),
('钥匙费', 'key', 20.00, '元', '钥匙配制费用', 1);

-- =============================================
-- 9. 费用记录表 (fee_record)
-- 记录学生费用缴纳情况
-- =============================================
DROP TABLE IF EXISTS fee_record;
CREATE TABLE fee_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '费用记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_no VARCHAR(20) COMMENT '学号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    fee_type_id BIGINT NOT NULL COMMENT '费用类型ID',
    fee_type_name VARCHAR(50) COMMENT '费用类型名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '费用金额',
    pay_status VARCHAR(20) DEFAULT 'unpaid' COMMENT '支付状态: unpaid-未支付, paid-已支付, overdue-已逾期',
    pay_time DATETIME COMMENT '支付时间',
    pay_method VARCHAR(20) COMMENT '支付方式: cash-现金, wechat-微信, alipay-支付宝, card-银行卡',
    academic_year VARCHAR(20) COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    due_date DATE COMMENT '应缴日期',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用记录表';

-- 插入费用记录测试数据
INSERT INTO fee_record (student_id, student_no, student_name, fee_type_id, fee_type_name, amount, pay_status, pay_time, pay_method, academic_year, semester, due_date, remark) VALUES
-- 2023-2024学年第一学期费用
(1, '2021001001', '张三', 1, '住宿费', 1200.00, 'paid', '2023-09-05 10:00:00', 'wechat', '2023-2024', '第一学期', '2023-09-10', '按时缴纳'),
(1, '2021001001', '张三', 2, '水电费', 300.00, 'paid', '2023-09-05 10:05:00', 'wechat', '2023-2024', '第一学期', '2023-09-10', '预缴三个月'),
(2, '2021001002', '李四', 1, '住宿费', 1200.00, 'paid', '2023-09-06 14:00:00', 'alipay', '2023-2024', '第一学期', '2023-09-10', '按时缴纳'),
(2, '2021001002', '李四', 2, '水电费', 300.00, 'paid', '2023-09-06 14:05:00', 'alipay', '2023-2024', '第一学期', '2023-09-10', '预缴三个月'),
(3, '2021001003', '王五', 1, '住宿费', 1200.00, 'paid', '2023-09-08 09:00:00', 'card', '2023-2024', '第一学期', '2023-09-10', '按时缴纳'),
(4, '2021001004', '赵六', 1, '住宿费', 1200.00, 'paid', '2023-09-07 11:00:00', 'wechat', '2023-2024', '第一学期', '2023-09-10', '按时缴纳'),
(5, '2021001005', '孙七', 1, '住宿费', 1200.00, 'paid', '2023-09-09 16:00:00', 'alipay', '2023-2024', '第一学期', '2023-09-10', '按时缴纳'),
-- 2023-2024学年第二学期费用
(1, '2021001001', '张三', 1, '住宿费', 1200.00, 'paid', '2024-02-25 10:00:00', 'wechat', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(1, '2021001001', '张三', 2, '水电费', 300.00, 'paid', '2024-02-25 10:05:00', 'wechat', '2023-2024', '第二学期', '2024-03-01', '预缴三个月'),
(2, '2021001002', '李四', 1, '住宿费', 1200.00, 'paid', '2024-02-26 14:00:00', 'alipay', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(3, '2021001003', '王五', 1, '住宿费', 1200.00, 'paid', '2024-02-27 09:00:00', 'card', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(4, '2021001004', '赵六', 1, '住宿费', 1200.00, 'unpaid', NULL, NULL, '2023-2024', '第二学期', '2024-03-01', '待缴纳'),
(5, '2021001005', '孙七', 1, '住宿费', 1200.00, 'paid', '2024-02-28 16:00:00', 'wechat', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(6, '2021001006', '周八', 1, '住宿费', 1200.00, 'paid', '2024-02-25 09:00:00', 'alipay', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(7, '2021001007', '吴九', 1, '住宿费', 1200.00, 'overdue', NULL, NULL, '2023-2024', '第二学期', '2024-03-01', '逾期未缴'),
(8, '2021001008', '郑十', 1, '住宿费', 1200.00, 'paid', '2024-02-26 10:00:00', 'wechat', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(9, '2021002001', '刘明', 1, '住宿费', 1200.00, 'paid', '2024-02-27 11:00:00', 'card', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(10, '2021002002', '陈红', 1, '住宿费', 1200.00, 'paid', '2024-02-28 14:00:00', 'alipay', '2023-2024', '第二学期', '2024-03-01', '按时缴纳'),
(11, '2021002003', '杨洋', 1, '住宿费', 1200.00, 'unpaid', NULL, NULL, '2023-2024', '第二学期', '2024-03-01', '待缴纳'),
(12, '2021002004', '黄丽', 1, '住宿费', 1200.00, 'paid', '2024-02-25 15:00:00', 'wechat', '2023-2024', '第二学期', '2024-03-01', '按时缴纳');

-- =============================================
-- 10. 公告通知表 (notice)
-- 存储系统公告和通知
-- =============================================
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    notice_type VARCHAR(20) DEFAULT 'notice' COMMENT '类型: notice-通知, announcement-公告, warning-警告',
    publisher_id BIGINT COMMENT '发布人ID',
    publisher_name VARCHAR(50) COMMENT '发布人姓名',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告通知表';

-- 插入公告测试数据
INSERT INTO notice (title, content, notice_type, publisher_id, publisher_name, publish_time, status, view_count) VALUES
('关于2024年春季学期宿舍卫生检查的通知', '各位同学：\n\n为了营造良好的宿舍环境，学校将于每周五下午进行宿舍卫生检查。请各位同学做好宿舍卫生工作，保持宿舍整洁。\n\n检查标准：\n1. 地面干净无杂物\n2. 床铺整洁\n3. 物品摆放整齐\n4. 阳台整洁\n\n请相互转告。', 'notice', 1, '系统管理员', '2024-03-01 09:00:00', 1, 156),
('宿舍用电安全提醒', '各位同学：\n\n近期发现部分宿舍存在违规使用大功率电器的情况，为确保宿舍用电安全，特此提醒：\n\n1. 禁止使用电饭煲、电磁炉等大功率电器\n2. 禁止私拉电线\n3. 离开宿舍请关闭电源\n\n违规者将按学校规定处理。', 'warning', 2, '张宿管', '2024-03-05 10:00:00', 1, 89),
('2024年宿舍费用缴纳通知', '各位同学：\n\n2024年春季学期宿舍费用缴纳工作即将开始，请于2024年3月1日前完成缴费。\n\n缴费方式：\n1. 微信支付\n2. 支付宝支付\n3. 银行卡转账\n4. 现金缴纳\n\n逾期未缴费将影响正常住宿。', 'announcement', 1, '系统管理员', '2024-02-20 08:00:00', 1, 234),
('宿舍门禁时间调整通知', '各位同学：\n\n因疫情防控需要，宿舍门禁时间调整为：\n\n周日至周四：22:00\n周五至周六：23:00\n\n请同学们按时回宿舍，配合宿管工作。', 'notice', 3, '李宿管', '2024-03-10 14:00:00', 1, 178),
('关于开展宿舍安全检查的通知', '各位同学：\n\n学校将于3月20日开展宿舍安全检查，请做好以下准备：\n\n1. 清理宿舍杂物\n2. 检查用电安全\n3. 整理消防通道\n\n请相互转告。', 'notice', 1, '系统管理员', '2024-03-15 09:00:00', 1, 67);

-- =============================================
-- 11. 来访登记表 (visitor)
-- 记录宿舍来访人员信息
-- =============================================
DROP TABLE IF EXISTS visitor;
CREATE TABLE visitor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '来访ID',
    visitor_name VARCHAR(50) NOT NULL COMMENT '来访人姓名',
    visitor_phone VARCHAR(20) COMMENT '来访人电话',
    visitor_id_card VARCHAR(20) COMMENT '来访人身份证号',
    visit_reason VARCHAR(255) COMMENT '来访事由',
    building_id BIGINT COMMENT '访问楼栋ID',
    building_name VARCHAR(50) COMMENT '访问楼栋名称',
    room_id BIGINT COMMENT '访问房间ID',
    room_number VARCHAR(20) COMMENT '访问房间号',
    visited_student_id BIGINT COMMENT '被访学生ID',
    visited_student_name VARCHAR(50) COMMENT '被访学生姓名',
    visit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '来访时间',
    leave_time DATETIME COMMENT '离开时间',
    status VARCHAR(20) DEFAULT 'visiting' COMMENT '状态: visiting-访问中, left-已离开',
    register_id BIGINT COMMENT '登记人ID',
    register_name VARCHAR(50) COMMENT '登记人姓名',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来访登记表';

-- 插入来访登记测试数据
INSERT INTO visitor (visitor_name, visitor_phone, visitor_id_card, visit_reason, building_id, building_name, room_id, room_number, visited_student_id, visited_student_name, visit_time, leave_time, status, register_id, register_name, remark) VALUES
('张父', '13600001001', '310101198001011234', '家长探望', 1, '1号楼', 1, '101', 1, '张三', '2024-03-10 09:00:00', '2024-03-10 12:00:00', 'left', 2, '张宿管', '携带水果'),
('李母', '13600001002', '310101198202025678', '送生活用品', 1, '1号楼', 2, '102', 2, '李四', '2024-03-11 14:00:00', '2024-03-11 16:30:00', 'left', 2, '张宿管', '送换季衣物'),
('王兄', '13600001003', '310101199503039012', '探望弟弟', 1, '1号楼', 1, '101', 3, '王五', '2024-03-12 10:00:00', '2024-03-12 11:30:00', 'left', 2, '张宿管', ''),
('赵父', '13600001004', '310101197804044321', '家长探望', 3, '3号楼', 17, '101', 4, '赵六', '2024-03-13 15:00:00', NULL, 'visiting', 3, '李宿管', '预计下午5点离开'),
('陈姐', '13600001005', '310101199606066789', '送物品', 3, '3号楼', 17, '101', 10, '陈红', '2024-03-14 09:30:00', '2024-03-14 10:00:00', 'left', 3, '李宿管', '');

-- =============================================
-- 创建索引
-- =============================================
CREATE INDEX idx_user_role ON sys_user(role);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_building_type ON building(building_type);
CREATE INDEX idx_room_building ON room(building_id);
CREATE INDEX idx_room_status ON room(status);
CREATE INDEX idx_student_room ON student_info(room_id);
CREATE INDEX idx_student_building ON student_info(building_id);
CREATE INDEX idx_hygiene_room ON hygiene(room_id);
CREATE INDEX idx_hygiene_date ON hygiene(check_date);
CREATE INDEX idx_facility_room ON facility(room_id);
CREATE INDEX idx_facility_status ON facility(status);
CREATE INDEX idx_repair_status ON facility_repair(status);
CREATE INDEX idx_fee_student ON fee_record(student_id);
CREATE INDEX idx_fee_status ON fee_record(pay_status);
CREATE INDEX idx_notice_status ON notice(status);

-- =============================================
-- 数据库初始化完成
-- =============================================
SELECT '数据库初始化完成！' AS message;
