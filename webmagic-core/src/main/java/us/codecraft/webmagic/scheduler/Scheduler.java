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
     * compare the parse queue copy with parsed set, return the un-parsed request if there is any,other returns null or null list
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
