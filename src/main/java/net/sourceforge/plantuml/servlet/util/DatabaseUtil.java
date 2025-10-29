package net.sourceforge.plantuml.servlet.util;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接池工具类.
 */
public final class DatabaseUtil {
    private static BasicDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("config.properties");
            if (is != null) {
                props.load(is);
                is.close();
            }

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.username"));
            dataSource.setPassword(props.getProperty("db.password"));
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.pool.initialSize", "5")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.pool.maxTotal", "20")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.pool.maxIdle", "10")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Private constructor to hide the implicit public one.
     */
    private DatabaseUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取数据库连接.
     *
     * @return Connection数据库连接
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭连接.
     *
     * @param conn 数据库连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据源.
     *
     * @return BasicDataSource数据源
     */
    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}
