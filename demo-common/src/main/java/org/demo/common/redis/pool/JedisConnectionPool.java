package org.demo.common.redis.pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisConnectionPool {

    private static JedisPool jedisPoolA;

    
    public static Jedis getJedisConnection() {
        return getJedisConnection(-1);
    }
    
    public static Jedis getJedisConnection(int db) {  // koushik: removed static 
        try { 
            if (jedisPoolA == null) { 
                jedisPoolA = initJedisPool("127.0.0.1", 7000);
            }
//          System.out.println(jedisPool);
//          System.out.println(jedisPool.getNumActive());
//          System.out.println(jedisPool.getNumIdle());
//          System.out.println(jedisPool.isClosed());
//          System.out.println(jedisPool.getNumWaiters());
//          System.out.println(jedisPool.toString());
            Jedis jedis = jedisPoolA.getResource();
            if( db >=0 ) jedis.select(db);
            return jedis;
            
        } catch (Exception e) {
            if (jedisPoolA == null) {
                System.out.println(jedisPoolA);
                System.out.println(jedisPoolA.getNumActive());
                System.out.println(jedisPoolA.getNumIdle());
                System.out.println(jedisPoolA.isClosed());
                System.out.println(jedisPoolA.getNumWaiters());
                System.out.println(jedisPoolA.toString());
            }
            e.printStackTrace();
            return null;
        } 
    }
   
    public static JedisPool initJedisPool(String host, int port) throws Exception {
        JedisPool r = null;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20); 
        config.setMaxIdle(10); 
        config.setMinIdle(10);
        config.setMaxWaitMillis(30000); 
        System.out.println(host + ":" + port);
        try{
            r = new JedisPool(config, host, port);
            Jedis j = r.getResource();
            j.close();
        }catch(JedisConnectionException e){
            r = null;
            e.printStackTrace();
        }catch(Exception e){
            r = null;
            e.printStackTrace();
        }

        return r;
    }
    
    public static void close(Jedis jedis) {
        if(jedis != null){
            if(jedis != null && jedis.isConnected()){
                jedis.close();
            }
        }
    }
}
