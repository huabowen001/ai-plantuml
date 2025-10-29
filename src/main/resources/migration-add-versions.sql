-- 数据库迁移脚本：添加版本功能
-- 使用方法：mysql -u root -p plantuml_ai < migration-add-versions.sql

USE plantuml_ai;

-- 创建版本表
CREATE TABLE IF NOT EXISTS diagram_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    history_id BIGINT NOT NULL COMMENT '历史记录ID',
    version_number INT NOT NULL COMMENT '版本号，从1开始递增',
    plantuml_code TEXT NOT NULL COMMENT '该版本的PlantUML代码',
    title VARCHAR(200) COMMENT '版本标题',
    change_description VARCHAR(500) COMMENT '版本变更描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT COMMENT '创建版本的用户ID',
    FOREIGN KEY (history_id) REFERENCES diagram_history(id) ON DELETE CASCADE,
    INDEX idx_history_id (history_id),
    INDEX idx_version_number (version_number),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_history_version (history_id, version_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT '✅ 版本表创建成功！' as status;

