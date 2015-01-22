package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

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
            if(createTable(resultItems)) {
                status = MysqlPipeline.DBStatusSuccess;
                logger.info("create db table successfully");
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

    private boolean createTable(ResultItems resultItems){
        return true;
    }
}
