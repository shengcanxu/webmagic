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
    public static int DBStatusSuccess = 1;
    public static int DBStatusFailure = 0;
    public static int DBStatusNotStarted = -1;

    private int status = DBStatusNotStarted;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(status == MysqlPipeline.DBStatusNotStarted){
            boolean succ = createDB();
            if(!succ){
                return;
            }
        }

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }

    private boolean createDB(){
        logger.info("create db successfully");
        return true;
    }
}
