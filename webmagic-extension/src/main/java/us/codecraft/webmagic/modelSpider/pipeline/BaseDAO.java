package us.codecraft.webmagic.modelSpider.pipeline;

/**
 * Created by cano on 2015/1/20.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDAO {
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "";

    private static Map<String,BaseDAO> instances = new HashMap<>();
    private Connection conn = null;
    private String dbName = null;

    private void BaseDAO(){ }

    public static BaseDAO getInstance(String dbName){
        BaseDAO instance = instances.get(dbName);
        if(instance == null){
            instance = new BaseDAO();
            instance.getConn(dbName);
            instances.put(dbName, instance);
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
    private void getConn(String dbName) {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String connString = URL + dbName + "?characterEncoding=UTF-8";
            conn = DriverManager.getConnection(connString, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void finalize() throws Throwable {
        for(Map.Entry<String,BaseDAO> entry : instances.entrySet()){
            entry.getValue().close();
        }
        instances = null;
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
            prsts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public int executeUpdate(String sql){
        Object[] param = new Object[0];
        int[] type = new int[0];
        return executeUpdate(sql,param, type);
    }

    public List executeQuery(String sql){
        return this.executeQuery(sql,null,null);
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
        List<Map<String,Object>> list = null;
        PreparedStatement prsts = null;
        try {
            prsts = conn.prepareStatement(sql);
            if(param != null) {
                for (int i = 1; i <= param.length; i++) {
                    prsts.setObject(i, param[i - 1], type[i - 1]);
                }
            }
            rs = prsts.executeQuery();
            list = new ArrayList<>();
            ResultSetMetaData rsm = rs.getMetaData();
            while (rs.next()) {
                Map<String,Object> map = new HashMap<>();
                for (int i = 1; i <= rsm.getColumnCount(); i++) {
                    map.put(rsm.getColumnName(i), rs.getObject(rsm.getColumnName(i)));
                }
                list.add(map);
            }
            rs.close();
            prsts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args){
        BaseDAO base1 = BaseDAO.getInstance("cano");
        BaseDAO base2 = BaseDAO.getInstance("cano");

        BaseDAO base3 = BaseDAO.getInstance("duoguo");

    }
}