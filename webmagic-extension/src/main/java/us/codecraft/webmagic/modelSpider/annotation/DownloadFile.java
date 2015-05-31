package us.codecraft.webmagic.modelSpider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by canoxu on 2015/2/10.
 * 下载field对应的文件，用在field上
 */

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DownloadFile {

    String savepath();

    /**
     * 下载的文件的类型
     */
    public static enum Type {PICTURE,FILE}

    Type type() default Type.FILE;

    /**
     * 是不是将不同的页面的文件/图片放置在不同的目录上。 false就是将所有的文件都放到同一个目录
     * @return
     */
    boolean inSeperateFolder() default false;

}
