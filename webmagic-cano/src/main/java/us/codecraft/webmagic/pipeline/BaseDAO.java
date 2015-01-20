package us.codecraft.webmagic.pipeline;

/**
 * Created by cano on 2015/1/20.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDAO {
    public final static String DRIVER = "com.mysql.jdbc.Driver";
    public final static String URL = "jdbc:mysql://127.0.0.1:3306/cano";
    public final static String USERNAME = "root";
    public final static String PASSWORD = "";

    /**
     *
     * @return 获得数据库连接
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn 数据库连接
     * @param prsts  PreparedStatement 对象
     * @param rs 结果集
     */
    public void closeAll(Connection conn, PreparedStatement prsts, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (prsts != null) {
            try {
                prsts.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行增、删、改SQL语句
     * @param sql sql语句
     * @param param 值集
     * @param type 值类型集
     * @return 受影响的行数
     */
    public int executeUpdate(String sql, Object[] param, int[] type) {

        int rows = 0;
        Connection conn = this.getConn();
        PreparedStatement prsts = null;
        try {
            prsts = conn.prepareStatement(sql);
            for (int i = 1; i <= param.length; i++) {
                prsts.setObject(i, param[i - 1], type[i - 1]);
            }
            rows = prsts.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeAll(conn, prsts, null);
        }
        return rows;
    }

    /**
     * 执行查询SQL语句
     * @param sql sql语句
     * @param param 值集
     * @param type 值类型集
     * @return 结果集
     */
    public List executeQuery(String sql, Object[] param, int[] type) {
        ResultSet rs = null;
        List list = null;
        Connection conn = this.getConn();
        PreparedStatement prsts = null;
        try {
            prsts = conn.prepareStatement(sql);
            for (int i = 1; i <= param.length; i++) {
                prsts.setObject(i, param[i - 1], type[i - 1]);
            }
            rs = prsts.executeQuery();
            list = new ArrayList();
            ResultSetMetaData rsm = rs.getMetaData();
            Map map = null;
            while (rs.next()) {
                map = new HashMap();
                for (int i = 1; i <= rsm.getColumnCount(); i++) {
                    map.put(rsm.getColumnName(i), rs.getObject(rsm.getColumnName(i)));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            this.closeAll(conn, prsts, rs);
        }
        return list;
    }

}