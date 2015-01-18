package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by cano on 2015/1/18.
 */
public class StackScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler{
    private LinkedBlockingDeque<Request> queue = new LinkedBlockingDeque<Request>();

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
        return queue.poll();
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        queue.addFirst(request);
    }
}
