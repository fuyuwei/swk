package com.swk.common.redis.mq;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = -3722341074031379434L;

	private String messageId;
	
	private String content;
	

	public Message() {
		super();
	}

	public Message(String messageId, String content) {
		super();
		this.messageId = messageId;
		this.content = content;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", content=" + content + "]";
	}
	
}
