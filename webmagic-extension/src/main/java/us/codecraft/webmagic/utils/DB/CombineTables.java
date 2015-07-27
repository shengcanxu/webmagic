package us.codecraft.webmagic.utils.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.modelSpider.pipeline.BaseDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by cano on 2015/5/30.
 * 将两个不同数据库的相同结构的表合并在一起， 并根据重复字段排重
 */
public class CombineTables {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static CombineTables instance = null;

    private CombineTables(){
    }

    public static CombineTables getInstance(){
        if(instance == null){
            instance = new CombineTables();
        }
        return  instance;
    }

    /**
     * 将from的数据拷贝到to里面， 并根据duplicateColumn来排重，如果重复就不插入
     * @param fromDB
     * @param toDB
     * @param fromTable
     * @param toTable
     * @param duplicateColumn
     */
    public void doCombineTables(String fromDB,String toDB, String fromTable, String toTable, String duplicateColumn) {
        BaseDAO toDao = BaseDAO.getInstance(toDB);
        BaseDAO fromDao = BaseDAO.getInstance(fromDB);


        //get the max-id from fromdb
        String sql = "select max(id) as maxid from " + toTable;
        List result = toDao.executeQuery(sql);
        int maxid = ((Map<String, Integer>)result.get(0)).get("maxid");

        //get the number of records from todb
        sql = "select count(id) as countid from " + fromTable;
        List result2 = fromDao.executeQuery(sql);
        long countid = ((Map<String,Long>)result2.get(0)).get("countid");

        int  a = 2;
    }


    public static void main(String[] args){
        CombineTables mysqlToRedis = CombineTables.getInstance();
        mysqlToRedis.doCombineTables("douguo2","douguo","douguocaidancontent","douguocaidancontent","pageUrl");
    }
}
