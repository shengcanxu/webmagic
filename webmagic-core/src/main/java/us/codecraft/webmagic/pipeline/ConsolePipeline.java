package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.util.Map;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ConsolePipeline implements Pipeline {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("get page: " + resultItems.getRequest().getUrl() + "\n");
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            sb.append(entry.getKey() + ":\t" + entry.getValue() + "\n");
        }
        logger.info(sb.toString());
    }
}
