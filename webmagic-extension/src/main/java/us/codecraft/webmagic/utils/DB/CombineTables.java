package us.codecraft.webmagic.utils.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }


    public static void main(String[] args){
        CombineTables mysqlToRedis = CombineTables.getInstance();
    }
}
