package com.swk.common.rockemq.transaction;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

public class MQConsumer {

	private final String GROUP_NAME = "transaction-balance";
	private final String NAMESRV_ADDR = "";
	private DefaultMQPushConsumer consumer;
	
	public MQConsumer(){
		try {
			this.consumer = new DefaultMQPushConsumer(GROUP_NAME);
			this.consumer.setNamesrvAddr(NAMESRV_ADDR);
			this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			this.consumer.subscribe("pay", "*");
			this.consumer.registerMessageListener(new Listener());
			this.consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	
	public QueryResult queryMessage(String topic,String key,int maxNum,long begin,long end) throws Exception{
		return this.consumer.queryMessage(topic, key, maxNum, begin, end);
	}
	
	class Listener implements MessageListenerConcurrently{

		@Override
		public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
				ConsumeConcurrentlyContext consumeconcurrentlycontext) {
			MessageExt msg = msgs.get(0);
			try {
//				String topic = msg.getTopic();
				// message body
				JSONObject messageBody = JSONObject.parseObject(new String(msg.getBody(),"utf-8"),JSONObject.class );
//				String tags = msg.getTags();
				String keys = msg.getKeys();
				String userId = messageBody.getString("userId");
				
				// 处理业务逻辑
				System.out.println("consumer收到消息, keys : " + keys + ", body : " + new String(msg.getBody(), "utf-8")+userId);
			} catch (Exception e) {
				e.printStackTrace();
				//重试次数为3情况 
				if(msg.getReconsumeTimes() == 3){
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					//记录日志
				}
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
			
			return null;
		}
		
	}
	
	
}
