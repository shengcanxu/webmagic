package us.codecraft.webmagic.utils.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cano on 2015/5/30.
 * 将set从redis中读出来并写入到文件
 */
public class RedisSetToFile {

    private String filePath = null;
    private String listName = null;

    private JedisPool pool;
    private Jedis jedis;
    FileOutputStream output;


    public RedisSetToFile(String filePath, String listName) {
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
                String content = jedis.spop(listName);
                if (content == null) {
                    break;
                }

                content = content + "\n";
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
        RedisSetToFile redisSetToFile = new RedisSetToFile("D:\\software\\redis\\data\\douguocaipuurlset", "set_douguo.com/");
        redisSetToFile.run();
    }
}
