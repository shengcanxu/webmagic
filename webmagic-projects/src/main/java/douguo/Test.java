package douguo;

import us.codecraft.webmagic.utils.redis.RedisCopy;

/**
 * Created by cano on 2015/5/31.
 */
public class Test {

    public static void main(String[] args){
//        String html = "<div><a href='https://github.com'>github.com</a></div>" +
//                "<table><tr><td>a</td><td>b</td></tr></table>";
//
//        Document document = Jsoup.parse(html);
//
//        String result = Xsoup.compile("//a/@href").evaluate(document).get();
//        //Assert.assertEquals("https://github.com", result);
//
//        List<String> list = Xsoup.compile("//tr/td/text()").evaluate(document).list();
//       // Assert.assertEquals("a", list.get(0));
//        //Assert.assertEquals("b", list.get(1));
//        int a = 2;

        RedisCopy copy = RedisCopy.getInstance();
        copy.copyQueue("queue_dupicate_ChufangCaipuUrls","queue_ChufangCaipuUrls");

    }
}
