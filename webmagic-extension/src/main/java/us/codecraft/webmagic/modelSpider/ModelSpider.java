package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.ModelPageProcessor;
import us.codecraft.webmagic.processor.PageProcessor;

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
    }

    public static ModelSpider create(Site site, PageModel pageModel) {
        return new ModelSpider(site, pageModel);
    }
}
