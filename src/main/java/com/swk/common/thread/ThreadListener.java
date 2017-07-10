package com.swk.common.thread;

/**
 * 实现多线程的两种方式
 * @author fuyuwei
 */
public class ThreadListener {

	/**
	 * 这个方法使用场景例如：
	 * 我们用redis的list的lpush进队里和rpop出队列
	 * producer:lpush
	 * consumer:rpop可以通过init来实现监听
	 * 需要注意的是使用了while(true)尽量配合Thead.sleep防止CPU利用率过高，下面的init_方法更佳，更加体现出了面向对象的编程
	 * Thread.sleep期间不占用CPU资源，睡醒之后重新获取CPU资源
	 */
	public static void init(){
		Thread thread = new Thread(){
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName());
				}
			}
			
		};
		thread.start();
	}
	
	public static void init_(){
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName());
				}
			}
			
		});
		thread.start();
	}
	
	public static void main(String[] args) {
		init();
		init_();
	}
}
