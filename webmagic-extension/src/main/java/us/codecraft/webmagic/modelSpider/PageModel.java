package us.codecraft.webmagic.modelSpider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.annotation.*;
import us.codecraft.webmagic.modelSpider.extractors.*;
import us.codecraft.webmagic.modelSpider.formatter.Formatter;
import us.codecraft.webmagic.modelSpider.formatter.RemoveTagFormatter;
import us.codecraft.webmagic.modelSpider.formatter.TrimFormatter;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cano on 2015/2/7.
 */
public class PageModel {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Class<?> clazz;
    private String modelName;

    //for getting multiple pages
    private Selector multiPageSelector = null;
    private String multiPageFieldName = null;

    //for download files <Fieldname, DownloadFile>
    private Map<String,DownloadFile> fileDownloadMap = new HashMap<>();

    //for getting sub-pages
    private Map<String,Selector> subpageMap = new HashMap<>();
    private Map<String,PageModel> subpageModelMap = new HashMap<>();

    private List<ParseUrlExtractor> linkExtractors = new ArrayList<>();

    private List<FieldValueExtractor> fieldExtractors = new ArrayList<>();

    private Map<String, List<Formatter>> formatterMap = new HashMap<>();

    private Map<String, String> customFunctionMap = new HashMap<>();

    private boolean shouldExpand = false;

    public static PageModel createModelFromClass(Class<?> clazz){
        PageModel pageModel = new PageModel();
        pageModel.createModel(clazz);
        return pageModel;
    }

    public void createModel(Class<?> clazz){
        this.clazz = clazz;
        this.modelName = clazz.getSimpleName();
        init(clazz);
    }

    public void createModel(){
        this.clazz = this.getClass();
        this.modelName = clazz.getSimpleName();
        init(clazz);
    }

