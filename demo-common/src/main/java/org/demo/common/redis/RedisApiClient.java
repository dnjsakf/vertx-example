package org.demo.common.redis;

import org.demo.common.redis.pool.JedisConnectionPool;

import redis.clients.jedis.JedisPool;

public class RedisApiClient extends AbstractRedisClient {

    private static JedisPool jedisPoolA;

    public RedisApiClient(String host, int port, String field) throws Exception {
        if (jedisPoolA == null) { 
            try {
                JedisPool pool = JedisConnectionPool.initJedisPool(host, port);
                if(pool != null){
                    jedisPoolA = pool;
                } else {
                    throw new Exception("Redis Connection Error - " + host + ":" + port);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        serverKey = field;
//      pingMaster("rest", 1000, 1000);
    }
}
