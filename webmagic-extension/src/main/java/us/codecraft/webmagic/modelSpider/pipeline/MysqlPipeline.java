package us.codecraft.webmagic.modelSpider.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.annotation.ExpandField;
import us.codecraft.webmagic.model.annotation.ResetDB;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.pipeline.PageModelPipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by canoxu on 2015/1/20.
 */
public class MysqlPipeline implements Pipeline {

    public static enum STATUS {Success,Failure,NotStarted}

    private STATUS status = STATUS.NotStarted;
    private BaseDAO dao = BaseDAO.getInstance();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(status == STATUS.Failure){
            logger.error("not able to create db table,stop processing");
            return;
        }

        PageModel pageModel = (PageModel) resultItems.getPageModel();
        Class<?> clazz = pageModel.getClazz();
        if(status == STATUS.NotStarted){
            if(createTable(clazz)) {
                status = STATUS.Success;
            }else{
                status = STATUS.Failure;
                logger.error("create db table fails");
                return ;
            }
        }

//        String separator = "@#$";
//        boolean shouldExpand = false;
//        ExpandField expandField = clazz.getAnnotation(ExpandField.class);
//        if(expandField != null){
//            separator = expandField.seperator();
//            shouldExpand = expandField.shouldExpand();
//        }
//
//        //insert field content to db based on ExpandField setting
//        String tableName = clazz.getSimpleName();
//        Field[] fields = clazz.getDeclaredFields();
//        AccessibleObject.setAccessible(fields, true);
//
//        if(shouldExpand){
//            insertToDbExpand(tableName,fields,o);
//        }else {
//            insertToDbNotExpand(tableName,fields,o,separator);
//        }
    }

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

//        String separator = "@#$";
//        boolean shouldExpand = false;
//        ExpandField expandField = o.getClass().getAnnotation(ExpandField.class);
//        if(expandField != null){
//            separator = expandField.seperator();
//            shouldExpand = expandField.shouldExpand();
//        }
//
//        //insert field content to db based on ExpandField setting
//        String tableName = clazz.getSimpleName();
//        Field[] fields = clazz.getDeclaredFields();
//        AccessibleObject.setAccessible(fields, true);
//
//        if(shouldExpand){
//            insertToDbExpand(tableName,fields,o);
//        }else {
//            insertToDbNotExpand(tableName,fields,o,separator);
//        }
    }

    private void insertToDbExpand(String tableName, Field[] fields, Object o) {
        try {
            int i=0;
            int multiSize = 0;
            do {
                String sql = "INSERT INTO `" + tableName + "` (";
                String keys = "`id`";
                String values = "NULL";
                for (Field field : fields) {
                    keys = keys + ", `" + field.getName() + "`";
                    if (field.getType().equals(List.class)) {
                        List<String> fieldValues = (List<String>) field.get(o);
                        if(fieldValues.size() == 0 || i >= fieldValues.size()){
                            values = values + ", 'NULL'";
                        }else {
                            values = values + ", '" + fieldValues.get(i) + "'";
                        }
                        if(multiSize < fieldValues.size()) multiSize = fieldValues.size();
                    } else {
                        values = values + ", '" + field.get(o) + "'";
                    }
                }
                sql = sql + keys + ") VALUES (" + values + ");";
                logger.info(sql);
                dao.executeUpdate(sql);
                i++;
            }while(i<multiSize);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void insertToDbNotExpand(String tableName, Field[] fields, Object o,String separator) {
        try {
            String sql = "INSERT INTO `" + tableName + "` (";
            String keys = "`id`";
            String values = "NULL";
            for (Field field : fields) {
                keys = keys + ", `" + field.getName() + "`";
                if (field.getType().equals(List.class)) {
                    List<String> fieldValues = (List<String>) field.get(o);
                    String fvs = "";
                    for (String v : fieldValues) {
                        if (fvs.length() == 0) fvs = v;
                        else fvs = fvs + separator + v;
                    }
                    values = values + ", '" + fvs + "'";
                } else {
                    values = values + ", '" + field.get(o) + "'";
                }
            }
            sql = sql + keys + ") VALUES (" + values + ");";
            logger.info(sql);
            dao.executeUpdate(sql);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
