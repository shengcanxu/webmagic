package us.codecraft.webmagic.utils.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;

/**
 * Created by cano on 2015/6/7.
 */
public class RedisCopy {

    private JedisPool pool;
    private Jedis jedis;
    private static RedisCopy instance = null;

    private RedisCopy(){
        //get jedis resource
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");
    }

    public static RedisCopy getInstance(){
        if(instance == null){
            instance = new RedisCopy();
        }
        return  instance;
    }

    public void copyQueue(String fromQueueName, String toQueueName){
        try {
            jedis = pool.getResource();

            List<String> list = jedis.lrange((fromQueueName), 0, jedis.llen(fromQueueName));
            for(int i=0; i<list.size(); i++){
                jedis.rpush(toQueueName,list.get(i));
                System.out.println(i);
            }

            pool.returnResource(jedis);
        } catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        }
    }
}
