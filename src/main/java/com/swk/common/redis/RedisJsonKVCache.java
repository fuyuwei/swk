package com.swk.common.redis;

import org.springframework.util.StringUtils;

import com.swk.util.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisJsonKVCache<V> implements RedisKV<String, V> {
	
	protected ObjectSerializer<String, V> valueSerializer;
	
	protected final Class<V> clazz;
	
	protected final String cacheName;
	
	protected final int expire;
	
	public static final int SUCCESS = 1;
	
	public static final int FAILURE = -1;
		

	public RedisJsonKVCache(ObjectSerializer<String, V> valueSerializer, Class<V> clazz, String cacheName, int expire) {
		super();
		this.valueSerializer = valueSerializer;
		this.clazz = clazz;
		this.cacheName = cacheName;
		this.expire = expire;
	}
	
	private void setValue(Jedis jedis,String key,String value){
		Pipeline pipe = jedis.pipelined();
		pipe.set(key, value);
		pipe.expire(key, this.expire);
		pipe.sync();
	}

	@Override
	public V get(String key) {
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String value = jedis.get(key);
			if(StringUtils.isEmpty(value)){
				V v = onLoad(key);
				if(v != null){
					value = valueSerializer.serialize(v);
					setValue(jedis, key, value);
				}
				return v;
			}else{
				return valueSerializer.deserialize(value, this.clazz);
			}
		}catch(JedisConnectionException e){
			e.printStackTrace();
			broken = true;
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return null;
	}
	
	/**
	 * if value is null,init the value from db,files and so on,
	 * this method must be overloading by subclass
	 * @param key
	 * @return
	 */
	protected V onLoad(String key){
		throw new UnsupportedOperationException();
	}

	@Override
	public int del(String key) {
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			jedis.del(key);
			return SUCCESS;
		}catch(JedisConnectionException e){
			e.printStackTrace();
			broken = true;
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return FAILURE;
	}
	
	public final String getPrefix() {
		return PropertiesUtil.getString("redis."+this.cacheName+".keyprefix", "");
	}

}
