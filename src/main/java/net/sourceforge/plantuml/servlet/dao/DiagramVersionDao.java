package net.sourceforge.plantuml.servlet.dao;

import net.sourceforge.plantuml.servlet.model.DiagramVersion;
import net.sourceforge.plantuml.servlet.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 图表版本DAO.
 */
public class DiagramVersionDao {

    /**
     * 创建新版本.
     */
    public Long create(DiagramVersion version) {
        String sql = "INSERT INTO diagram_versions (history_id, version_number, plantuml_code, "
                + "title, change_description, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, version.getHistoryId());
            stmt.setInt(2, version.getVersionNumber());
            stmt.setString(3, version.getPlantumlCode());
            stmt.setString(4, version.getTitle());
            stmt.setString(5, version.getChangeDescription());
            if (version.getCreatedBy() != null) {
                stmt.setLong(6, version.getCreatedBy());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取历史记录的所有版本（按版本号倒序）.
     */
    public List<DiagramVersion> findByHistoryId(Long historyId, int limit) {
        List<DiagramVersion> versions = new ArrayList<>();
        String sql = "SELECT * FROM diagram_versions WHERE history_id = ? "
                + "ORDER BY version_number DESC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historyId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    versions.add(mapResultSetToVersion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return versions;
    }

    /**
     * 根据ID获取版本.
     */
    public DiagramVersion findById(Long id) {
        String sql = "SELECT * FROM diagram_versions WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVersion(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取历史记录的最新版本号.
     */
    public Integer getLatestVersionNumber(Long historyId) {
        String sql = "SELECT MAX(version_number) as max_version FROM diagram_versions WHERE history_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int maxVersion = rs.getInt("max_version");
                    return rs.wasNull() ? 0 : maxVersion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取历史记录的版本数量.
     */
    public int countByHistoryId(Long historyId) {
        String sql = "SELECT COUNT(*) as total FROM diagram_versions WHERE history_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 删除最旧的版本（保持最多20个版本）.
     */
    public void deleteOldestVersions(Long historyId, int keepCount) {
        String sql = "DELETE FROM diagram_versions WHERE history_id = ? "
                + "AND id NOT IN (SELECT id FROM ("
                + "SELECT id FROM diagram_versions WHERE history_id = ? "
                + "ORDER BY version_number DESC LIMIT ?) as t)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historyId);
            stmt.setLong(2, historyId);
            stmt.setInt(3, keepCount);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除历史记录的所有版本.
     */
    public boolean deleteByHistoryId(Long historyId) {
        String sql = "DELETE FROM diagram_versions WHERE history_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 映射ResultSet到DiagramVersion对象.
     */
    private DiagramVersion mapResultSetToVersion(ResultSet rs) throws SQLException {
        DiagramVersion version = new DiagramVersion();
        version.setId(rs.getLong("id"));
        version.setHistoryId(rs.getLong("history_id"));
        version.setVersionNumber(rs.getInt("version_number"));
        version.setPlantumlCode(rs.getString("plantuml_code"));
        version.setTitle(rs.getString("title"));
        version.setChangeDescription(rs.getString("change_description"));
        version.setCreatedAt(rs.getTimestamp("created_at"));
        Long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) {
            version.setCreatedBy(createdBy);
        }
        return version;
    }
}

