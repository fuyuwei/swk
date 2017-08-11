package com.swk.common.ratelimit;

import java.util.Arrays;

import org.springframework.util.StringUtils;

import com.swk.common.redis.RedisConfiguration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 限流方案一：redis+lua，可用于针对某些方法访问限流
 * @author fuyuwei
 */
public class RateLimiter {
	
	public static final long DEFAULT = 1l;

	private String cacheName;
	
	private String keyPrefix;

	public RateLimiter(String cacheName, String keyPrefix) {
		super();
		this.cacheName = cacheName;
		this.keyPrefix = keyPrefix;
	}
	
	private String getKey(String key){
		return keyPrefix + key;
	}
	
	/**
	 * 对key访问递增计数
	 * @param key
	 * @param def
	 * @return
	 */
	public long incrKey(String key,long def){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			long keyCount = jedis.incr(getKey(key));
			return keyCount;
		}catch(JedisConnectionException e){
			broken = true;
			e.printStackTrace();
		}catch(Throwable th){
			th.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(cacheName, jedis, broken);
		}
		return def;
	}
	
	/**
	 * 完成访问key递减
	 * @param key
	 * @param def
	 */
	public long decrKey(String key,long def){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			long keyCount = jedis.decr(getKey(key));
			return keyCount;
		}catch(JedisConnectionException e){
			broken = true;
			e.printStackTrace();
		}catch(Throwable th){
			th.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(cacheName, jedis, broken);
		}
		return def;
	}
	
	/**
	 * 获取key数量
	 * @param key
	 * @param def
	 * @return
	 */
	public long getKeyCount(String key,long def){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String keyCount = jedis.get(getKey(key));
			return keyCount==null?Long.parseLong(keyCount):def;
		}catch(JedisConnectionException e){
			broken = true;
			e.printStackTrace();
		}catch(Throwable th){
			th.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return def;
	}
	
	/**
	 * 判断是否超出指定访问频次
	 * @param key
	 * @param limit 指定访问频次
	 * @return
	 */
	public boolean isOverLimit(String key,long limit){
		long currentCount = incrKey(key, DEFAULT);
		return currentCount >= limit;
	}
	
	/**
	 * 使用lua对key进行周期内访问计数
	 * @param key 限制key
	 * @param limit 限制次数
	 * @param period 周期
	 * @return true:过载，false：未过载
	 */
	public boolean acquire(String key,long limit,long period){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			if(!StringUtils.isEmpty(limit) && !StringUtils.isEmpty(period)){
				// redis执行lua，返回0过载
				StringBuffer luaScript = new StringBuffer();
				luaScript.append("local key=KEYS[1] ");
				luaScript.append("local limit=tonumber(ARGV[1]) ");
				luaScript.append("local expire=tonumber(ARGV[2]) ");
				luaScript.append("local current=tonumber(redis.call(\"INCRBY\",key,\"1\")) ");
				luaScript.append("if current > limit then return 0 ");
				luaScript.append("elseif current==1 then redis.call(\"expire\",key,expire) end ");
				luaScript.append("return 1");
				System.out.println(Arrays.asList(getKey(key)));
				System.out.println(Arrays.asList(limit+"",period+""));
				return (Long)jedis.eval(luaScript.toString(),Arrays.asList(getKey(key)), Arrays.asList(limit+"",period+"")) == 0;
			}
		}catch(JedisConnectionException e){
			broken = true;
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		RateLimiter rateLimiter = new RateLimiter("sunwukong", "swk");
		boolean b = rateLimiter.acquire("20170809", 2, 30);
		System.out.println(b);
	}
	
}
