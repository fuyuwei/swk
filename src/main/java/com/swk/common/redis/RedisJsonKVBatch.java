package com.swk.common.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.swk.util.JsonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisJsonKVBatch<V> extends RedisJsonKVCache<V> implements RedisKVBatch<String, V> {

	public RedisJsonKVBatch(ObjectSerializer<String, V> valueSerializer, Class<V> clazz, String cacheName, int expire) {
		super(valueSerializer, clazz, cacheName, expire);
	}
	
	private static String[] getKeys(List<String> keys){
		String[] strs = new String[keys.size()];
		return keys.toArray(strs);
	}
	
	private void mset(String[] keys,Jedis jedis){
		Pipeline pipe = jedis.pipelined();
		jedis.mset(keys);
		for(int i=0;i<keys.length;i++){
			jedis.expire(keys[0], this.expire);
		}
		pipe.sync();
	}
	
	
	@Override
	public Map<String, V> multiGets(List<String> keys) {
		Jedis jedis = null;
		Map<String,V> map = new HashMap<String,V>();
		if(StringUtils.isEmpty(keys)){
			return map;
		}
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			List<String> values = jedis.mget(getKeys(keys));
			V value = null;
			for(String str:values){
				if(!StringUtils.isEmpty(str)){
					value = JsonUtil.toBean(str, this.clazz);
					map.put(key(value), value);
				}
			}
			List<String> list = new ArrayList<String>();
			for(String key:keys){
				if(map.get(key) == null){
					list.add(key);
				}
			}
			if(StringUtils.isEmpty(list)) return map;
			Map<String,V> mapValues = this.onAbsents(keys);
			if(StringUtils.isEmpty(mapValues)) return map;
			map.putAll(mapValues);
			String[] kvs = new String[list.size() * 2];
			int index = 0;
			for(String key:mapValues.keySet()){
				kvs[index++] = key;
				kvs[index++] = JsonUtil.toJsonStr(mapValues.get(key));
			}
			mset(kvs, jedis);
		}catch(JedisConnectionException e){
			e.printStackTrace();
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return null;
	}
	
	protected String key(V v) {
		throw new UnsupportedOperationException();
	}
	
	protected Map<String, V> onAbsents(List<String> keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void multiDels(List<String> keys) {
		Jedis jedis = null;
		if(StringUtils.isEmpty(keys)) {
			return ;
		}
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			Pipeline p = jedis.pipelined();
			String [] tmpKeys = new String[keys.size()];
			tmpKeys = keys.toArray(tmpKeys);
			p.del(tmpKeys);
			p.sync();
		}
		catch(JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
		}
		catch(Throwable th) {
			th.printStackTrace();
		}
		finally {
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
	}

}
