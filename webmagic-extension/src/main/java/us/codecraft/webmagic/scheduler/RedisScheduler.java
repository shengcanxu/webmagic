package us.codecraft.webmagic.scheduler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Redis as url scheduler for distributed crawlers.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    Logger logger = LoggerFactory.getLogger(getClass());

    private JedisPool pool;

    private boolean depthFirst = false;

    private boolean startOver = false;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String QUEUE_PREFIX_DUPICATE = "queue_dupicate_";

    private static final String SET_PREFIX = "set_";

    public RedisScheduler(String host) {
        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisScheduler(String host, boolean depthFirst){
        this(host);
        this.depthFirst = depthFirst;
    }

    public RedisScheduler(JedisPool pool) {
        this.pool = pool;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.del(getSetKey(task));
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            if(startOver){
                jedis.del(getSetKey(task));
                jedis.del(getQueueKey(task));
                jedis.del(getDupicateQueueKey(task));
                startOver = false;
                logger.info("remove redis queue and start over parsing.");
            }

            boolean isDuplicate = jedis.sismember(getSetKey(task), request.getUrl());
            if (!isDuplicate) {
                jedis.sadd(getSetKey(task), request.getUrl());
            }
            return isDuplicate;
        } finally {
            pool.returnResource(jedis);
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try{
            Gson gson = new Gson();
            String json = gson.toJson(request);
            if(depthFirst){
                jedis.lpush(getQueueKey(task), json);
            }else {
                jedis.rpush(getQueueKey(task), json);
            }
            jedis.rpush(getDupicateQueueKey(task), json);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try{
            String json = jedis.lpop(getQueueKey(task));
            if (json == null){
                return null;
            }
            Gson gson = new Gson();
            Request request = gson.fromJson(json, Request.class);
            return request;
        }finally {
            pool.returnResource(jedis);
        }
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getDupicateQueueKey(Task task) {
        return QUEUE_PREFIX_DUPICATE + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    public RedisScheduler setStartOver(boolean startOver) {
        this.startOver = startOver;
        return this;
    }

    public RedisScheduler setDepthFirst(boolean depthFirst){
        this.depthFirst = depthFirst;
        return this;
    }

    @Override
    public List<Request> checkIfCompleteParse(Task task) {
        Jedis jedis = pool.getResource();
        Gson gson = new Gson();
        List<Request> list = new ArrayList<>();
        try{
            String key = getDupicateQueueKey(task);
            List<String> requests = jedis.lrange((key), 0, jedis.llen(key));
            for(String json : requests){
                Request request = gson.fromJson(json, Request.class);
                boolean exist = jedis.sismember(getSetKey(task), request.getUrl());
                if(!exist){
                    list.add(request);
                }
            }
            return list;
        }finally {
            pool.returnResource(jedis);
        }
    }
}
