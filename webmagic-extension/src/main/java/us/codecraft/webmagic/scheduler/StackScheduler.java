package us.codecraft.webmagic.scheduler;

import com.google.gson.Gson;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by cano on 2015/1/18.
 */
public class StackScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private Deque<String> queue = new LinkedBlockingDeque<>();

    private Set<String> urlSet = new HashSet<>();

    private Deque<String> queueDuplicate = new LinkedBlockingDeque<>();

    public StackScheduler(){
        setDuplicateRemover(this);
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        boolean duplicate = urlSet.contains(request.getUrl());
        if(!duplicate){
            urlSet.add(request.getUrl());
        }
        return duplicate;
    }

    @Override
    public void resetDuplicateCheck(Task task) {

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
    public List<Request> checkIfCompleteParse(Task task) {
        List<Request> list = new ArrayList<>();
        Gson gson = new Gson();
        Iterator<String> iterator = queueDuplicate.iterator();
        while(iterator.hasNext()){
            String json = iterator.next();
            Request request = gson.fromJson(json, Request.class);
            list.add(request);
        }
        return list;
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        Gson gson = new Gson();
        String json = gson.toJson(request);
        queue.addFirst(json);
        queue.push(json);
    }
}
