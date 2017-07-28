package com.swk.common.redis;

import java.util.List;

import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * the numerical operation of redis
 * @author fuyuwei
 */
public class RedisNumKVBatch {

	private final String cacheName;
	
	private final int expireSec;
	
	private final String keyPrefix;
	
	public RedisNumKVBatch(String cacheName,int expireSec,String keyPrefix){
		this.cacheName = cacheName;
		this.expireSec = expireSec;
		this.keyPrefix = keyPrefix;
	}
	
	/**
	 * get numerical value by key
	 * @param key
	 * @param def
	 * @return
	 */
	public long get(String key,long def){
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(cacheName);
			String value = jedis.get(decorateKey(key));
			if(StringUtils.isEmpty(value)){
				return def;
			}
			return value == null ? def : Long.parseLong(value);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			broken = true;
		} catch(Throwable th){
			th.printStackTrace();
		} finally{
			RedisConfiguration.returnInstance(key, jedis, broken);
		}
		return def;
	}
	
	public long change(String key,long changeNum,long def){
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(cacheName);
			// this way it is possiable to send multiple commands to the server without waiting for
			// the replies at all,and finally read the replies in single step.
			Pipeline p = jedis.pipelined();
			// the change range is changeNum
			p.incrBy(decorateKey(key), changeNum);
			p.expire(decorateKey(key), this.expireSec);
			List<Object> list = p.syncAndReturnAll();
			if(list.size() > 0){
				return list.get(0) == null ? def : Long.parseLong(list.get(0)+"");
			}
			
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			broken = true;
		}finally{
			RedisConfiguration.returnInstance(key, jedis, broken);
		}
		return def;
	}
	
	private String decorateKey(String key){
		return keyPrefix+key;
	}
	
	public String getCacheName() {
		return cacheName;
	}

	public int getExpireSec() {
		return expireSec;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	
}
