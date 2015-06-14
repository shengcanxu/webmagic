package us.codecraft.webmagic.utils.redis;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import us.codecraft.webmagic.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.String;

/**
 * Created by cano on 2015/5/30.
 * 将list从redis中读出来并写入到文件
 */
public class RedisListToFile {

    private static RedisListToFile instance = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool pool;
    private Jedis jedis;
    FileOutputStream output;

    private RedisListToFile(){
        //get jedis resource
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");
    }

    public static RedisListToFile getInstance(){
        if(instance == null){
            instance = new MysqlToRedis();
        }
        return  instance;
    }

    public void redisListToFile(String filePath, String listName){
        try {
            jedis = pool.getResource();

            //get file resource
            File storeFile = new File(filePath);
            if(storeFile.exists()){
                System.out.println("file exists for " + filePath);
            }
            output = new FileOutputStream(storeFile);

            List<String> list = jedis.lrange((listName), 0, jedis.llen(listName));
            for(int i=0; i<list.size(); i++){
                String json = list.get(i);
                if (json == null) {
                    break;
                }

                Gson gson = new Gson();
                Request request = gson.fromJson(json, Request.class);
                String content = request.getUrl() + "\n";
                output.write(content.getBytes());

                System.out.println(i);
            }


            pool.returnResource(jedis);

        } catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args){
        RedisListToFile redisListToFile = RedisListToFile.getInstance();
        redisListToFile.redisListToFile("f:/douguocaipuurls", "queue_dupicate_douguo.com/");
    }
}
