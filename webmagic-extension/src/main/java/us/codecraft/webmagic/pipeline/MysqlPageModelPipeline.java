package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.annotation.ResetDB;
import us.codecraft.webmagic.utils.BaseDAO;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * Created by canoxu on 2015/1/20.
 */
public class MysqlPageModelPipeline implements PageModelPipeline{
    public static enum STATUS {Success,Failure,NotStarted}

    private STATUS status = STATUS.NotStarted;
    private BaseDAO dao = BaseDAO.getInstance();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(Object o, Task task) {
        if(status == STATUS.Failure){
            logger.error("not able to create db table,stop processing");
            return;
        }

        Class<?> clazz = o.getClass();
        if(status == STATUS.NotStarted){
            if(createTable(clazz)) {
                status = STATUS.Success;
            }else{
                status = STATUS.Failure;
                logger.error("create db table fails");
                return ;
            }
        }

        String tableName = clazz.getSimpleName();
        String sql = "INSERT INTO `" + tableName + "` (";
        String keys = "`id`";
        String values = "NULL";
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        try {
            for (Field field : fields) {
                keys = keys + ", `" + field.getName() + "`";
                values = values + ", '" + field.get(o) + "'";
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        sql = sql + keys + ") VALUES (" + values + ");";
        logger.info(sql);
        dao.executeUpdate(sql);
    }

    /**
     * create table with the definition of class
     * @param clazz
     * @return
     */
    public boolean createTable(Class<?> clazz) {
        String tableName = clazz.getSimpleName();
        ResetDB resetDB =  clazz.getDeclaredAnnotation(ResetDB.class);
        if(resetDB != null && resetDB.value()){
            String sql = "DROP TABLE IF EXISTS `" + tableName + "`;";
            dao.executeUpdate(sql);
            logger.info("drop table " + tableName + " and re-recrate again.");
        }

        logger.info("creating table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`id` int(11) NOT NULL AUTO_INCREMENT";
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String type = "varchar(1000)";
            if(fieldType.getName().equalsIgnoreCase("int")){
                type = "int(11)";
            }
            sql = sql + ", `" + fieldName + "` " + type + " NULL";
        }
        sql = sql + ", PRIMARY KEY (`id`)) ENGINE=InnoDB;";
        logger.info(sql);
        dao.executeUpdate(sql);

        logger.info("create table " + tableName + " successfully");
        return true;

    }
}
