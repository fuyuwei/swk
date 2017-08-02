package com.swk.common.redis.mq;

import java.util.UUID;

import com.swk.common.redis.RedisKJsonListCache;

/**
 * 利用redis做队列,我们采用的是redis中list的push和pop操作; 
 * 队列遵循FIFO原则，redis中的lpush(头入)rpop(尾出)或rpush(尾入)lpop(头出)可以满足要求
 * @author fuyuwei
 */
public class RedisMQPruducer {

	public static void sendMessage(String key,String content){
		String messageId = UUID.randomUUID().toString().replace("-", "");
		Message message = new Message(messageId,content);
		RedisKJsonListCache<Message> cache = new RedisKJsonListCache<Message>(Message.class,"sunwukong",1800);
		cache.lpush(key, message);
	}
}
