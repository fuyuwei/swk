package com.swk.common.redis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.swk.util.PropertiesUtil;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 初始化redis配置信息
 * http://www.codeweblog.com/jedis-returnresource%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9/
 * @author fuyuwei
 */
public class RedisConfiguration {
	
	private static volatile Map<String,JedisPool> pools;
	
	/**
	 * 根据redis配置信息初始化redis连接池
	 */
	public static void initJedisPool(){
		String redisIds = PropertiesUtil.getString("redis.redisId", "");
		if(StringUtils.isEmpty(redisIds)) return;
		String[] ids = redisIds.split(",");
		Map<Integer,JedisPool> caches = new HashMap<Integer,JedisPool>();
		for(String id:ids){
			int maxActive = PropertiesUtil.getInt("redis.redis"+id+".maxActive", 20);
			int maxIdle = PropertiesUtil.getInt("redis.redis"+id+".maxIdle", 5);
			JedisPoolConfig config = newJedisConfig(maxActive, maxIdle);
			JedisPool pool = null;
			String pwd = PropertiesUtil.getString("redis.redis"+id+".password", "");
			String ip = PropertiesUtil.getString("redis.redis"+id+".ip", "localhost");
			int port = PropertiesUtil.getInt("redis.redis"+id+".port",6379);
			int timeout = 3000;
			if(StringUtils.isEmpty(pwd)){
				pool = new JedisPool(config,ip,port,timeout);
			}else{
				pool = new JedisPool(config,ip,port,timeout,pwd);
			}
			caches.put(Integer.valueOf(id), pool);
		}
		String cacheNames = PropertiesUtil.getString("redis.cachenames", "");
		String[] names = cacheNames.split(",");
		Map<String,JedisPool> poolMap = new HashMap<String,JedisPool>();
		int redisId = 0;
		for(String name : names){
			redisId = PropertiesUtil.getInt("redis."+name+".redisId", -1);
			if(!caches.containsKey(redisId)) 
				throw new RuntimeException("no redisId found "+redisId);
			poolMap.put(name, caches.get(redisId));
		}
		pools = poolMap;
		caches.clear();
	}
	
	/**
	 * 初始化jedisPool
	 * @param maxActive
	 * @param maxIdle
	 * 
	 * @return
	 */
	private static JedisPoolConfig newJedisConfig(int maxActive,int maxIdle){
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted
		config.setMaxTotal(maxActive);
		// 控制一个pool最多有多少个状态为idle(空闲)的jedis实例
		config.setMaxIdle(maxIdle);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),超时就抛异常,小于零:阻塞不确定的时间,默认-1
		config.setMaxWaitMillis(3000);
		// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		config.setBlockWhenExhausted(true);
		// 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
		// 在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		// 在空闲时检查有效性, 默认false
		config.setTestWhileIdle(true);
		return config;
	}
}
