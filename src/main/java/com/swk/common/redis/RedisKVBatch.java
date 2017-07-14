package com.swk.common.redis;

import java.util.List;
import java.util.Map;

public interface RedisKVBatch<K,V> extends RedisKV<K,V> {

	public Map<K,V> multiGets(List<K> keys);
	
	public void multiDels(List<K> keys);
}
