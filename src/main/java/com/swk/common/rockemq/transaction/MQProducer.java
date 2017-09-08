package com.swk.common.rockemq.transaction;

import java.util.Map;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;

public class MQProducer {

	private final String GROUP_NAME = "transaction-pay";
	
	private final String NAMESER_ADDR = "";
	
	private TransactionMQProducer producer;
	
	public MQProducer(){
		this.producer = new TransactionMQProducer(GROUP_NAME);
		this.producer.setNamesrvAddr(NAMESER_ADDR);
		this.producer.setCheckThreadPoolMinSize(5);// 事务回查最小并发数
		this.producer.setCheckThreadPoolMaxSize(20);// 事务回查最大并发数
		this.producer.setCheckRequestHoldMax(2000);// 队列数
		this.producer.setTransactionCheckListener(new TransactionCheckListener() {
			@Override
			public LocalTransactionState checkLocalTransactionState(MessageExt arg0) {
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		});
		try {
			this.producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	
	public QueryResult queryMessage(String topic, String key, int maxNum, long begin, long end) throws Exception {
		return this.producer.queryMessage(topic, key, maxNum, begin, end);
	}
	
	public LocalTransactionState check(MessageExt me){
		LocalTransactionState ls = this.producer.getTransactionCheckListener().checkLocalTransactionState(me);
		return ls;
	}
	
	public void sendTransactionMessage(Message message, LocalTransactionExecuter localTransactionExecuter, 
			Map<String, Object> transactionMapArgs) throws Exception {
		TransactionSendResult tsr = this.producer.sendMessageInTransaction(message, localTransactionExecuter, transactionMapArgs);
		System.out.println("send返回内容：" + tsr.toString());
	}
	
	public void shutdown(){
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				producer.shutdown();
			}
		}));
		System.exit(0);
	}
}
