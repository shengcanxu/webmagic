package us.codecraft.webmagic.scheduler;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.modelSpider.ModelSpider;
import us.codecraft.webmagic.modelSpider.pipeline.BaseDAO;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use Redis as url scheduler for distributed crawlers.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    Logger logger = LoggerFactory.getLogger(getClass());

    private JedisPool pool;

    private Site site = null;

    private boolean startOver = false;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String QUEUE_PREFIX_DUPICATE = "queue_dupicate_";

    private static final String SET_PREFIX = "set_";

    private RedisScheduler(String host) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(1000);
        config.setMaxIdle(20);
        config.setMaxWait(100000l);
        this.pool = new JedisPool(config, host,6379,100000);
        setDuplicateRemover(this);
    }

    public RedisScheduler(String host, Site site){
        this(host);
        this.site = site;
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
            if (!isDuplicate || request.isRefresh()) {
                jedis.sadd(getSetKey(task), request.getUrl());
                return false;
            }
            return true;
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
            if(site.isDeepFirst()){
                jedis.lpush(getQueueKey(task), json);
            }else {
                jedis.rpush(getQueueKey(task), json);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try{
            String json = jedis.lpop(getQueueKey(task));
            jedis.rpush(getDupicateQueueKey(task), json);

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

    @Override
    public void completeParse(Request request, Task task) {
        Jedis jedis = pool.getResource();

        try{
            Gson gson = new Gson();

            HttpHost httpHost = request.getProxy();
            int statusCode = request.getStatusCode();
            request.setProxy(null);
            request.setStatusCode(0);
            String json = gson.toJson(request);
            request.setProxy(httpHost);
            request.setStatusCode(statusCode);

            jedis.lrem(getDupicateQueueKey(task),1,json);

        }finally {
            pool.returnResource(jedis);
        }
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + ((ModelSpider) task).getPageModel().getModelName();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + ((ModelSpider) task).getPageModel().getModelName();
    }

    protected String getDupicateQueueKey(Task task) {
        return QUEUE_PREFIX_DUPICATE + ((ModelSpider) task).getPageModel().getModelName();
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
                list.add(request);
            }
            return list;
        }finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void saveQueue(Task task) {
        Jedis jedis = pool.getResource();
        BaseDAO dao = BaseDAO.getInstance();

        //create table
        String tableName;
        if(task instanceof ModelSpider){
            tableName = ((ModelSpider) task).getPageModel().getClazz().getSimpleName() + "UrlSet";
        }else{
            tableName = task.getUUID() + "UrlSet";
        }
        String sql = "DROP TABLE IF EXISTS `" + tableName + "`;";
        dao.executeUpdate(sql);
        logger.info("drop table " + tableName + " and re-recreate again.");
        logger.info("creating table " + tableName);
        sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`id` int(11) NOT NULL AUTO_INCREMENT,`url` varchar(1024) NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB;";
        dao.executeUpdate(sql);

        //store values
        logger.info("storing parsed urls.");
        try{
            Set<String> urlSet = jedis.smembers(getSetKey(task));
            for(String url : urlSet){
                logger.info(url);
                sql = "INSERT INTO `" + tableName + "` (`id`,`url`) VALUES (NULL,'" + url + "')";
                dao.executeUpdate(sql);
            }
        }finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void recoverQueue(Task task) {
        BaseDAO dao = BaseDAO.getInstance();
        Jedis jedis = pool.getResource();

        //get urls
        String tableName;
        if(task instanceof ModelSpider){
            tableName = ((ModelSpider) task).getPageModel().getClazz().getSimpleName() + "UrlSet";
        }else{
            tableName = task.getUUID() + "UrlSet";
        }

        String sql = "select url from " + tableName;
        try {
            List<Map<String,Object>> list = dao.executeQuery(sql,null,null);
            for (Map<String, Object> map : list) {
                String url = (String) map.get("url");
                jedis.sadd(getSetKey(task), url);
            }
        }finally {
            pool.returnResource(jedis);
        }
    }
}
