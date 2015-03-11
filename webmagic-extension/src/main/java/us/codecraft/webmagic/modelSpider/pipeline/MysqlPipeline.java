package us.codecraft.webmagic.modelSpider.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.annotation.DownloadContentImage;
import us.codecraft.webmagic.modelSpider.annotation.DownloadFile;
import us.codecraft.webmagic.modelSpider.annotation.FieldType;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by canoxu on 2015/1/20.
 */
public class MysqlPipeline implements Pipeline {

    public static enum STATUS {Success,Failure,NotStarted}

    private STATUS status = STATUS.NotStarted;
    private BaseDAO dao = BaseDAO.getInstance();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean shouldResetDb = false;
    private String separator = "@#$";

    public MysqlPipeline(){}

    public MysqlPipeline setSeparator(String separator){
        this.separator = separator;
        return this;
    }

    /**
     *
     * @param shouldResetDb true to drop table and recreate again, false to use the existing table if exists
     * @return
     */
    public MysqlPipeline setShouldResetDb(boolean shouldResetDb) {
        this.shouldResetDb = shouldResetDb;
        return this;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        if(status == STATUS.Failure){
            logger.error("not able to create db table,stop processing");
            return;
        }

        PageModel pageModel = (PageModel) resultItems.getPageModel();
        if(status == STATUS.NotStarted){
            if(createTable(pageModel)) {
                status = STATUS.Success;
            }else{
                status = STATUS.Failure;
                logger.error("create db table fails");
                return ;
            }
        }

        //insert field content to db based on shouldExpandFields setting
        String tableName = pageModel.getModelName();
        insertToDb(tableName, resultItems);
    }

    private void insertToDb(String tableName, ResultItems resultItems) {
        String sql = "INSERT INTO `" + tableName + "` (";
        String keys = "`id`";
        String values = "NULL";


        for (Map.Entry<String,Object> entry: resultItems.getAll().entrySet()) {
            keys = keys + ", `" + entry.getKey() + "`";
            Object value = entry.getValue();
            if (value instanceof List) {
                List<String> fieldValues = (List<String>) value;
                String fvs = "";
                for (String v : fieldValues) {
                    if (fvs.length() == 0) fvs = v;
                    else fvs = fvs + separator + v;
                }
                values = values + ", '" + fvs + "'";
            } else {
                String fieldValue = (String) value;
                values = values + ", '" + fieldValue + "'";
            }
        }
        sql = sql + keys + ") VALUES (" + values + ");";
        logger.info(sql);
        dao.executeUpdate(sql);
    }

    /**
     * create table with the definition of class
     * @param pageModel
     * @return
     */
    private boolean createTable(PageModel pageModel) {
        Class<?> clazz = pageModel.getClass();
        String tableName = clazz.getSimpleName();
        if(this.shouldResetDb){
            String sql = "DROP TABLE IF EXISTS `" + tableName + "`;";
            dao.executeUpdate(sql);
            logger.info("drop table " + tableName + " and re-recreate again.");
        }

        logger.info("creating table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`id` int(11) NOT NULL AUTO_INCREMENT";
        Map<String,String> map = getTableFieldsFromPageModel(clazz);
        for (Map.Entry<String,String> entry : map.entrySet()) {
            sql = sql + ", `" + entry.getKey() + "` " + entry.getValue() + " NULL";
        }
        sql = sql + ", PRIMARY KEY (`id`)) ENGINE=InnoDB;";
        logger.info(sql);
        dao.executeUpdate(sql);

        logger.info("create table " + tableName + " successfully");
        return true;
    }

    /**
     * create the <tablefieldname, tablefieldtype> from pagemodel's class
     */
    private Map<String,String> getTableFieldsFromPageModel(Class<?> clazz){
        Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for(Field field : fields){
            if(field.getName().startsWith("this")) { //this is field for inner-Class, so need to ignore it
                continue;
            }else if(PageModel.class.isAssignableFrom(field.getType())){
                Map<String, String> subpageMap = getTableFieldsFromPageModel(field.getType());
                map.putAll(subpageMap);
            }else {
                if (field.getAnnotation(DownloadFile.class) != null) {
                    map.put(field.getName() + "File", "varchar(1000)");
                }
                if (field.getAnnotation(DownloadContentImage.class) != null){
                    map.put(field.getName() + "Image", "text");
                }
                map.put(field.getName(), getAnnotationFieldType(field));
            }
        }
        return map;
    }

    private String getAnnotationFieldType(Field field){
        FieldType fieldType = field.getAnnotation(FieldType.class);
        if(fieldType != null){
            String type;
            switch (fieldType.type()){
                case INT:
                    type = "int(11)";
                    break;
                case STRING:
                    type = "varchar(1000)";
                    break;
                case TEXT:
                    type = "text";
                    break;
                case DATETIME:
                    type = "datetime";
                    break;
                default:
                    type = "varchar(1000)";
                    break;
            }
            return type;
        }else{
            return "varchar(1000)";
        }
    }
}
