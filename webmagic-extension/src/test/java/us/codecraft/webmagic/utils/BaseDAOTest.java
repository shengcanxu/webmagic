package us.codecraft.webmagic.utils;

import org.junit.Test;
import us.codecraft.webmagic.modelSpider.pipeline.BaseDAO;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Created by cano on 2015/1/20.
 */
public class BaseDAOTest {

    @Test
    public void executeQueryTest(){
        BaseDAO baseDao=BaseDAO.getInstance("cano");

        String sql="SELECT * FROM testjava where id=?";
        Object[] param= new Object[]{1};
        int[] type={Types.INTEGER};

        List<Map<String, String>> list=(List<Map<String, String>>) baseDao.executeQuery(sql, param, type);
        for (Map<String, String> item : list){
            for(Map.Entry entry : item.entrySet()){
                System.out.println(entry.getKey() + "  " + entry.getValue());
            }
        }
    }

    @Test
    public void executeUpdateTest(){
        BaseDAO baseDao = BaseDAO.getInstance("cano");

        String sql="insert into testjava (`title`,`num`) values (?,?)";
        Object[] param= new Object[]{"hello",323};
        int[] type={Types.VARCHAR, Types.INTEGER};

        int affectNows = baseDao.executeUpdate(sql,param,type);
        System.out.println(affectNows);
    }
}
