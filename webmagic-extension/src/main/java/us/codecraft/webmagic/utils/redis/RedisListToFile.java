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

/**
 * Created by cano on 2015/5/30.
 * 将list从redis中读出来并写入到文件
 */
public class RedisListToFile {

    private String filePath = null;
    private String listName = null;

    private JedisPool pool;
    private Jedis jedis;
    FileOutputStream output;


    public RedisListToFile(String filePath, String listName) {
        this.filePath = filePath;
        this.listName = listName;
    }

    public void run(){
        //get jedis resource
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(10000l);
        pool = new JedisPool(config, "127.0.0.1");

        boolean borrowOrOprSuccess = true;
        try {
            jedis = pool.getResource();
        } catch (JedisConnectionException e) {
            borrowOrOprSuccess = false;
            if (jedis != null)
                pool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                pool.returnResource(jedis);
        }

        //get file resource
        File storeFile = new File(filePath);
        if(storeFile.exists()){
            System.out.println("file exists for " + filePath);
        }
        try {
            output = new FileOutputStream(storeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int i =0;
        try {
            while(true) {
                String json = jedis.lpop(listName);
                if (json == null) {
                    break;
                }

                Gson gson = new Gson();
                Request request = gson.fromJson(json, Request.class);
                String content = request.getUrl() + "\n";
                output.write(content.getBytes());

                i++;
                System.out.println(i);
            }

            pool.returnResource(jedis);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        RedisListToFile redisListToFile = new RedisListToFile("f:/douguocaipuurls", "queue_dupicate_douguo.com/");
        redisListToFile.run();
    }
}
