package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.*;
import us.codecraft.webmagic.model.ModelPageProcessor;
import us.codecraft.webmagic.modelSpider.pipeline.FileDownloadPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.FormatterPipeline;
import us.codecraft.webmagic.modelSpider.pipeline.MultiplePagesPipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The spider for page model extractor.<br>
 * In webmagic, we call a POJO containing extract result as "page model". <br>
 * You can customize a crawler by write a page model with annotations. <br>
 * Such as:
 * <pre>
 * {@literal @}TargetUrl("http://my.oschina.net/flashsword/blog/\\d+")
 *  public class OschinaBlog{
 *
 *      {@literal @}ExtractBy("//title")
 *      private String title;
 *
 *      {@literal @}ExtractBy(value = "div.BlogContent",type = ExtractBy.Type.Css)
 *      private String content;
 *
 *      {@literal @}ExtractBy(value = "//div[@class='BlogTags']/a/text()", multi = true)
 *      private List<String> tags;
 * }
 * </pre>
 * And start the spider by:
 * <pre>
 *   OOSpider.create(Site.me().addStartUrl("http://my.oschina.net/flashsword/blog")
 *        ,new JsonFilePageModelPipeline(), OschinaBlog.class).run();
 * }
 * </pre>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ModelSpider<T> extends Spider {

    private ModelPageProcessor modelPageProcessor;

    private PageModel pageModel = new PageModel();

    private ModelSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    /**
     * create a spider
     *
     * @param site
     * @param pageModel
     */
    public ModelSpider(Site site, PageModel pageModel) {
        this(ModelSpiderProcessor.create(site, pageModel));
        this.pageModel = pageModel;
        this.addPipeline(new MultiplePagesPipeline());
        this.addPipeline(new FormatterPipeline());
        this.addPipeline(new FileDownloadPipeline());
    }

    public static ModelSpider create(Site site, PageModel pageModel) {
        return new ModelSpider(site, pageModel);
    }

    @Override
    protected void processRequest(Request request) {
        Page page = downloader.download(request, this);
        if (page == null) {
            sleep(site.getSleepTime());
            onError(request);
            return;
        }
        page.setDepth(request.getDepth());  //add the level

        // for cycle retry
        if (page.isNeedCycleRetry()) {
            extractAndAddRequests(page, true);
            sleep(site.getSleepTime());
            return;
        }
        pageProcessor.process(page);

        if (!page.getResultItems().isSkip()) {
            ResultItems resultItems = page.getResultItems();
            if(pageModel.isShouldExpand()){
                List<ResultItems> resultItemsesList = expandResultItems(resultItems);
                for(ResultItems r : resultItemsesList){
                    for(Pipeline pipeline : pipelines){
                        pipeline.process(r,this);
                    }
                }
            }else {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(resultItems, this);
                }
            }
        }

        extractAndAddRequests(page, spawnUrl);

        //for proxy status management
        request.putExtra(Request.STATUS_CODE, page.getStatusCode());
        sleep(site.getSleepTime());
    }

    private List<ResultItems> expandResultItems(ResultItems resultItems){
        List<ResultItems> resultItemsesList = new ArrayList<>();
        int i=0;
        int multiSize = 0;
        do {
            ResultItems newResultItems = resultItems.cloneValue();
            for (Map.Entry<String,Object> entry: resultItems.getAll().entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    List fieldValues = (List) value;
                    if(fieldValues.size() > 0 && i < fieldValues.size()){
                        newResultItems.put(name,fieldValues.get(i));
                    }
                    if(multiSize < fieldValues.size()) multiSize = fieldValues.size();
                } else {
                    newResultItems.put(name,value);
                }
            }
            resultItemsesList.add(newResultItems);
            i++;
        }while(i<multiSize);
        return resultItemsesList;
    }
}
