package com.swk.common.thread;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器timer的几种调度方式，底层是TaskQueue实现，Timer线程安全
 * 使用场景：通过定时器我们可以定时把一些热点数据刷新到内存中
 * @author fuyuwei
 */
public class ThreadTimer {

	/**
	 * 延迟Tms后触发
	 */
	public static void schedule1(){
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("schedule1 is executed.");
			}
		}, 1000);
	}
	
	/**
	 * 延迟Tms后触发，之后每T2ms触发
	 */
	public static void schedule2(){
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("schedule2 is executed.");
			}
		}, 1000, 2000);
	}
	
	private static int flag;
	/**
	 * 延迟Tms之后，交替Tms执行一次，T+Tms之后之执行一次
	 */
	public static void schedule3(){
		class Timer_ extends TimerTask{
			@Override
			public void run() {
				System.out.println("schedule3 is executed ervery "+flag);
				flag = (flag+1)%2;
				new Timer().schedule(new Timer_(), 1000+1000*flag);
			}
		}
		new Timer().schedule(new Timer_(), 2000);
	}
	
	/**
	 * 每天T时间点执行
	 */
	public static void schedule4(){
		String timeStr = "2017-07-10 23:06:30";
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
		try {
			date = fmt.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("schedule4 is executed.");
			}
		}, date, 24*60*60);
	}
	
	public static void main(String[] args) {
		schedule1();
		schedule2();
		schedule3();
		schedule4();
	}
}
