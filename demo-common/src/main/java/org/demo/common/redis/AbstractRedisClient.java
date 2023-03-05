package org.demo.common.redis;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.demo.common.Launcher;
import org.demo.common.redis.pool.JedisConnectionPool;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class AbstractRedisClient {

    protected static String serverKey;

    public static String getServerKey() {
        return serverKey;
    }

    public static void setServerKey(String field) {
        serverKey = field;
    }

    protected static void pingMaster(String type, int delay, int interval) {
        Timer timer = new Timer("timer-ping-" + serverKey);
//      pingDaemonServer(type);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
//              pingDaemonServer(type);
            }
        }, delay, interval);
    }

    protected static void pingDaemonServer(String type) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection();
            jedis.select(10);
            JsonObject status = new JsonObject();
            status.put("status", true);
            status.put("type", type);
            status.put("host", Launcher.getHost());
            status.put("time", System.currentTimeMillis());
            jedis.hset(RedisKeyStore.SERVICE.getMaster(), serverKey, status.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static boolean daemonManageFunction(String resStr, Vertx vertx) {
        // TODO Auto-generated method stub
        return true;
    }

    public static boolean clientManageFunction(String resStr, Vertx vertx) {
        // TODO Auto-generated method stub
        return true;
    }

    protected static List<String> BRPOP(int timeout, String field) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection();
            return BRPOP(timeout, field, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static List<String> BRPOP(int timeout, String field, Jedis jedis) throws Exception {
        List<String> list = jedis.brpop(timeout, field);
        return list;
    }

    protected static String RPOP(String field) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection();
            return RPOP(field, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static String RPOP(String field, Jedis jedis) throws Exception {
        String str = jedis.rpop(field);
        return str;
    }

    public static void LPUSH(int db, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            LPUSH(field, value, jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static void LPUSH(String field, String message, Jedis jedis) {
        jedis.lpush(field, message);
    }
    
    public static void RPUSH(int db, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            RPUSH(field, value, jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static void RPUSH(String field, String message, Jedis jedis) {
        jedis.rpush(field, message);
    }
    
    public static void LSET(int db, String field, long index, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            LSET(field, index, value, jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static void LSET(String field, long index, String message, Jedis jedis) {
        jedis.lset(field, index, message);
    }
    
    public static void HSET(int db, String kValue, String field, String message) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            HSET(kValue, field, message, jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static void HSET(String kValue, String field, String message, Jedis jedis) throws Exception {
        jedis.hset(kValue, field, message);
    }

    protected static String SESSION(String field) throws Exception {
        return "";
    }

    public static String HGET(int db, String kValue, String field) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return HGET(jedis, kValue, field);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static Map<String, String> HGETALL(int db, String field) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return HGETALL(jedis, field);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static Map<String, String> HGETALL(Jedis jedis, String field) throws Exception {
        return jedis.hgetAll(field);
    }

    protected static String HGET(Jedis jedis, String kValue, String field) throws Exception {
        String r = jedis.hget(kValue, field);
        return r;
    }

    protected static List<String> HMGET(Jedis jedis, String kValue, String[] field) throws Exception {
        List<String> r = jedis.hmget(kValue, field);
        return r;
    }

    protected static String HGETDEL(String kValue, String field) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection();
            return HGETDEL(kValue, field, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static String HGETDEL(String kValue, String field, Jedis jedis) throws Exception {
        String r = jedis.hget(kValue, field);
        jedis.hdel(kValue, field);
        return r;
    }

    public static void HDEL(int db, String kValue, String field) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.hdel(kValue, field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static String getPID() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    public AbstractRedisClient() {
        super();
    }

    /**
     * <pre>
     * </pre>
     * 
     * @Method Name : authLoginTemp
     * @date : 2017. 6. 13.
     * @author : goodrhys
     * @history : 인증 시작 ( rest api )
     *          -----------------------------------------------------------------------
     *          변경일 작성자 변경내용 ----------- -------------------
     *          --------------------------------------- 2017. 6. 13. goodrhys 최초
     *          작성
     *          -----------------------------------------------------------------------
     * 
     * @param session
     * @return
     */
    public static String authLoginTemp(JsonObject session) {
        String uuid = null;
        Jedis jedis = null;
        try {
            String empNo = session.getString("empNo");
            uuid = UUID.randomUUID().toString();
            jedis = JedisConnectionPool.getJedisConnection(1);
            jedis.hset(RedisKeyStore.SESSION + ":TEMP", uuid, session.toString());
            
//          deleteLogoutSession(jedis, empNo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
        return uuid;
    }

    private static void deleteLogoutSession(Jedis jedis, String empNo) {
        /*
         * delete logouted session data
         */
        Map<String, String> sessions = jedis
                .hgetAll(RedisKeyStore.SESSION.getMaster() + ":" + empNo);
        List<String> keyList = new ArrayList<String>();
        for( String key : sessions.keySet()){
            JsonObject sObj = new JsonObject(sessions.get(key));
            if (sObj.getInteger("isLogin", 1) == 0) {
                jedis.hdel(RedisKeyStore.SESSION.getMaster() + ":" + empNo, key);
                jedis.zrem(RedisKeyStore.SESSION + ":LOGOUT", key);
                keyList.add(key);
            }
        }

        if (keyList.size() > 0) {
            jedis.select(2);
            for (String key : keyList) {
                jedis.del(key);
            }
        }
    }

    /**
     * <pre>
     * </pre>
     * 
     * @Method Name : authLogin
     * @date : 2017. 6. 13.
     * @author : goodrhys
     * @history : 세션 생성 ( sockjs )
     *          -----------------------------------------------------------------------
     *          변경일 작성자 변경내용 ----------- -------------------
     *          --------------------------------------- 2017. 6. 13. goodrhys 최초
     *          작성
     *          -----------------------------------------------------------------------
     * 
     * @param uuid
     * @return
     */
    public static String authLogin(String uuid, String UUID_) {
        String uuidN = null;
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(1);

            String tempSession = jedis.hget(RedisKeyStore.SESSION + ":TEMP", uuid);
            if (tempSession != null) {
                JsonObject session = new JsonObject(tempSession);
                String emp_no = session.getString("empNo");
                uuidN = UUID_;
                session.put("uuid", uuidN);
                session.put("serverKey", serverKey);
                session.put("isLogin", 1);
                session.put("authTime", System.currentTimeMillis());
                jedis.hset(RedisKeyStore.SESSION.getMaster() + ":" + emp_no, UUID_, session.toString());

//              deleteLogoutSession(jedis, emp_no);
                
                jedis.select(2);
                jedis.set(uuidN, session.toString());
                

            }
            jedis.select(1);
            jedis.hdel(RedisKeyStore.SESSION + ":TEMP", uuid);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
        return uuidN;
    }

    /**
     * <pre>
     * </pre>
     * 
     * @Method Name : authLoginRefresh
     * @date : 2017. 9. 7.
     * @author : goodrhys
     * @history :
     *          -----------------------------------------------------------------------
     *          변경일 작성자 변경내용 ----------- -------------------
     *          --------------------------------------- 2017. 9. 7. goodrhys 최초
     *          작성
     *          -----------------------------------------------------------------------
     * 
     * @param uuid
     * @param UUID_
     * @return
     */
    public static String authLoginRefresh(String uuid, String UUID_) {
        String uuidN = null;
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(2);

            String tempSession = jedis.get(uuid);
            if (tempSession != null) {
                JsonObject session = new JsonObject(tempSession);
                String emp_no = session.getString("empNo");

                jedis.select(1);
                String beforeSession = jedis.hget(RedisKeyStore.SESSION.getMaster() + ":" + emp_no, uuid);
                if (beforeSession != null) {
                    uuidN = UUID_;
                    session.put("uuid", uuidN);
                    session.put("serverKey", serverKey);
                    session.put("isLogin", 1);
                    session.put("authTime", System.currentTimeMillis());
                    jedis.hset(RedisKeyStore.SESSION.getMaster() + ":" + emp_no, UUID_, session.toString());

//                  deleteLogoutSession(jedis, emp_no);

                    jedis.select(2);
                    jedis.set(uuidN, session.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
        return uuidN;
    }

    /**
     * <pre>
     * </pre>
     * 
     * @Method Name : getAuthSession
     * @date : 2017. 6. 13.
     * @author : goodrhys
     * @history : 세션 조회
     *          -----------------------------------------------------------------------
     *          변경일 작성자 변경내용 ----------- -------------------
     *          --------------------------------------- 2017. 6. 13. goodrhys 최초
     *          작성
     *          -----------------------------------------------------------------------
     * 
     * @param uuid
     * @return
     */
    public static String getAuthSession(String uuid) {
        String session = null;
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(2);
            session = jedis.get(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
        return session;
    }

    public static void PUBLISH_MSG(JsonObject msg) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection();
            jedis.lpush(RedisKeyStore.MSG_PUB.getMaster(), msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void ZADD(int db, String field, long score, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            ZADD(field, score, value, jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void ZREM(int db, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.zrem(field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void LREM(int db, String field, long cnt, String value) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.lrem(field, cnt, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static void ZADD(String field, long score, String message, Jedis jedis) {
        jedis.zadd(field, score, message);
    }

    public static long ZCOUNT(int db, String field) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return jedis.zcount(field, "-inf", "+inf");
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static Set<String> ZREVRANGE(int db, String field, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return ZREVRANGE(field, start, end, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static Set<String> ZREVRANGE(String field, long start, long end, Jedis jedis) {
        return jedis.zrevrange(field, start, end);
    }

    public static Set<String> ZRANGE(int db, String field, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return ZRANGE(field, start, end, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    protected static Set<String> ZRANGE(String field, long start, long end, Jedis jedis) {
        return jedis.zrange(field, start, end);
    }
    
    public static Set<String> ZRANGEBYSCORE(int db, String field, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return ZRANGEBYSCORE(field, min, max, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }
    
    protected static Set<String> ZRANGEBYSCORE(String field, String min, String max, Jedis jedis) {
        return jedis.zrangeByScore(field, min, max);
    }
    
    public static Set<Tuple> ZRANGEBYSCOREWITHSCORES(int db, String field, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return ZRANGEBYSCOREWITHSCORES(field, min, max, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }
    
    protected static Set<Tuple> ZRANGEBYSCOREWITHSCORES(String field, String min, String max, Jedis jedis) {
        return jedis.zrangeByScoreWithScores(field, min, max);
    }
    
    public static Set<String> LRANGE(int db, String field, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return ZRANGEBYSCORE(field, min, max, jedis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }
    
    protected static List<String> ZRANGEBYSCORE(String field, long start, long end, Jedis jedis) {
        return jedis.lrange(field, start, end);
    }

    public static void DEL(int db, String field) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static String ZSCORE(int db, String field, String member) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return String.valueOf(jedis.zscore(field, member));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void ZINCRBY(int db, String field, long score, String member) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.zincrby(field, score, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static Set<String> ZREVRANGEBYSCORE(int db, String field, int limit) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            return jedis.zrevrangeByScore(field, "+inf", "-inf", 0, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void LDEL(int db, String field) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.del(field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static void setKeyAndValue(String field, String value, int db) {
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            jedis.set(field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }
    }

    public static String getKeyAndValue(String field, int db) {
        String articleInfo = null;
        Jedis jedis = null;
        try {
            jedis = JedisConnectionPool.getJedisConnection(db);
            articleInfo = jedis.get(field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                JedisConnectionPool.close(jedis);
        }

        return articleInfo;
    }
}
