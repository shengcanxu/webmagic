package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.List;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Scheduler {

    /**
     * add a url to fetch
     *
     * @param request
     * @param task
     */
    public void push(Request request, Task task);

    /**
     * get an url to crawl
     *
     * @param task the task of spider
     * @return the url to crawl
     */
    public Request poll(Task task);

    /**
     * 标志一个request or task执行完毕
     * @param task
     */
    public void completeParse(Request request, Task task);

    /**
     * 判断是否所有的request都执行完毕
     */
    public List<Request> checkIfCompleteParse(Task task);

    /**
     * save to queue to db
     */
    public void saveQueue(Task task);

    /**
     * recover queue from db
     * @param task
     */
    public void recoverQueue(Task task);

}
