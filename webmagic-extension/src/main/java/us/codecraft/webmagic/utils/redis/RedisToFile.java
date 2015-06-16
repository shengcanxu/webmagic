package us.codecraft.webmagic.utils.redis;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Set;

/**
 * Created by cano on 2015/5/30.
 * 将list从redis中读出来并写入到文件
 */
public class RedisToFile {

    private static RedisToFile instance = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool pool;
    private Jedis jedis;
    FileOutputStream output;

    private RedisToFile(){
        //get jedis resource
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");
    }

    public static RedisToFile getInstance(){
        if(instance == null){
            instance = new RedisToFile();
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

                System.out.println(i+1);
            }


            pool.returnResource(jedis);

        } catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void redisSetToFile(String filePath, String setName){
        try {
            jedis = pool.getResource();

            //get file resource
            File storeFile = new File(filePath);
            if(storeFile.exists()){
                System.out.println("file exists for " + filePath);
            }
            output = new FileOutputStream(storeFile);

            Set<String> urlset = jedis.smembers(setName);
            String[] urls = new String[urlset.size()];
            urlset.toArray(urls);
            for(int i=0; i<urls.length; i++){
                String content = urls[i] + "\n";
                output.write(content.getBytes());

                System.out.println(i+1);
            }

            pool.returnResource(jedis);

        } catch (JedisConnectionException e) {
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        RedisToFile redisListToFile = RedisToFile.getInstance();
        //redisListToFile.redisListToFile("D:\\software\\redis\\data\\DouguoshipuContentList.txt", "queue_dupicate_DouguoshipuContent");

        redisListToFile.redisSetToFile("D:\\software\\redis\\data\\DouguoshipuContentList.txt", "set_DouguoshipuContent");

    }
}
