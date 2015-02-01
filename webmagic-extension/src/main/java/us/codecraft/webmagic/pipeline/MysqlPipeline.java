package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.model.ItemModel;
import us.codecraft.webmagic.model.PageModel;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.BaseDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by canoxu on 2015/1/20.
 */
public class MysqlPipeline implements Pipeline{
    public static final int DBStatusSuccess = 1;
    public static final int DBStatusFailure = 0;
    public static final int DBStatusNotStarted = -1;

    private int status = DBStatusNotStarted;
    private BaseDAO dao = BaseDAO.getInstance();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(status == MysqlPipeline.DBStatusFailure){
            logger.error("not able to create db table,stop processing");
            return;
        }

        PageModel pageModel = resultItems.getPageModel();
        if(pageModel == null){
            status = MysqlPipeline.DBStatusFailure;
            logger.error("page Model is null.");
            return;
        }
        String tableName = pageModel.getModelName();
        if(tableName == null) {
            Spider spider = (Spider) task;
            tableName = spider.getSite().getDomain().replace(".", "");
        }
        if(status == MysqlPipeline.DBStatusNotStarted){
            if(createTable(pageModel,tableName)) {
                status = MysqlPipeline.DBStatusSuccess;
            }else{
                status = MysqlPipeline.DBStatusFailure;
                logger.error("create db table fails");
                return ;
            }
        }

        String sql = "INSERT INTO `" + tableName + "` (";
        String keys = "`id`";
        String values = "NULL";
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            keys = keys + ", `" + entry.getKey() + "`";
            values = values + ", '" + entry.getValue() + "'";
        }
        sql = sql + keys + ") VALUES (" + values + ");";
        logger.info(sql);
        dao.executeUpdate(sql);
    }

    private boolean createTable(PageModel pageModel, String tableName){
        List<ItemModel> itemsModel = pageModel.getItemsModel();
        if(itemsModel == null){
            logger.error("fail to create table " + tableName + ", items model is not set.");
            return false;
        }

        logger.info("creating table " + tableName + " successfully.");
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`id` int(11) NOT NULL AUTO_INCREMENT";
        for(int i=0; i<itemsModel.size(); i++){
            ItemModel itemModel = itemsModel.get(i);
            sql = sql + ", `" + itemModel.getName() + "` " + itemModel.getItemType() + " NULL";
        }
        sql = sql + ", PRIMARY KEY (`id`)) ENGINE=InnoDB;";
        //logger.info(sql);
        dao.executeUpdate(sql);
        logger.info("create table " + tableName + " successfully");
        return true;
    }
}
