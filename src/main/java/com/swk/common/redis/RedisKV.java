package com.swk.common.redis;

/**
 * 只提供删除和查询（如果查询不到会查询数据库并加载到redis）
 * @author fuyuwei
 * @param <K>
 * @param <V>
 */
public interface RedisKV<K, V> {
	
	public V get(K key);
	
	public int del(K key);
}
