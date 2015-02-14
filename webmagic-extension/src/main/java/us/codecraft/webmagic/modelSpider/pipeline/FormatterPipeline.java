package us.codecraft.webmagic.modelSpider.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.PageModel;
import us.codecraft.webmagic.modelSpider.extractors.FieldValueExtractor;
import us.codecraft.webmagic.modelSpider.formatter.Formatter;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

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

        for (FieldValueExtractor extractor : pageModel.getFieldExtractors()) {
            String name = extractor.getName();
            List<Formatter> formatters = pageModel.getFormatterMap().get(name);
            if (formatters != null) {
                for (Formatter formatter : formatters) {
                    Object formated = formatter.format(resultItems.get(name));
                    resultItems.put(name,formated);
                }
            }
        }
    }
}
