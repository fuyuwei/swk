package com.swk.common.redis;

public interface RedisKVSet<K, V> extends RedisKV<K, V> {

	public void set(K key,V value);
}
