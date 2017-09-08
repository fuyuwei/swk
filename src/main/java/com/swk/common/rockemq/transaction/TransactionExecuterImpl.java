package com.swk.common.rockemq.transaction;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

@SuppressWarnings("unchecked")
public class TransactionExecuterImpl implements LocalTransactionExecuter {

	@Override
	public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
		try {
			JSONObject messageBody = JSONObject.parseObject(new String(msg.getBody()), JSONObject.class);
			Map<String,Object> mapArgs = (Map<String,Object>)arg;
			System.out.println("messageBody:"+messageBody+"mapArgs:"+mapArgs);
			// 处理业务逻辑
			return LocalTransactionState.COMMIT_MESSAGE;
		} catch (Exception e) {
			e.printStackTrace();
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
	}

}
