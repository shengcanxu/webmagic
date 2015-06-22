package us.codecraft.webmagic.utils.file;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import us.codecraft.webmagic.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cano on 2015/5/31.
 * 从文件获取连接列表等操作
 */
public class FileUtils {

    /**
     * 从文件中读取内容到list
     * @param filePath
     * @return
     */
    public static List<String> getUrlsFromFile(String filePath){
        List<String> urls = new ArrayList<>();
        try {
            File fin = new File(filePath);
            FileInputStream fis = new FileInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                urls.add(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

    public static void getFromFileToRedis(String filePath,String redisKey,boolean startOver){
        List<String> urls = getUrlsFromFile(filePath);
        System.out.println("get " + urls.size() + " urls");

        //get jedis resource
        JedisPool pool;
        Jedis jedis = null;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");

        boolean borrowOrOprSuccess = true;
        try {
            jedis = pool.getResource();

            if(startOver){
                jedis.del(getSetKey(redisKey));
                jedis.del(getQueueKey(redisKey));
                jedis.del(getDupicateQueueKey(redisKey));
                System.out.println("remove redis queue and start over parsing.");
            }

            //do add to redis
            int i =0;
            for (String url : urls) {
                Request request = new Request(url);
                Gson gson = new Gson();
                String json = gson.toJson(request);
                jedis.sadd(getSetKey(redisKey), url);
                jedis.rpush(getQueueKey(redisKey),json);

                i++;
                System.out.println("No. " + i);
            }

        } catch (JedisConnectionException e) {
            borrowOrOprSuccess = false;
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                pool.returnResource(jedis);
        }
    }

    private static String getSetKey(String redisKey) {
        return "set_" + redisKey;
    }

    private static String getQueueKey(String redisKey) {
        return "queue_" + redisKey;
    }

    private static String getDupicateQueueKey(String redisKey) {
        return "queue_dupicate_" + redisKey;
    }
}
