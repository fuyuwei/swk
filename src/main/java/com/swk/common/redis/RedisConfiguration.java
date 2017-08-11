package com.swk.common.redis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.swk.util.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 初始化redis配置信息
 * @volatile 使用场景描述：
 * 用volatile修改JedisPool的Map集合，在多线程环境中，当前现在使用redis连接池时，如果其他线程对redis连接进行修改，例如将连接返回到连接池等
 * 当前线程池能获取到最新的连接池信息
 * http://www.codeweblog.com/jedis-returnresource%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9/
 * @author fuyuwei
 */
public class RedisConfiguration {
	
	private static volatile Map<String,JedisPool> pools;
	
	private static volatile JedisPool jedisPool;
	
	/**
	 * 针对默认只有一个redis实例
	 * @return
	 */
	public static Jedis getRedisInstance(){
		if(jedisPool == null){
			synchronized(RedisConfiguration.class){
				if(jedisPool == null){
					int maxActive = PropertiesUtil.getInt("redis.pool.maxActive", 20);
					int maxIdle = PropertiesUtil.getInt("redis.pool.maxIdle", 5);
					JedisPoolConfig config = newJedisConfig(maxActive, maxIdle);
					String pwd = PropertiesUtil.getString("redis.password", "");
					String ip = PropertiesUtil.getString("redis.ip", "192.168.1.00");
					int port = PropertiesUtil.getInt("redis.port", 6379);
					int timeout = 3000;
					if(StringUtils.isEmpty(pwd)){
						jedisPool = new JedisPool(config,ip,port,timeout);
					}else{
						jedisPool = new JedisPool(config,ip,port,timeout,pwd);
					}
				}
			}
		}
		return jedisPool.getResource();
	}
	
	public static void returnInstance(Jedis jedis,boolean isBrokenConn){
		if(jedis != null){
			if(isBrokenConn){
				jedisPool.returnBrokenResource(jedis);
			}else{
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	/**
	 * 针对配置了多个redis实例，根据配置的redis名称初始化redis连接池
	 * @param cacheName
	 * @return
	 */
	public static Jedis getRedisInstance(String cacheName){
		initCaches();
		return pools.get(cacheName).getResource();
	}
	
	public static void returnInstance(String cacheName,Jedis jedis,boolean broken){
		initCaches();
		if(jedis != null){
			// 异常情况
			if(broken){
				pools.get(cacheName).returnBrokenResource(jedis);
			}
			else{
				pools.get(cacheName).returnResource(jedis);
			}
		}
		
	}
	public static void initCaches(){
		// 这里pools用volatile修饰，JVM的happen-before原则
		if(pools == null){
			synchronized(RedisConfiguration.class){
				if(pools == null){
					initJedisPool();
				}
			}
		}
	}
	
	/**
	 * 对于有多个redis实例的
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
