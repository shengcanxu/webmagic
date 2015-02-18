package us.codecraft.webmagic.scheduler;

import com.google.gson.Gson;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by cano on 2015/1/18.
 */
public class StackScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler{
    private LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<String>();

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }

    @Override
    public synchronized Request poll(Task task) {
        Gson gson = new Gson();
        String json = queue.poll();
        return gson.fromJson(json,Request.class);
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        Gson gson = new Gson();
        String json = gson.toJson(request);
        queue.addFirst(json);
    }
}
