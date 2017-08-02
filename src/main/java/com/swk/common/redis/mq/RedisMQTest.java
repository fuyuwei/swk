package com.swk.common.redis.mq;

public class RedisMQTest {

	public static void main(String[] args) {
		String key = "sunwukong";
		RedisMQPruducer.sendMessage(key, "hello,redis");
		RedisMQConsumer.consumMessage(key);
	}
}
