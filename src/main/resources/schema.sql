-- 创建数据库
CREATE DATABASE IF NOT EXISTS plantuml_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE plantuml_ai;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PlantUML历史记录表
CREATE TABLE IF NOT EXISTS diagram_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    description TEXT,
    original_text TEXT NOT NULL COMMENT '用户输入的原始文本',
    plantuml_code TEXT NOT NULL COMMENT 'AI生成的PlantUML代码',
    diagram_type VARCHAR(50) DEFAULT 'sequence' COMMENT '图表类型：sequence, class, activity, etc.',
    image_url VARCHAR(500) COMMENT '生成的图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_favorite BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_favorite (is_favorite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 图表版本表（保存每个历史记录的修改版本，最多保留20个）
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

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 图表标签关联表
CREATE TABLE IF NOT EXISTS diagram_tags (
    diagram_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (diagram_id, tag_id),
    FOREIGN KEY (diagram_id) REFERENCES diagram_history(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 分享表（可选功能，用于分享图表）
CREATE TABLE IF NOT EXISTS diagram_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diagram_id BIGINT NOT NULL,
    share_token VARCHAR(100) NOT NULL UNIQUE,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_count INT DEFAULT 0,
    FOREIGN KEY (diagram_id) REFERENCES diagram_history(id) ON DELETE CASCADE,
    INDEX idx_share_token (share_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入测试数据（可选）
-- 密码为 'password123' 的 bcrypt hash
INSERT IGNORE INTO users (username, email, password_hash, full_name) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员'),
('demo', 'demo@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '演示用户');

