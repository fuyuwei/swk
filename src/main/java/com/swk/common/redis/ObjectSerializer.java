package com.swk.common.redis;

/**
 * 对象序列化生成器
 * @param <S> 
 * @param <V> 
 */
public interface ObjectSerializer<S, V> {
	
	public S serialize(V value);
	
	public V deserialize(S bs, Class<?> clz);
	
}
