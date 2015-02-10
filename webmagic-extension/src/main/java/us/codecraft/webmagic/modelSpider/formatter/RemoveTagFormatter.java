package us.codecraft.webmagic.modelSpider.formatter;

import java.util.List;

/**
 * Created by canoxu on 2015/2/10.
 */
public class RemoveTagFormatter implements Formatter{
    @Override
    public List<String> format(List<String> value){
        return value;
    }
}
