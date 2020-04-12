package com.ittttt.provider.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

public class JedisUtils {

    private static JedisPool jedisPool;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("application");
        String host = bundle.getString("host");
        int port = Integer.parseInt(bundle.getString("port"));
        int maxTotal = Integer.parseInt(bundle.getString("maxTotal"));
        int maxIdle = Integer.parseInt(bundle.getString("maxIdle"));

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPool = new JedisPool(jedisPoolConfig,host,port);
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static void close(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }

    public static void close(JedisPool jedisPool){
        if(jedisPool!=null){
            jedisPool.close();
        }
    }
}
