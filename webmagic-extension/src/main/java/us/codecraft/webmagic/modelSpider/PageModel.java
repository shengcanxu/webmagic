package us.codecraft.webmagic.modelSpider;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.modelSpider.annotation.ParseUrl;
import us.codecraft.webmagic.modelSpider.extractors.ExtractByExtractor;
import us.codecraft.webmagic.modelSpider.extractors.ExtractByUrlExtractor;
import us.codecraft.webmagic.modelSpider.extractors.FieldValueExtractor;
import us.codecraft.webmagic.modelSpider.extractors.ParseUrlExtractor;
import us.codecraft.webmagic.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by cano on 2015/2/7.
 */
public class PageModel {

    private Class<?> clazz;
    private String modelName;

    private List<ParseUrlExtractor> linkExtractors = new ArrayList<>();
    private List<FieldValueExtractor> fieldExtractors = new ArrayList<>();

    public void createModel(){
        this.clazz = this.getClass();
        this.modelName = clazz.getSimpleName();
        init(clazz);
    }

    private void init(Class clazz) {
        initClassExtractors(clazz);

        Set<Field> fields = ClassUtils.getFieldsIncludeSuperClass(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            FieldValueExtractor extractor = getAnnotationExtractBy(field);
            FieldValueExtractor extractorTmp = getAnnotationExtractByUrl(field);
            if (extractor != null && extractorTmp != null) {
                throw new IllegalStateException("Only one of 'ExtractBy ExtractByUrl' can be added to a field!");
            } else if (extractor == null && extractorTmp != null) {
                fieldExtractors.add(extractorTmp);
            }else if (extractor != null && extractorTmp == null){
                fieldExtractors.add(extractor);
            }
        }
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

    }

    private FieldValueExtractor getAnnotationExtractBy(Field field) {
        ExtractByExtractor extractor = null;
        ExtractBy extractBy = field.getAnnotation(ExtractBy.class);
        if (extractBy != null) {
            extractor = new ExtractByExtractor(extractBy,field);
        }
        return extractor;
    }


    private FieldValueExtractor getAnnotationExtractByUrl(Field field) {
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

    public Class<?> getClazz() {
        return clazz;
    }

    public String getModelName() {
        return modelName;
    }
}
