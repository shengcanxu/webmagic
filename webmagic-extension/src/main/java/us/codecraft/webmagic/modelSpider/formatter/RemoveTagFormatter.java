package us.codecraft.webmagic.modelSpider.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canoxu on 2015/2/10.
 */
public class RemoveTagFormatter implements Formatter{

    @Override
    public Object format(Object value) {
        if(value == null){
            return null;
        }else if(value instanceof List){
            List<String> v = (List<String>) value;
            List<String> formatted = new ArrayList<>();
            for (String str : v){
                String newStr = str.replaceAll("<.*>","");
                formatted.add(newStr);
            }
            return formatted;
        }else{
            String s = (String) value;
            return s.replaceAll("<[^>]*>", "");
        }
    }
}