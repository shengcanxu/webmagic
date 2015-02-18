package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.formatter.Formatter;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class FormatterPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.isSkip()) return;

        PageModel pageModel = (PageModel)resultItems.getPageModel();
        if(pageModel.getFormatterMap().size() == 0) return;

        for (Map.Entry<String, List<Formatter>> entry : pageModel.getFormatterMap().entrySet()) {
            String name = entry.getKey();
            List<Formatter> formatters = entry.getValue();
            if (formatters != null) {
                for (Formatter formatter : formatters) {
                    Object formated = formatter.format(resultItems.get(name));
                    resultItems.put(name,formated);
                }
            }
        }
    }
}
