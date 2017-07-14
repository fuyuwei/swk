package com.swk.common.redis;

import java.util.List;

public interface RedisKList<K,V> {

	public List<V> get(K key);
	
	public void del(K key);
	
	public int size(K key);
	
}
