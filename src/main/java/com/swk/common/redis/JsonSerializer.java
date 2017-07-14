package com.swk.common.redis;

import com.swk.util.JsonUtil;

/**
 * json序列器
 * @author fuyuwei
 * @param <V>
 */
public class JsonSerializer<V> implements ObjectSerializer<String,V>{

	@Override
	public String serialize(V value){
		return JsonUtil.toJsonStr(value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V deserialize(String json,Class<?> clazz){
		return (V) JsonUtil.toBean(json, clazz);
	}

}
