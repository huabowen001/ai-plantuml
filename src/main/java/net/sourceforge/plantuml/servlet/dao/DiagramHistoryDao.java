package net.sourceforge.plantuml.servlet.dao;

import net.sourceforge.plantuml.servlet.model.DiagramHistory;
import net.sourceforge.plantuml.servlet.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 图表历史记录数据访问对象.
 */
public class DiagramHistoryDao {

    /**
     * 创建新的图表历史记录.
     *
     * @param history DiagramHistory对象
     * @return 创建成功返回ID，失败返回null
     */
    public Long create(DiagramHistory history) {
        String sql = "INSERT INTO diagram_history (user_id, title, description, original_text, "
                   + "plantuml_code, diagram_type, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, history.getUserId());
            stmt.setString(2, history.getTitle());
            stmt.setString(3, history.getDescription());
            stmt.setString(4, history.getOriginalText());
            stmt.setString(5, history.getPlantumlCode());
            stmt.setString(6, history.getDiagramType());
            stmt.setString(7, history.getImageUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ID查找图表历史记录.
     *
     * @param id 记录ID
     * @return DiagramHistory对象，如果不存在返回null
     */
    public DiagramHistory findById(Long id) {
        String sql = "SELECT * FROM diagram_history WHERE id = ? AND is_deleted = FALSE";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDiagramHistory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户ID查找所有历史记录.
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 图表历史记录列表
     */
    public List<DiagramHistory> findByUserId(Long userId, int limit, int offset) {
        List<DiagramHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM diagram_history WHERE user_id = ? AND is_deleted = FALSE "
                   + "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(extractDiagramHistory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    /**
     * 获取用户收藏的图表.
     *
     * @param userId 用户ID
     * @return 收藏的图表列表
     */
    public List<DiagramHistory> findFavoritesByUserId(Long userId) {
        List<DiagramHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM diagram_history WHERE user_id = ? AND is_favorite = TRUE "
                   + "AND is_deleted = FALSE ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(extractDiagramHistory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    /**
     * 更新图表历史记录.
     *
     * @param history DiagramHistory对象
     * @return 是否更新成功
     */
    public boolean update(DiagramHistory history) {
        String sql = "UPDATE diagram_history SET title = ?, description = ?, plantuml_code = ?, "
                   + "diagram_type = ?, image_url = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, history.getTitle());
            stmt.setString(2, history.getDescription());
            stmt.setString(3, history.getPlantumlCode());
            stmt.setString(4, history.getDiagramType());
            stmt.setString(5, history.getImageUrl());
            stmt.setLong(6, history.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 切换收藏状态.
     *
     * @param id 记录ID
     * @param isFavorite 是否收藏
     * @return 是否更新成功
     */
    public boolean toggleFavorite(Long id, boolean isFavorite) {
        String sql = "UPDATE diagram_history SET is_favorite = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isFavorite);
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 软删除图表历史记录.
     *
     * @param id 记录ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) {
        String sql = "UPDATE diagram_history SET is_deleted = TRUE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取用户的历史记录总数.
     *
     * @param userId 用户ID
     * @return 记录总数
     */
    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM diagram_history WHERE user_id = ? AND is_deleted = FALSE";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 从ResultSet中提取DiagramHistory对象.
     *
     * @param rs ResultSet
     * @return DiagramHistory对象
     * @throws SQLException SQL异常
     */
    private DiagramHistory extractDiagramHistory(ResultSet rs) throws SQLException {
        DiagramHistory history = new DiagramHistory();
        history.setId(rs.getLong("id"));
        history.setUserId(rs.getLong("user_id"));
        history.setTitle(rs.getString("title"));
        history.setDescription(rs.getString("description"));
        history.setOriginalText(rs.getString("original_text"));
        history.setPlantumlCode(rs.getString("plantuml_code"));
        history.setDiagramType(rs.getString("diagram_type"));
        history.setImageUrl(rs.getString("image_url"));
        history.setCreatedAt(rs.getTimestamp("created_at"));
        history.setUpdatedAt(rs.getTimestamp("updated_at"));
        history.setIsFavorite(rs.getBoolean("is_favorite"));
        history.setIsDeleted(rs.getBoolean("is_deleted"));
        return history;
    }
}

