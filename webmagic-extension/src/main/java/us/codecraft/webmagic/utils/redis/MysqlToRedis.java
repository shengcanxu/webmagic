package us.codecraft.webmagic.utils.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import us.codecraft.webmagic.modelSpider.pipeline.BaseDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by cano on 2015/5/30.
 * 将list从redis中读出来并写入到文件
 */
public class MysqlToRedis {
    private BaseDAO dao = BaseDAO.getInstance("cano");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool pool;
    private Jedis jedis;
    private static MysqlToRedis instance = null;

    private MysqlToRedis(){
        //get jedis resource
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");
    }

    public static MysqlToRedis getInstance(){
        if(instance == null){
            instance = new MysqlToRedis();
        }
        return  instance;
    }

    public void mysqlResultToRedisQueue(String sql,String columnName, String queueName){
        try {
            jedis = pool.getResource();

            List<Map<String,Object>> list = dao.executeQuery(sql,null,null);
            for (Map<String, Object> map : list) {
                String str = (String) map.get(columnName);
                System.out.println(str);
                jedis.rpush(queueName, str);
            }

            pool.returnResource(jedis);
        }catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        }
    }

    public void mysqlResultToRedisSet(String sql,String columnName, String setName){
        try {
            jedis = pool.getResource();

            List<Map<String,Object>> list = dao.executeQuery(sql,null,null);
            for (Map<String, Object> map : list) {
                String str = (String) map.get(columnName);
                System.out.println(str);
                jedis.sadd(setName, str);
            }

            pool.returnResource(jedis);
        }catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args){
        MysqlToRedis mysqlToRedis = MysqlToRedis.getInstance();
        String sql = "SELECT `url` FROM `chufangfenleiurls` ";
        mysqlToRedis.mysqlResultToRedisQueue(sql,"url","ChuFangCaipuUrls");
    }
}
