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
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/cano";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "";

    private static BaseDAO instance = null;
    private Connection conn = null;

    private void BaseDAO(){ }

    public static BaseDAO getInstance(){
        if(instance == null){
            instance = new BaseDAO();
            instance.getConn();
            return instance;
        }else{
            return instance;
        }
    }

    /**
     *
     * @return 获得数据库连接
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void getConn() {
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
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        instance.close();
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
        PreparedStatement prsts = null;
        try {
            prsts = conn.prepareStatement(sql);
            for (int i = 1; i <= param.length; i++) {
                prsts.setObject(i, param[i - 1], type[i - 1]);
            }
            rows = prsts.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
        return list;
    }

}