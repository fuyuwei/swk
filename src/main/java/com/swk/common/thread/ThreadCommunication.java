package com.swk.common.thread;

/**
 * 线程之间的通信
 * 场景：孙悟空先吃5个桃，然后张三丰吃10个桃，这样循环20次
 * @author fuyuwei
 *
 */
public class ThreadCommunication {
	
	public static void main(String[] args) {
		final ThreadAlternate alternate = new ThreadAlternate();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=1;i<=20;i++){
					alternate.runSwk(i);
				}
			}
		}).start();
		
		for(int i=1;i<=20;i++){
			alternate.runZhsf(i);
		}
	}

}

class ThreadAlternate {
	private boolean isSwk = true;

	public synchronized void runSwk(int loop) {
		while (!isSwk) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 1; i <= 5; i++) {
			System.out.println("第" + loop + "回合，孙悟空开始吃" + i + "个桃");
		}
		this.isSwk = false;
		this.notify();
	}

	public synchronized void runZhsf(int loop) {
		while (isSwk) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 1; i <= 10; i++) {
			System.out.println("第" + loop + "回合，张三丰开始吃" + i + "个桃");
		}
		this.isSwk = true;
		this.notify();
	}
}