    private void init(Class clazz) {
        initClassExtractors(clazz);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ExtractByExtractor extractor = getAnnotationExtractBy(field);
            ExtractByUrlExtractor extractorTmp = getAnnotationExtractByUrl(field);
            if (extractor != null && extractorTmp != null) {
                throw new IllegalStateException("Only one of 'ExtractBy ExtractByUrl' can be added to a field!");
            } else if (extractor == null && extractorTmp != null) {
                fieldExtractors.add(extractorTmp);
            }else if (extractor != null && extractorTmp == null){
                fieldExtractors.add(extractor);
            }

            //get text formatter
            List<Formatter> formatters = getAnnotationFormatter(field);
            if(formatters.size() != 0){
                formatterMap.put(field.getName(),formatters);
            }

            //get if current field has multiple pages
            getAnnotationMultiplePagesRegion(field);

            //get if current field has file to download
            getAnnotationFileUrls(field);

            //get subpage fields
            getAnnotationSubPage(field);

            //get extractby parseurl extractors
            getAnnotationParseUrlsExtractor(field);

            //get custom functions
            getAnnotationCustomFunction(field);
        }
    }

    private void getAnnotationCustomFunction(Field field){
        CustomFunction customFunction = field.getAnnotation(CustomFunction.class);
        if(customFunction != null){
            customFunctionMap.put(field.getName(), customFunction.name());
        }
    }

    private void getAnnotationParseUrlsExtractor(Field field){
        ExtractByParseUrl parseUrl = field.getAnnotation(ExtractByParseUrl.class);
        if(parseUrl != null){
            if(parseUrl.depth() > linkExtractors.size()){
                logger.error("parserurl extractor depth exceeds the size of linkextractors");
            }else{
                ExtractByParseUrlExtractor parseUrlExtractor = new ExtractByParseUrlExtractor(parseUrl,field);
                linkExtractors.get(parseUrl.depth()-1).addExtractorByParseUrlExtractor(parseUrlExtractor);
            }
        }
    }

    private void getAnnotationSubPage(Field field){
        SubPageField subPageField = field.getAnnotation(SubPageField.class);
        if(subPageField != null){
            Selector selector = new XpathSelector(subPageField.SubPageRegion());
            subpageMap.put(field.getName(), selector);

            Class<?> type = field.getType();
            if(PageModel.class.isAssignableFrom(type)){
                PageModel subpagePageModel = PageModel.createModelFromClass(type);
                subpageModelMap.put(field.getName(), subpagePageModel);
            }
        }
    }

    private void getAnnotationFileUrls(Field field){
        DownloadFile downloadFile = field.getAnnotation(DownloadFile.class);
        if(downloadFile != null){
            String fileName = field.getName();
            fileDownloadMap.put(fileName, downloadFile);
        }
    }

    private void getAnnotationMultiplePagesRegion(Field field){
        MultiplePagesField multiplePagesField = field.getAnnotation(MultiplePagesField.class);
        if(multiplePagesField != null){
            if(List.class.isAssignableFrom(field.getType())){
                logger.error("MultiplePagesRegion can't be assigned to a List field");
                return;
            }

            multiPageSelector = new XpathSelector(multiplePagesField.multiPageRegion());
            multiPageFieldName = field.getName();
        }
    }

    private List<Formatter> getAnnotationFormatter(Field field){
        List<Formatter> formatters = new ArrayList<>();
        TextFormatter textFormatter = field.getAnnotation(TextFormatter.class);
        if(textFormatter != null){
            TextFormatter.Type[] types = textFormatter.types();
            for(TextFormatter.Type type : types){
                Formatter formatter;
                switch (type){
                    case TRIM:
                        formatter = new TrimFormatter();
                        formatters.add(formatter);
                        break;
                    case REMOVETAG:
                        formatter = new RemoveTagFormatter();
                        formatters.add(formatter);
                        break;
                    default:
                        break;
                }
            }
        }
        return formatters;
    }

    private void initClassExtractors(Class clazz) {
        Annotation annotation = clazz.getAnnotation(ExtractBy.class);
        if (annotation != null) {
            ExtractBy extractBy = (ExtractBy) annotation;
            //ExtractByExtractor extractor = new ExtractByExtractor(extractBy);
            //TODO: add class extractBy annotation
            //objectExtractor = new Extractor(new XpathSelector(extractBy.value()), Extractor.Source.Html, extractBy.notNull(), extractBy.multi());
        }

        //add ParseUrl annotation
        Annotation[] annotations = clazz.getAnnotationsByType(ParseUrl.class);
        if(annotations != null && annotations.length > 0){
            for(Annotation a : annotations){
                ParseUrl parseUrl = (ParseUrl) a;
                ParseUrlExtractor extractor = new ParseUrlExtractor(parseUrl,clazz);
                linkExtractors.add(extractor);
            }
        }

        //if should expand field values
        annotation = clazz.getAnnotation(ExpandFieldValues.class);
        if(annotation != null){
            shouldExpand = true;
        }

    }

    private ExtractByExtractor getAnnotationExtractBy(Field field) {
        ExtractByExtractor extractor = null;
        ExtractBy extractBy = field.getAnnotation(ExtractBy.class);
        if (extractBy != null) {
            extractor = new ExtractByExtractor(extractBy,field);
        }
        return extractor;
    }


    private ExtractByUrlExtractor getAnnotationExtractByUrl(Field field) {
        ExtractByUrlExtractor extractor = null;
        ExtractByUrl extractByUrl = field.getAnnotation(ExtractByUrl.class);
        if (extractByUrl != null) {
            extractor = new ExtractByUrlExtractor(extractByUrl,field);
        }
        return extractor;
    }

    public List<ParseUrlExtractor> getLinkExtractors() {
        return linkExtractors;
    }

    public List<FieldValueExtractor> getFieldExtractors() {
        return fieldExtractors;
    }

    public Map<String, List<Formatter>> getFormatterMap() {
        return formatterMap;
    }

    public Selector getMultiPageSelector() {
        return multiPageSelector;
    }

    public String getMultiPageFieldName() {
        return multiPageFieldName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isShouldExpand() {
        return shouldExpand;
    }

    public boolean hasFileToDownload(){
        return fileDownloadMap.size() != 0;
    }

    public boolean hasSubpage(){
        return subpageMap.size() != 0;
    }

    public Map<String, Selector> getSubpageMap() {
        return subpageMap;
    }

    public PageModel getSubpageModel(String name) {
        return subpageModelMap.get(name);
    }

    public Map<String, DownloadFile> getFileDownloadMap() {
        return fileDownloadMap;
    }

    public Map<String, String> getCustomFunctionMap() {
        return customFunctionMap;
    }
}
