package us.codecraft.webmagic;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import us.codecraft.webmagic.utils.Experimental;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
    public static final String STATUS_CODE = "statusCode";
    public static final String PROXY = "proxy";

    private String url;

    private String method;

    private int depth = 0;

    private boolean isNextPageRequest = false;

    private String subPageFatherUrl;
    private String subPageName;

    private String fatherUrl;

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    /**
     * store the namevaluepair for post request
     */
    private List<NameValuePair> postData;

    /**
     * store the content extracted from parseurl page
     */
    private Map<String, String> contents;

    /**
     * Priority of the request.<br>
     * The bigger will be processed earlier. <br>
     * @see us.codecraft.webmagic.scheduler.PriorityScheduler
     */
    private long priority;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    public Request(String url, int depth){
        this.url = url;
        this.depth = depth;
    }

    /**
     * depth of the page in parsing list
     * @return
     */
    public int getDepth(){
        return this.depth;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public long getPriority() {
        return priority;
    }

    public boolean isNextPageRequest() {
        return isNextPageRequest;
    }

    public void setNextPageRequest(boolean isNextPageRequest) {
        this.isNextPageRequest = isNextPageRequest;
    }

    public  boolean isSubPage(){
        return subPageFatherUrl != null;
    }

    public void  setSubPageFatherUrl(String fatherUrl,String subpageName){
        this.subPageFatherUrl = fatherUrl;
        this.subPageName = subpageName;
    }

    public String getSubPageName() {
        return subPageName;
    }

    public String getSubPageFatherUrl(){
        return subPageFatherUrl;
    }

    public boolean hasFatherPage() {
        return fatherUrl != null;
    }

    public String getFatherUrl() {
        return fatherUrl;
    }

    public void setFatherUrl(String fatherUrl) {
        this.fatherUrl = fatherUrl;
    }

    /**
     * Set the priority of request for sorting.<br>
     * Need a scheduler supporting priority.<br>
     * @see us.codecraft.webmagic.scheduler.PriorityScheduler
     *
     * @param priority
     * @return this
     */
    @Experimental
    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    public Map<String, String> getContents() {
        return contents;
    }

    public Request putContents(String name, String content){
        if(contents == null){
            contents = new HashMap<>();
        }
        contents.put(name,content);
        return this;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (!url.equals(request.url)) return false;

        return true;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * The http method of the request. Get for default.
     * @return httpMethod
     * @see us.codecraft.webmagic.utils.HttpConstant.Method
     * @since 0.5.0
     */
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * return null if request method is not post
     * @return
     */
    public NameValuePair[] getPostData() {
        if(method == HttpConstant.Method.POST) {
            return (NameValuePair[]) postData.toArray();
        }else{
            return null;
        }
    }

    public void addPostData(String name, String value) {
        NameValuePair nameValuePair = new BasicNameValuePair(name,value);
        this.postData.add(nameValuePair);
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                ", priority=" + priority +
                '}';
    }
}
