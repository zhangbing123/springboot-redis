package com.zb.redis.redisdemo.helper;

import com.zb.redis.redisdemo.utils.JsonUtils;
import com.zb.redis.redisdemo.utils.SerializeUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: redis操作类
 * @author: zhangbing
 * @create: 2019-03-14 16:35
 **/
@Slf4j
public class RedisManager {


    private JedisCluster jedisCluster;

    public  RedisManager(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    /**
     * 根据key值设置value 如果value存在则覆盖
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key,Object value){
        try {
            String str=jedisCluster.set(key, String.valueOf(value));
            if("OK".equals(str))
                return true;
        }catch (Exception ex){
            log.error("setToRedis:{Key:"+key+",value"+value+"}",ex);
        }
        return false;
    }

    /**string
     * 向redis存入key和value,并释放连接资源
     * 如果key已经存在 则覆盖
     * @param key
     * @param value
     * @return 成功 返回OK 失败返回 0
     */
    public String setSerialize(String key, Object value) {
        return jedisCluster.set(SerializeUtils.serialize(key), SerializeUtils.serialize(JsonUtils.toJson(value)));
    }

    /**
     * 根据key获取相应的值
     * @param key
     * @return
     */
    public Object get(String key){
        String str=null;
        try {
            str=jedisCluster.get(key);
        }catch (Exception ex){
            log.error("getRedis:{Key:"+key+"}",ex);
        }
        return str;
    }

    /**String
     * 通过key获取储存在redis中的value
     * 并释放连接
     * @param key
     * @return 成功返回value 失败返回null
     */
    public <T>T getSerialize(String key,Class<T> requiredType) {
        return JsonUtils.fromJson(SerializeUtils.deserialize(jedisCluster.get(SerializeUtils.serialize(key))),requiredType);
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public Boolean exists(String key){
        try {
            return jedisCluster.exists(key);
        }catch (Exception e){
            log.error("判断key是否存在报错",e);
        }
        return false;
    }

    /**
     * 删除单个key
     * @param key
     * @return
     */
    public Long del(String key){
        Long result = jedisCluster.del(key);
        return result;
    }

    /**
     * 删除单个key
     * @param key
     * @return
     */
    public Long delSerialize(String key){
        Long result = jedisCluster.del(SerializeUtils.serialize(key));
        return result;
    }

    /**
     * 删除多个key
     * @param keys
     * @return
     */
    public Long dels(String... keys){
        Long result = jedisCluster.del(keys);
        return result;
    }

    /**
     * 当存储的字符串是整数时，当前键值递增并返回递增后的值
     * @param key
     * @return
     */
    public Long incr(String key){
        Long result = null;
        try {
            result = jedisCluster.incr(key);
        }catch (Exception e){
            log.error("递增操作失败：",e);
        }
        return result;
    }

    /**
     * 当存储的字符串是整数时，当前键值递增传入的整数并返回递增后的值
     * @param key
     * @param increment 递增的整数
     * @return
     */
    public Long incrBy(String key,long increment){
        Long result = null;
        try {
            result = jedisCluster.incrBy(key,increment);
        }catch (Exception e){
            log.error("定制整数递增操作失败：",e);
        }
        return result;
    }

    /**
     * 当存储的字符串是整数时，当前键值递减并返回递减后的值
     * @param key
     * @return
     */
    public Long decr(String key){
        Long result = null;
        try {
            result = jedisCluster.decr(key);
        }catch (Exception e){
            log.error("递减操作失败：",e);
        }
        return result;
    }

    /**
     * 当存储的字符串是整数时，当前键值递减传入的整数并返回递减后的值
     * @param key
     * @param decrement 递减的整数
     * @return
     */
    public Long decrBy(String key,long decrement){
        Long result = null;
        try {
            result = jedisCluster.decrBy(key,decrement);
        }catch (Exception e){
            log.error("定制整数递减操作失败：",e);
        }
        return result;
    }

    /**hash
     * 通过key给field设置指定的值,如果key不存在,则先创建 ,存在会覆盖原来的值
     * @param key 可以看成是对象名称
     * @param field 字段
     * @param value
     * @return 如果不存在，新建的返回1，存在返回0, 异常返回null
     *
     */
    public Long hset(String key, String field, String value) {
        return jedisCluster.hset(key, field, value);
    }

    /**
     * 通过key 和 field 获取指定的 value
     * @param key
     * @param field
     * @return 没有返回null
     */
    public <T>T  hget(String key, String field,Class<T> requiredType) {
        return JsonUtils.fromJson(jedisCluster.hget(key, field),requiredType);
    }

    /**
     * 通过key同时设置 hash的多个field
     * @param key
     * @param hash
     * @return 返回OK 异常返回null
     */
    public String hmset(String key, Map<String, String> hash) {
        return jedisCluster.hmset(key, hash);
    }

    /**
     * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
     * @param key
     * @param fields 可以使 一个String 也可以是 String数组
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        return jedisCluster.hmget(key, fields);
    }

    /**
     * 通过key获取所有的field和value
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
        return jedisCluster.hgetAll(key);
    }

    /**
     * 查看key是否存在指定的field
     * @param key
     * @return
     */
    public Boolean hexists(String key, String field) {
        return jedisCluster.hexists(key, field);
    }

    /**
     * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在，操作无效
     * @param key
     * @param field
     * @param value
     * @return 不存在新建返回1，存在返回0
     */
    public Long hsetnx(String key, String field, String value) {
        return jedisCluster.hsetnx(key, field, value);
    }

    /**Hash
     * 为哈希表 key 中的域 field 的值加上增量 value
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrBy(String key, String field, long value) {
        return jedisCluster.hincrBy(key, field, value);
    }

    /**
     * 通过key删除field的value
     * @param key
     * @return
     */
    public Long hdel(String key, String field) {
        return jedisCluster.hdel(key,field);
    }

    /**
     * 返回key存储的map对象中的所有key
     * @param key
     * @return
     */
    public Set<String> hkeys(String key) {
        return jedisCluster.hkeys(key);
    }
    /**
     * 返回key存储的map对象中的所有键的values值
     * @param key
     * @return
     */
    public List<String> hvals(String key) {
        return jedisCluster.hvals(key);
    }

    /**
     * 返回key为键中存放的field值的个数
     * @param key
     * @return
     */
    public Long hlen(String key) {
        return jedisCluster.hlen(key);
    }


    /**List
     * 通过key在list头部添加值
     * @param key
     * @param value
     * @return 在 push 操作后的 list 长度。
     */
    public Long lpush(String key, String value) {
        return jedisCluster.lpush(SerializeUtils.deserialize(jedisCluster.get(SerializeUtils.serialize(key))));
    }

    /**List
     * 向存于 key 的列表的尾部插入所有指定的值。如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。
     * 当 key 保存的不是一个列表，那么会返回一个错误。
     * @param key
     * @param value
     * @return  在 push 操作后的列表长度
     */
    public Long rpush(String key, String value) {
        return jedisCluster.rpush(key, value);
    }

    /**
     * 弹出 List左边的第一个元素
     * @param key
     * @return
     */
    public String lpop(String key) {
        return jedisCluster.lpop(key);
    }

    /**
     * 弹出List右边的第一个元素
     * @param key
     * @return
     */
    public String rpop(String key) {
        String result = jedisCluster.rpop(key);
        return result;
    }

    /**List
     * 获取list的长度
     * @param key
     * @return
     */
    public Long llen(String key) {
        return jedisCluster.llen(key);
    }

    /**List
     * 返回存储在 key 的列表里指定范围内的元素
     * @param key
     * @parm start 开始位置
     * @param end 结束位置 -1表示最后一个
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        return jedisCluster.lrange(key, start, end);
    }

    /**
     * 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     */

    public Long lrem(String key, long count, String value) {
        Long result = jedisCluster.lrem(key, count, value);
        return result;
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     * @param key
     * @param index
     * @return
     */
    public String lindex(String key, long index) {
        String result = jedisCluster.lindex(key, index);
        return result;
    }

    /**
     * 设置 index 位置的list元素的值为 value
     * @param key
     * @param index
     * @param value
     * @return
     */
    public String lset(String key, long index, String value) {
        String result = jedisCluster.lset(key, index, value);
        return result;
    }

    /**List
     * 截取(trim)一个已存在的 list，这样 list 就会只包含指定范围的指定元素
     * @param key
     * @parm start 开始位置
     * @param end 结束位置 -1表示最后一个
     * @return
     */

    public String ltrim(String key, long start, long end) {
        return jedisCluster.ltrim(key, start, end);
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略(set不重复)。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * @param key
     * @param member
     * @return
     */
    public Long sadd(String key, String member) {
        Long result = jedisCluster.sadd(key, member);
        return result;
    }

    /**
     * 移除集合 key 中的一个 member 元素，不存在的 member 元素会被忽略。
     * @param key
     * @param member
     * @return
     */
    public Long srem(String key, String member) {
        Long result = jedisCluster.srem(key, member);
        return result;
    }

    /**
     * 返回集合 key 中的所有成员。
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        Set<String> result = jedisCluster.smembers(key);
        return result;
    }

    /**
     * 判断 member 元素是否集合 key 的成员。
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, String member) {
        Boolean result = jedisCluster.sismember(key, member);
        return result;
    }

    /**
     * 返回所有给定集合之间的差集。
     * @param keys
     * @return
     */
    public Set<String> sdiff(String... keys) {
        Set<String> result = jedisCluster.sdiff(keys);
        return result;
    }

    /**
     * 返回给定集合的交集。
     * @param keys
     * @return
     */
    public Set<String> sinter(String... keys) {
        Set<String> result = jedisCluster.sinter(keys);
        return result;
    }

    /**
     * 返回所有给定集合的并集
     * @param keys
     * @return
     */
    public Set<String> sunion(String... keys) {
        Set<String> result = jedisCluster.sunion(keys);
        return result;
    }

    /**
     * 返回集合 key集合中元素的数量。
     * @param key
     * @return
     */
    public Long scard(String key) {
        Long result = jedisCluster.scard(key);
        return result;
    }

    /**
     * 移除并返回集合中的一个随机元素。
     * @param key
     * @return
     */
    public String spop(String key) {
        String result = jedisCluster.spop(key);
        return result;
    }

    /**sorted set
     * 将一个 member 元素及其 score值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，
     *并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(String key, double score, String member) {
        Long result = jedisCluster.zadd(key, score, member);
        return result;
    }

    /**
     * sorted set
     * 移除有序集 key 中的一成员member，不存在的成员将被忽略。
     * @param key
     * @param member
     * @return
     */
    public Long zrem(String key, String member) {
        Long result = jedisCluster.zrem(key, member);
        return result;
    }

    /**sorted set
     * 返回集合 key集合中元素的数量
     * @param key
     * @return
     */
    public Long zcard(String key) {
        Long result = jedisCluster.zcard(key);
        return result;
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zcount(String key, double min, double max) {
        Long result = jedisCluster.zcount(key, min, max);
        return result;
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值。
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key, String member) {
        Double result = jedisCluster.zscore(key, member);
        return result;
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量"score"
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Double zincrby(String key, double score, String member) {
        Double result = jedisCluster.zincrby(key, score, member);
        return result;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。其中成员的位置按 score 值递增(从小到大)来排序。
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrange(String key, int start, int end) {
        Set<String> result = jedisCluster.zrange(key, start, end);
        return result;
    }

    /**
     * 其中成员的位置按 score 值递减(从大到小)来排列。
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, int start, int end) {
        Set<String> result = jedisCluster.zrevrange(key, start, end);
        return result;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。
     * 有序集成员按 score 值递减(从大到小)的次序排列。
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrevrangeByScore(String key, double max, double min,int offset, int count) {
        return jedisCluster.zrevrangeByScore(key, max, min, offset, count);
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {

        return jedisCluster.zrevrangeByScore(key, max, min);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 有序集成员按 score 值递增(从小到大)次序排列。
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zrangeByScore(String key, double min, double max) {
        return jedisCluster.zrangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
     *  排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
     */

    public Long zrank(String key, String member) {
        Long result = jedisCluster.zrank(key, member);
        return result;
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
     * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(String key, String member) {
        Long result = jedisCluster.zrevrank(key, member);
        return result;
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zremrangeByRank(String key, int start, int end) {
        Long result = jedisCluster.zremrangeByRank(key, start, end);
        return result;
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * @param key
     * @param start
     * @param end
     * @return
     */

    public Long zremrangeByScore(String key, double start, double end) {
        Long result = jedisCluster.zremrangeByScore(key, start, end);
        return result;
    }

    /**
     * 计算给定的一个或多个有序集的交集，并将该交集(结果集)储存到 destination 。
     * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之和.
     * @param dstkey
     * @param sets
     * @return
     */
    public Long zinterstore(String dstkey, String... sets) {
        Long result = jedisCluster.zinterstore(dstkey, sets);
        return result;
    }

    /**
     * 计算给定的一个或多个有序集的并集，并将该并集(结果集)储存到 destination 。
     * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
     * @param dstkey
     * @param sets
     * @return
     */
    public Long zunionstore(String dstkey, String... sets) {
        Long result = jedisCluster.zunionstore(dstkey, sets);
        return result;
    }

    /**
     *设置某个key的过期时间(秒),
     * (EXPIRE bruce 1000：设置bruce这个key 1000秒后系统自动删除)
     * 注意：如果在还没有过期的时候，对值进行了改变，那么那个值会被清除。
     * @return
     */
    public Long expire(String key, int seconds) {
        return jedisCluster.expire(key, seconds);
    }

    /**
     * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。
     * 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
     * @return
     */
    public Long expireAt(String key, Long unixTime) {
        return jedisCluster.expireAt(SerializeUtils.serialize(key), unixTime);
    }

}
