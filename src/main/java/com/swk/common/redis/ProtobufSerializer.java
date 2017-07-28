package com.swk.common.redis;

import com.google.protobuf.GeneratedMessage;

/**
 * protobuf serializer
 * @author fuyuwei
 * @param <V>
 */
public abstract class ProtobufSerializer<V extends GeneratedMessage> implements ObjectSerializer<byte[], V> {

	@Override
	public byte[] serialize(V value) {
		return value.toByteArray();
	}

	@Override
	public abstract V deserialize(byte[] bs, Class<?> clz);

}
