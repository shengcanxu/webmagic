package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.PageModel;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;

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

        if(status == MysqlPipeline.DBStatusNotStarted){
            Spider spider = (Spider)task;
            String tableName = spider.getSite().getDomain().replace(".","");
            if(createTable(resultItems,tableName)) {
                status = MysqlPipeline.DBStatusSuccess;
            }else{
                status = MysqlPipeline.DBStatusFailure;
                logger.error("create db table fails");
                return ;
            }
        }

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + " is :\t" + entry.getValue());
        }
    }

    private boolean createTable(ResultItems resultItems, String tableName){
        logger.info("creating table " + tableName + " successfully.");
        String sql = "DROP TABLE IF EXISTS `" + tableName +"`; CREATE TABLE IF NOT EXISTS `" + tableName + "` (`id` int(11) NOT NULL AUTO_INCREMENT";

        List<Map<String, String>> itemsModel = resultItems.getPageModel().getItemsModel();
        for(int i=0; i<itemsModel.size(); i++){
            Map<String,String> itemModel = itemsModel.get(i);
            sql = sql + ", `" + itemModel.get(PageModel.itemModelName) + "` " + itemModel.get(PageModel.itemModelItemType) + " NULL";
        }
        sql = sql + " PRIMARY KEY (`id`)) ENGINE=InnoDB;";
        logger.info(sql);

        logger.info("create table " + tableName + " successfully");

        logger.info("fail to create table " + tableName);
        return true;
    }
}
