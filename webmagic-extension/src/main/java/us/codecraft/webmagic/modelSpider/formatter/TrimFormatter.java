package us.codecraft.webmagic.modelSpider.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canoxu on 2015/2/10.
 */
public class TrimFormatter implements Formatter {
    @Override
    public List<String> format(List<String> value) {
        if(value == null){
            return null;
        }

        List<String> formatted = new ArrayList<>();
        for (String str : value){
            formatted.add(str.trim());
        }
        return formatted;
    }
}
