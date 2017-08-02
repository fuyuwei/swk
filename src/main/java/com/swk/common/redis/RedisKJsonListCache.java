package com.swk.common.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.swk.common.redis.mq.Message;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisKJsonListCache<V> implements RedisKList<String, V>{
	
	private final ObjectSerializer<String, V> valueSerializer;
	
	private final Class<V> clz;
	
	private final String cacheName;
	
	private final int expretSec;
	
	public RedisKJsonListCache( Class<V> clz, String cacheName, int expireSec) {
		this.valueSerializer = new JsonSerializer<V>();
		this.clz = clz;
		this.cacheName = cacheName;
		this.expretSec = expireSec;
	}
	
	/**
	 * 队列头入队
	 * @param key
	 * @param content
	 */
	public void lpush(String key,V message){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			jedis.lpush(key, valueSerializer.serialize(message));
		}catch(JedisConnectionException e){
			e.printStackTrace();
			broken = true;
		}catch(Throwable th){
			th.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName,jedis,broken);
		}
	}
	
	/**
	 * 对列尾出对
	 * @param key
	 */
	public V rpop(String key){
		Jedis jedis = null;
		boolean broken = false;
		try{
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String value = jedis.rpop(key);
			return valueSerializer.deserialize(value,this.clz);
		}catch(JedisConnectionException e){
			e.printStackTrace();
			broken = true;
		}catch(Throwable th){
			th.printStackTrace();
		}finally{
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return null;
	}

	private List<String> serialize(List<V> list) {
		if(StringUtils.isEmpty(list)) return new ArrayList<String>(0);
		List<String> retList = new ArrayList<String>(list.size());
		for(V v:list) {
			retList.add(valueSerializer.serialize(v));
		}
		return retList;
	}
	
	private List<V> deserialize(List<String> list) {
		if(StringUtils.isEmpty(list)) return new ArrayList<V>(0);
		List<V> retList = new ArrayList<V>(list.size());
		for(String v:list) {
			retList.add(valueSerializer.deserialize(v, clz));
		}
		return retList;
		
	}
	
	private void setValue(Jedis jedis, String bkey, String[]value) {
		Pipeline p = jedis.pipelined();
		p.del(bkey);
		p.rpush(bkey, value);
		p.expire(bkey, this.expretSec);
		p.sync();
	}
	
	@Override
	public List<V> get(String key) {
		Jedis jedis = null;
		boolean broken = false;
		List<V> retList = new ArrayList<V>(0);
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String bkey = key;
			long len = jedis.llen(bkey);
			List<String> list = jedis.lrange(bkey, 0, len-1);
			if(StringUtils.isEmpty(list)) {
				retList = this.onAbsent(key);
				list = serialize(retList);
				if(!StringUtils.isEmpty(list)) {
					String [] strs = new String[list.size()];
					list.toArray(strs);
					setValue(jedis, bkey, strs);
				}
			}
			else {
				retList = deserialize(list);
			}
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
		return retList;
	}
	
	@Override
	public List<V> getPara(String key,Object obj) {
		Jedis jedis = null;
		
		boolean broken = false;
		List<V> retList = new ArrayList<V>(0);
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			String bkey = key;
			long len = jedis.llen(bkey);
			List<String> list = jedis.lrange(bkey, 0, len-1);
			if(StringUtils.isEmpty(list)) {
				retList = this.onAbsentCompare(key,obj);
				list = serialize(retList);
				if(!StringUtils.isEmpty(list)) {
					String [] strs = new String[list.size()];
					list.toArray(strs);
					setValue(jedis, bkey, strs);
				}
			}
			else {
				retList = deserialize(list);
			}
		}
		catch(JedisConnectionException e) {
			broken = true;
		}
		catch(Throwable th) {
		}
		finally {
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		return retList;
	}


	@Override
	public void del(String key) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			jedis.del(key);
		}
		catch(JedisConnectionException e) {
			e.printStackTrace();
			broken = true;
		}
		catch(Throwable th) {
			th.printStackTrace();
		}
		finally {
			RedisConfiguration.returnInstance(this.cacheName, jedis, broken);
		}
		
	}

	@Override
	public int size(String key) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			long size = jedis.llen(key);
			if(size > 0) return (int)size;
			else {
				List<V> list = this.get(key);
				return list.size();
			}
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
		return 0;
	}


	/**
	 * 当key在缓存不存在的时候，从持久介质获取，禁止返回空
	 * @param key
	 * @return
	 */
	protected List<V> onAbsent(String key) {
		return new ArrayList<V>(0);
	}
	/**
	 * 当key在缓存不存在的时候，从持久介质获取，禁止返回空
	 * @param key
	 * @return
	 */
	protected List<V> onAbsentCompare(String key,Object obj) {
		return new ArrayList<V>(0);
	}
	
	

	public Map<String, Integer> msize(List<String> keys) {
		Jedis jedis = null;
		boolean broken = false;
		Map<String, Integer> retMap = new HashMap<String, Integer>();
		if(StringUtils.isEmpty(keys)) return retMap;
		try {
			jedis = RedisConfiguration.getRedisInstance(this.cacheName);
			_msize(jedis, keys, retMap);
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
		return retMap;
	}
	
	
	private void _msize(Jedis jedis, List<String> keys, Map<String, Integer> retMap) {
		Pipeline p = jedis.pipelined();
		for(String key : keys) {
			p.exists(key);
		}
		List<Object> exists = p.syncAndReturnAll();
		boolean allExist = true;
		for(Object exist : exists) {
			if(!(Boolean) exist) {
				allExist = false;
				break;
			}
		}
		if(allExist) {
			p = jedis.pipelined();
			for(String key : keys) {
				p.llen(key);
			}
			List<Object> lens = p.syncAndReturnAll();
			if(lens.size()==keys.size()) {
				for(int i=0;i<keys.size() && i<lens.size();i++) {
					retMap.put(keys.get(i), ((Long)(lens.get(i))).intValue());
				}
			}
		}
		if(StringUtils.isEmpty(retMap)) {
			for(String key : keys) {
				retMap.put(key, size(key));
			}
		}
	}
	
}
