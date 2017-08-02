package com.swk.common.redis;

import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisJsonKVCacheSimple<V> extends RedisJsonKVCache<V> implements RedisKVSet<String, V>{

	public RedisJsonKVCacheSimple(ObjectSerializer<String, V> valueSerializer, Class<V> clz, String cacheName,
			int expireSec) {
		super(valueSerializer, clz, cacheName, expireSec);
	}
	
	@Override
	public void set(String key, V v) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String value = valueSerializer.serialize(v);
			setValue(jedis, key, value);
		} catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
		} catch (Throwable th) {
			th.printStackTrace();
		} finally {
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
	}
	
	@Override
	public V get(String key) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String value = jedis.get(key);
			if (!StringUtils.isEmpty(value)) {
				return valueSerializer.deserialize(value, this.clazz);
			}
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			broken = true;
		} catch (Throwable th) {
			th.printStackTrace();
		} finally {
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return null;
	}
	
	private void setValue(Jedis jedis, java.lang.String key, java.lang.String value) {
		Pipeline p = jedis.pipelined();
		p.set(key, value);
		p.expire(key, this.expire);
		p.sync();
	}


}
