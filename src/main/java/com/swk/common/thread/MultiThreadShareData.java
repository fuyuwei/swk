package com.swk.common.thread;

public class MultiThreadShareData {

	public static void main(String[] args) {
//		ShareData1 data2 = new ShareData1();
//		new Thread(new MyRunnable1(data2)).start();
//		new Thread(new MyRunnable2(data2)).start();
		
		final ShareData1 data1 = new ShareData1();
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					data1.decrement();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					data1.increment();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();

	}

}
	
	class MyRunnable1 implements Runnable{
		private ShareData1 data1;
		public MyRunnable1(ShareData1 data1){
			this.data1 = data1;
		}
		public void run() {
			data1.decrement();
			
		}
	}
	
	class MyRunnable2 implements Runnable{
		private ShareData1 data1;
		public MyRunnable2(ShareData1 data1){
			this.data1 = data1;
		}
		public void run() {
			data1.increment();
		}
	}

	class ShareData1 {
		
		private int j = 0;
		public synchronized void increment(){
			j++;
			System.out.println("increment:"+j);
		}
		
		public synchronized void decrement(){
			j--;
			System.out.println("decrement:"+j);
		}
	}